package repos

import io.getquill.jdbczio.Quill
import io.getquill.{ H2ZioJdbcContext, SnakeCase }
import java.time.Instant
import java.util.UUID
import javax.sql.DataSource
import model._
import zio._

class H2MessageRepo(ds: DataSource) extends MessageRepo {

  private val ctx     = new H2ZioJdbcContext(SnakeCase)
  private val dsLayer = ZLayer.succeed(ds)

  import H2MessageRepo.MessageTable
  import ctx._

  override def insert(mailbox: Mailbox, message: MessageRequest): Task[MessageId] = {
    for {
      uuid      <- zio.Random.nextUUID
      createdAt <- zio.Clock.instant
      _         <- ctx.run {
                     query[MessageTable]
                       .insertValue(
                         lift(
                           MessageTable(
                             uuid,
                             mailbox.name,
                             message.sender.value,
                             message.subject.map(_.value),
                             message.message.map(_.value),
                             createdAt
                           )
                         )
                       )
                   }
    } yield MessageId(uuid)
  }.provide(dsLayer)

  override def getById(messageId: MessageId): Task[Option[Message]] =
    ctx
      .run {
        query[MessageTable]
          .filter(_.id == lift(messageId.value))
          .map(row =>
            Message(
              MessageId(row.id),
              Mailbox(row.mailbox),
              Sender(row.sender),
              row.subject.map(Subject(_)),
              row.message.map(MessageContent(_)),
              row.createdAt
            )
          )
      }
      .provide(dsLayer)
      .map(_.headOption)

  override def getByMailbox(mailbox: Mailbox): Task[List[Message]] =
    ctx
      .run {
        query[MessageTable]
          .filter(_.mailbox == lift(mailbox.name))
          .map(row =>
            Message(
              MessageId(row.id),
              Mailbox(row.mailbox),
              Sender(row.sender),
              row.subject.map(Subject(_)),
              row.message.map(MessageContent(_)),
              row.createdAt
            )
          )
      }
      .provide(dsLayer)

  override def deleteById(messageId: MessageId): Task[Unit] =
    ctx
      .run {
        query[MessageTable].filter(_.id == lift(messageId.value)).delete
      }
      .provide(dsLayer)
      .unit

  override def messages: Task[List[Message]] =
    ctx
      .run {
        query[MessageTable]
          .sortBy(_.createdAt)
          .map(row =>
            Message(
              MessageId(row.id),
              Mailbox(row.mailbox),
              Sender(row.sender),
              row.subject.map(Subject(_)),
              row.message.map(MessageContent(_)),
              row.createdAt
            )
          )
      }
      .provide(dsLayer)

  override def deleteMessagesOlderThan(timestamp: Instant): Task[Unit] =
    ctx
      .run {
        quote {
          query[MessageTable].filter(_.createdAt < lift(timestamp)).delete
        }
      }
      .provide(dsLayer)
      .unit

  implicit final class InstantOps(i: Instant) {
    def >(other: Instant) = quote(sql"$i > $other".asCondition)

    def <(other: Instant) = quote(sql"$i < $other".asCondition)

    def ===(other: Instant) = quote(sql"$i == $other".asCondition)

    def >=(right: Instant) = quote(sql"$i >= $right".asCondition)

    def <=(other: Instant) = quote(sql"$i <= $other".asCondition)

    def between(min: Instant, max: Instant) = quote(sql"$i BETWEEN $min AND $max".asCondition)
  }
}

object H2MessageRepo {
  final case class MessageTable(
    id: UUID,
    mailbox: String,
    sender: String,
    subject: Option[String],
    message: Option[String],
    createdAt: Instant
  )

  def layer: ZLayer[Any, Throwable, H2MessageRepo] =
    Quill.DataSource.fromPrefix("MailinatorApp") >>> ZLayer.fromFunction(new H2MessageRepo(_))
}
