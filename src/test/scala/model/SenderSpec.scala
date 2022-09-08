package model

import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.test._

object SenderSpec extends ZIOSpecDefault {
  private val sender = Sender("foo")
  private val senderJson = s"\"${sender.v}\""

  def spec = suite("SenderSpec")(
    test("Encode Sender to json") {
      for {
        json <- ZIO.succeed(sender.toJson)
      } yield assertTrue(json == senderJson)
    },
    test("Decode Sender") {
      for {
        result <- ZIO.succeed(senderJson.fromJson[Sender])
      } yield assertTrue(result == Right(sender))
    }
  )

}
