package repos

import io.getquill.jdbczio.Quill
import io.getquill.{ H2ZioJdbcContext, Ord, SnakeCase }
import java.time.Instant
import javax.sql.DataSource
import model.Mailbox
import zio._

class H2MailboxRepo(ds: DataSource) extends MailboxRepo {

  private val ctx     = new H2ZioJdbcContext(SnakeCase)
  private val dsLayer = ZLayer.succeed(ds)

  import H2MailboxRepo.MailboxTable
  import ctx._

  override def insert(mailbox: Mailbox): Task[Unit] = {
    for {
      createdAt <- zio.Clock.instant
      _         <- ctx
                     .run {
                       query[MailboxTable]
                         .insertValue(lift(MailboxTable(mailbox.name, createdAt)))
                         .onConflictIgnore
                     }
    } yield ()
  }.provide(dsLayer)

  override def delete(mailbox: Mailbox): Task[Unit] =
    ctx
      .run {
        query[MailboxTable]
          .filter(_.name == lift(mailbox.name))
          .delete
      }
      .provide(dsLayer)
      .unit

  override def mailboxes: Task[List[Mailbox]] =
    ctx
      .run {
        query[MailboxTable]
          .sortBy(_.createdAt)(Ord.desc)
          .map(row => Mailbox.apply(row.name))
      }
      .provide(dsLayer)
}

object H2MailboxRepo {
  final case class MailboxTable(name: String, createdAt: Instant)

  def layer: ZLayer[Any, Throwable, H2MailboxRepo] =
    Quill.DataSource.fromPrefix("MailinatorApp") >>> ZLayer.fromFunction(new H2MailboxRepo(_))
}
