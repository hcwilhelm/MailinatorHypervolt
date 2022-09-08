package model

import zio._
import zio.json.{DecoderOps, EncoderOps}
import zio.test._
import zio.test.ZIOSpecDefault

object MessageRequestSpec extends ZIOSpecDefault {

  private val sender = Sender("bar")
  private val subject = Subject("wtf")
  private val messageContent = MessageContent("A very long message")

  private val messageRequestWithOptionalFields = MessageRequest(sender, Some(subject), Some(messageContent))
  private val messageRequestWithoutOptionalFields = MessageRequest(sender, None, None)

  private val senderJsonWithSubjectAndMessage = s"""{"sender":"${sender.v}","subject":"${subject.v}","message":"${messageContent.v}"}"""
  private val senderJsonWithOutSubjectAndMessage = s"""{"sender":"${sender.v}"}"""

  def spec = suite("MessageRequestSpec")(
    test("Encode MessageRequest with optional fields to json") {
      for {
        json <- ZIO.succeed(messageRequestWithOptionalFields.toJson)
      } yield assertTrue(json == senderJsonWithSubjectAndMessage)
    },
    test("Encode MessageRequest without optional fields to json") {
      for {
        json <- ZIO.succeed(messageRequestWithoutOptionalFields.toJson)
      } yield assertTrue(json == senderJsonWithOutSubjectAndMessage)
    },
    test("Decode MessageRequest including optional fields from json") {
      for {
        result <- ZIO.succeed(senderJsonWithSubjectAndMessage.fromJson[MessageRequest])
      } yield assertTrue(result == Right(messageRequestWithOptionalFields))
    },
    test("Decode MessageRequest without optional fields from json") {
      for {
        result <- ZIO.succeed(senderJsonWithOutSubjectAndMessage.fromJson[MessageRequest])
      } yield assertTrue(result == Right(messageRequestWithoutOptionalFields))
    }
  )

}
