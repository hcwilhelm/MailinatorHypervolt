package model

import model.Message.{MessageId, Sender, Subject}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.UUID

final case class Message(id: MessageId, mailbox: Mailbox, sender: Sender, subject: Option[Subject], message: Option[String])

object Message {

  final case class MessageId(value: UUID)

  final case class Sender(value: String)

  final case class Subject(value: String)

  implicit val messageIdEncoder = JsonEncoder[UUID].contramap[MessageId](_.value)
  implicit val messageIdDecoder = JsonDecoder[UUID].map(MessageId)

  implicit val senderEncoder = JsonEncoder[String].contramap[Sender](_.value)
  implicit val senderDecoder = JsonDecoder[String].map(Sender)

  implicit val subjectEncoder = JsonEncoder[String].contramap[Subject](_.value)
  implicit val subjectDecoder = JsonDecoder[String].map(Subject)

  implicit val messageEncoder = DeriveJsonEncoder.gen[Message]
  implicit val messageDecoder = DeriveJsonDecoder.gen[Message]
}