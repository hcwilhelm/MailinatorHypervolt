package model

import java.time.Instant
import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder }

final case class Message(
  id: MessageId,
  mailbox: Mailbox,
  sender: Sender,
  subject: Option[Subject],
  message: Option[MessageContent],
  createdAt: Instant
)

object Message {
  implicit val messageEncoder = DeriveJsonEncoder.gen[Message]
  implicit val messageDecoder = DeriveJsonDecoder.gen[Message]
}
