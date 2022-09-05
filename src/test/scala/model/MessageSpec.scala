package model

import zio.test.ZIOSpecDefault
import zio.json.{DecoderOps, EncoderOps}
import zio.test._
import zio._

import java.time.Instant
import java.util.UUID

object MessageSpec extends ZIOSpecDefault {

  private val messageId = MessageId(UUID.fromString("0e7fea0c-e50e-41ca-b42a-166120f77951"))
  private val mailbox = Mailbox("foo")
  private val sender = Sender("bar")
  private val subject = Subject("wtf")
  private val messageContent = MessageContent("A very long message")
  private val createdAt = Instant.parse("2022-06-02T21:34:33.616Z")

  private val messageWithOptionalFields = Message(messageId, mailbox, sender, Some(subject), Some(messageContent), createdAt)
  private val messageWithoutOptionalFields = Message(messageId, mailbox, sender, None, None, createdAt)

  private val senderJsonWithSubjectAndMessage = s"""{"id":"${messageId.value}","mailbox":"${mailbox.name}","sender":"${sender.value}","subject":"${subject.value}","message":"${messageContent.value}","createdAt":"${createdAt.toString}"}"""
  private val senderJsonWithOutSubjectAndMessage = s"""{"id":"${messageId.value}","mailbox":"${mailbox.name}","sender":"${sender.value}","createdAt":"${createdAt.toString}"}"""

  def spec = suite("MessageSpec")(
    test("Encode Message with optional fields to json") {
      for {
        json <- ZIO.succeed(messageWithOptionalFields.toJson)
      } yield assertTrue(json == senderJsonWithSubjectAndMessage)
    },
    test("Encode Message without optional fields to json") {
      for {
        json <- ZIO.succeed(messageWithoutOptionalFields.toJson)
      } yield assertTrue(json == senderJsonWithOutSubjectAndMessage)
    },
    test("Decode Message including optional fields from json") {
      for {
        result <- ZIO.succeed(senderJsonWithSubjectAndMessage.fromJson[Message])
      } yield assertTrue(result == Right(messageWithOptionalFields))
    },
    test("Decode Message without optional fields from json") {
      for {
        result <- ZIO.succeed(senderJsonWithOutSubjectAndMessage.fromJson[Message])
      } yield assertTrue(result == Right(messageWithoutOptionalFields))
    }
  )
}
