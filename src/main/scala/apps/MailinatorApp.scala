package apps

import java.util.UUID
import model.{ Mailbox, MessageId, MessageRequest }
import repos.{ MailboxRepo, MessageRepo }
import zhttp.http._
import zio._
import zio.json._

object MailinatorApp {

  def apply() =
    Http.collectZIO[Request] {

      // POST /mailboxes -d '"FooBar"' | POST /mailboxes -> Create Random mailbox
      case req @ (Method.POST -> !! / "mailboxes")                     => for {
          body     <- req.bodyAsString
          mailbox  <- if (body.isEmpty)
                        Random.nextString(12).map(name => Right(Mailbox(name)))
                      else
                        ZIO.succeed(body.fromJson[Mailbox])
          response <- mailbox match {
                        case Left(error)    =>
                          ZIO.debug(s"Failed to parse input: $error").as(Response.status(Status.BadRequest))
                        case Right(mailbox) =>
                          MailboxRepo.insert(mailbox).as(Response.json(mailbox.toJson))
                      }
        } yield response

      // GET /mailboxes
      case (Method.GET -> !! / "mailboxes")                            => for {
          mailboxes <- MailboxRepo.mailboxes
        } yield Response.json(mailboxes.toJson)

      // POST /mailboxes/{email_address}/messages -d `{message_model}`
      case req @ (Method.POST -> !! / "mailboxes" / name / "messages") => for {
          messageRequest <- req.bodyAsString.map(_.fromJson[MessageRequest])
          response       <- messageRequest match {
                              case Left(error)           =>
                                ZIO.debug(s"Failed to parse input: $error").as(Response.status(Status.BadRequest))
                              case Right(messageRequest) =>
                                MessageRepo.insert(Mailbox(name), messageRequest).as(Response.status(Status.Created))
                            }
        } yield response

      // GET /mailboxes/{email address}/messages
      case (Method.GET -> !! / "mailbox" / name / "messages")          => for {
          messages <- MessageRepo.getByMailbox(Mailbox(name))
        } yield Response.json(messages.toJson)

      // GET /mailboxes/{email address}/messages/{message id}
      case (Method.GET -> !! / "mailboxes" / name / "messages" / id)   => for {
          message <- MessageRepo.getById(MessageId(UUID.fromString(id)))
        } yield Response.json(message.toJson)

      // DELETE /mailboxes/{email address}
      case (Method.DELETE -> !! / "mailboxes" / name)                  => for {
          _ <- MailboxRepo.delete(Mailbox(name))
        } yield Response.status(Status.Ok)

      // DELETE /mailboxes/{email address}/messages/{message id}
      case (Method.DELETE -> !! / "mailboxes" / _ / "messages" / id)   => for {
          _ <- MessageRepo.deleteById(MessageId(UUID.fromString(id)))
        } yield Response.status(Status.Ok)
    }
}
