package model

import zio.json.{DecoderOps, EncoderOps}
import zio.test._
import zio._

object MailboxSpec extends ZIOSpecDefault {

  private val mailbox = Mailbox("foo")
  private val mailboxJson = s"\"${mailbox.name}\""

  def spec = suite("MailBoxSpec")(
    test("Encode Mailbox to json") {
      for {
        json      <- ZIO.succeed(mailbox.toJson)
      } yield assertTrue(json == mailboxJson)
    },
    test("Decode Mailbox from json") {
      for {
        result <- ZIO.succeed(mailboxJson.fromJson[Mailbox])
      } yield assertTrue(result == Right(mailbox))
    }
  )
}
