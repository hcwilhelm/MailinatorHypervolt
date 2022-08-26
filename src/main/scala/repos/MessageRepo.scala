package repos

import model.Message
import model.Message.MessageId
import zio._

import java.time.Instant

trait MessageRepo {

  def insert(message: Message): Task[Unit]

  def getById(messageId: MessageId): Task[Message]

  def deleteById(messageId: MessageId): Task[Unit]

  def messages: Task[List[Message]]

  def deleteMessagesOlderThan(timestamp: Instant): Task[Unit]
}

object MessageRepo {
  def insert(message: Message) = ZIO.serviceWithZIO[MessageRepo](_.insert(message))

  def getById(messageId: MessageId) = ZIO.serviceWithZIO[MessageRepo](_.getById(messageId))

  def deleteById(messageId: MessageId) = ZIO.serviceWithZIO[MessageRepo](_.deleteById(messageId))

  def messages = ZIO.serviceWithZIO[MessageRepo](_.messages)

  def deleteMessagesOlderThan(timestamp: Instant) = ZIO.serviceWithZIO[MessageRepo](_.deleteMessagesOlderThan(timestamp))
}
