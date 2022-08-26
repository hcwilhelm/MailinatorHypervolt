package apps

import model.Mailbox
import repos.MailboxRepo
import zhttp.http._
import zio._
import zio.json._

object MailinatorApp {

  def apply() =
    Http.collectZIO[Request] {

      // POST /mailboxes -d '{"name": "FooBar"}' | POST /mailboxes -> Create Random mailbox
      case req@(Method.POST -> !! / "mailboxes") => for {
        body <- req.bodyAsString
        mailbox <- if (body.isEmpty) Random.nextString(12).map(name => Right(Mailbox(name))) else ZIO.succeed(body.fromJson[Mailbox])
        response <- mailbox match {
          case Left(error) => ZIO.debug(s"Failed to parse input: $error").as(Response.status(Status.BadRequest))
          case Right(mailbox) => MailboxRepo.insert(mailbox).as(Response.json(mailbox.toJson))
        }
      } yield response

      // GET /mailboxes
      case req@(Method.GET -> !! / "mailboxes") => for {
        mailboxes <- MailboxRepo.mailboxes
      } yield Response.json(mailboxes.toJson)
    }
}
