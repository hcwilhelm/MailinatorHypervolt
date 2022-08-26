package model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

final case class Mailbox(name: String) extends AnyVal

object Mailbox {
  implicit val mailboxEncoder = DeriveJsonEncoder.gen[Mailbox]
  implicit val mailboxDecoder = DeriveJsonDecoder.gen[Mailbox]
}