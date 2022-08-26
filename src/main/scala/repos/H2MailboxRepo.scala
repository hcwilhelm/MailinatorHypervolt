package repos

import io.getquill.jdbczio.Quill
import io.getquill.{H2ZioJdbcContext, SnakeCase}
import model.Mailbox
import zio._

import java.time.Instant
import javax.sql.DataSource

class H2MailboxRepo(ds: DataSource) extends MailboxRepo {

  private val ctx = new H2ZioJdbcContext(SnakeCase)
  private val dsLayer = ZLayer.succeed(ds)

  import H2MailboxRepo.MailboxTable
  import ctx._

  override def insert(mailbox: Mailbox): Task[Unit] =
    ctx.run {
      query[MailboxTable]
        .insertValue(lift(MailboxTable(mailbox.name, Instant.now())))
        .onConflictIgnore
    }.provide(dsLayer).unit

  override def delete(mailbox: Mailbox): Task[Unit] =
    ctx.run {
      query[MailboxTable]
        .filter(row => row.name == lift(mailbox.name))
        .delete
    }.provide(dsLayer).unit

  override def mailboxes: Task[List[Mailbox]] =
    ctx.run {
      query[MailboxTable]
        .sortBy(_.createdAt)
        .map(row => Mailbox(row.name))
    }.provide(dsLayer)
}

object H2MailboxRepo {
  final case class MailboxTable(name: String, createdAt: Instant)

  def layer: ZLayer[Any, Throwable, H2MailboxRepo] =
    Quill.DataSource.fromPrefix("MailinatorApp") >>> ZLayer.fromFunction(new H2MailboxRepo(_))
}
