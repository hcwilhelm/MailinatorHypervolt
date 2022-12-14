package model

import io.getquill.MappedEncoding
import zio.json.{ JsonDecoder, JsonEncoder }

final case class Sender(v: String)

object Sender {
  implicit val senderEncoder = JsonEncoder[String].contramap[Sender](_.v)
  implicit val senderDecoder = JsonDecoder[String].map(Sender(_))
  implicit val dBEncoder     = MappedEncoding[String, Sender](Sender(_))
}
