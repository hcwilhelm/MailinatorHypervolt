package model

import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.test._

object MessageContentSpec extends ZIOSpecDefault {

  private val content = MessageContent("fooBar")
  private val contentJson = s"\"${content.v}\""

  def spec = suite("MessageContentSpec")(
    test("Encode MessageContent to json") {
      for {
        json <- ZIO.succeed(content.toJson)
      } yield assertTrue(json == contentJson)
    },
    test("Decode MessageContent") {
      for {
        result <- ZIO.succeed(contentJson.fromJson[MessageContent])
      } yield assertTrue(result == Right(content))
    }
  )
}
