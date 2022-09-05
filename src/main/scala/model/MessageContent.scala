package model

import io.getquill.MappedEncoding
import zio.json.{ JsonDecoder, JsonEncoder }

final case class MessageContent(value: String)

object MessageContent {
  implicit val messageContentEncoder = JsonEncoder[String].contramap[MessageContent](_.value)
  implicit val messageContentDecoder = JsonDecoder[String].map(MessageContent(_))
  implicit val dBEncoder             = MappedEncoding[String, MessageContent](MessageContent(_))
}
