package model

import zio.json.{ DeriveJsonDecoder, DeriveJsonEncoder }

final case class MessageRequest(
  sender: Sender,
  subject: Option[Subject],
  message: Option[MessageContent]
)

object MessageRequest {
  implicit val messageEncoder = DeriveJsonEncoder.gen[MessageRequest]
  implicit val messageDecoder = DeriveJsonDecoder.gen[MessageRequest]
}
