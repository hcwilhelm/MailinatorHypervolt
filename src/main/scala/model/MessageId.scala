package model

import io.getquill.MappedEncoding
import java.util.UUID
import zio.json.{ JsonDecoder, JsonEncoder }

final case class MessageId(value: UUID)

object MessageId {
  implicit val messageIdEncoder = JsonEncoder[UUID].contramap[MessageId](_.value)
  implicit val messageIdDecoder = JsonDecoder[UUID].map(MessageId(_))
  implicit val dBEncoder        = MappedEncoding[UUID, MessageId](MessageId(_))
}
