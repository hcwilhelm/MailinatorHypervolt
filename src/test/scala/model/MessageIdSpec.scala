package model

import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.test._

import java.util.UUID


object MessageIdSpec extends ZIOSpecDefault {

  private val uuidString = "a039101e-1527-4128-bea9-fd8e95e91980"
  private val uuidJson = s"\"$uuidString\""

  def spec = suite("MessageIdSpec")(
    test("Encode MessageId to json") {
      for {
        json <- ZIO.succeed(MessageId(UUID.fromString(uuidString)).toJson)
      } yield assertTrue(json == uuidJson)
    },
    test("Decode MessageId") {
      for {
        result <- ZIO.succeed(uuidJson.fromJson[MessageId])
      } yield assertTrue(result == Right(MessageId(UUID.fromString(uuidString))))
    }
  )
}
