package repos

import io.getquill.jdbczio.Quill
import io.getquill.{H2ZioJdbcContext, SnakeCase}
import model.Message
import zio._

import java.time.Instant
import java.util.UUID
import javax.sql.DataSource

class H2MessageRepo(ds: DataSource) extends MessageRepo {

  private val ctx = new H2ZioJdbcContext(SnakeCase)
  private val dsLayer = ZLayer.succeed(ds)

  import H2MessageRepo.MessageTable
  import ctx._

  override def insert(message: Message): Task[Unit] =
    ctx.run {
      query[MessageTable]
        .insertValue(lift(MessageTable(
          message.id.value,
          message.mailbox.name,
          message.sender.value,
          message.subject.map(_.value),
          message.message,
          Instant.now())))
        .onConflictIgnore
    }.provide(dsLayer).unit

  override def getById(messageId: Message.MessageId): Task[Message] = ???

  override def deleteById(messageId: Message.MessageId): Task[Unit] = ???

  override def messages: Task[List[Message]] = ???

  override def deleteMessagesOlderThan(timestamp: Instant): Task[Unit] = ???
}

object H2MessageRepo {
  final case class MessageTable(id: UUID, mailbox: String, sender: String, subject: Option[String], message: Option[String], createdAt: Instant)



  def layer: ZLayer[Any, Throwable, H2MessageRepo] =
    Quill.DataSource.fromPrefix("MailinatorApp") >>> ZLayer.fromFunction(new H2MessageRepo(_))
}