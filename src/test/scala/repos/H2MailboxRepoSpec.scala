package repos

import model.Mailbox
import zio.test.{ZIOSpecDefault, _}
import zio._

object H2MailboxRepoSpec extends ZIOSpecDefault {

  private def moveClock(seconds: Int) = TestClock.adjust(Duration.fromSeconds(seconds))

  def spec = suite("H2MailboxRepoSpec")(
    test("Insert new mailbox") {
      for {
        result <- MailboxRepo.insert(Mailbox("foo"))
      } yield assertTrue(result == ())
    },
    test("Insert same mailbox more than once should not fail") {
      for {
        _ <- MailboxRepo.insert(Mailbox("foo"))
        _ <- moveClock(10)
        _ <- MailboxRepo.insert(Mailbox("foo"))
        result <- MailboxRepo.insert(Mailbox("foo"))
      } yield assertTrue(result == ())
    },
    test("Delete mailbox") {
      for {
        _ <- MailboxRepo.insert(Mailbox("foo"))
        result <- MailboxRepo.delete(Mailbox("foo"))
      } yield assertTrue(result == ())
    },
    test("List mailboxes") {
      for {
        _ <- MailboxRepo.insert(Mailbox("foo"))
        _ <- moveClock(10)
        _ <- MailboxRepo.insert(Mailbox("bar"))
        result <- MailboxRepo.mailboxes
      } yield assertTrue(result == List(Mailbox("bar"), Mailbox("foo")))
    },
  ).provideLayerShared(H2MailboxRepo.layer)
}
