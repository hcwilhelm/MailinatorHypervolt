package repos

import model.Mailbox
import zio._

trait MailboxRepo {
  def insert(mailbox: Mailbox): Task[Unit]

  def delete(mailbox: Mailbox): Task[Unit]

  def mailboxes: Task[List[Mailbox]]
}

object MailboxRepo {
  def insert(mailbox: Mailbox) = ZIO.serviceWithZIO[MailboxRepo](_.insert(mailbox))

  def delete(mailbox: Mailbox) = ZIO.serviceWithZIO[MailboxRepo](_.delete(mailbox))

  def mailboxes = ZIO.serviceWithZIO[MailboxRepo](_.mailboxes)
}


