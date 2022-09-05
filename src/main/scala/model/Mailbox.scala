package model

import io.getquill.MappedEncoding
import zio.json.{ JsonDecoder, JsonEncoder }

final case class Mailbox(name: String)

object Mailbox {
  implicit val messageContentEncoder = JsonEncoder[String].contramap[Mailbox](_.name)
  implicit val messageContentDecoder = JsonDecoder[String].map(Mailbox(_))
  implicit val dBEncoder             = MappedEncoding[String, Mailbox](Mailbox(_))
}
