package model

import zio.ZIO
import zio.json.{DecoderOps, EncoderOps}
import zio.test.{ZIOSpecDefault, assertTrue}

object SubjectSpec extends ZIOSpecDefault {

  private val subject = Subject("foo")
  private val subjectJson = s"\"${subject.value}\""

  def spec = suite("SubjectSpec")(
    test("Encode Subject to json") {
      for {
        json <- ZIO.succeed(subject.toJson)
      } yield assertTrue(json == subjectJson)
    },
    test("Decode Subject") {
      for {
        result <- ZIO.succeed(subjectJson.fromJson[Subject])
      } yield assertTrue(result == Right(subject))
    }
  )
}
