package repos

import java.time.Instant
import model.{ Mailbox, Message, MessageId, MessageRequest }
import zio._

trait MessageRepo {

  def insert(mailbox: Mailbox, message: MessageRequest): Task[MessageId]

  def getById(messageId: MessageId): Task[Option[Message]]

  def getByMailbox(mailbox: Mailbox): Task[List[Message]]

  def deleteById(messageId: MessageId): Task[Unit]

  def messages: Task[List[Message]]

  def deleteMessagesOlderThan(timestamp: Instant): Task[Unit]
}

object MessageRepo {
  def insert(mailbox: Mailbox, message: MessageRequest) = ZIO.serviceWithZIO[MessageRepo](_.insert(mailbox, message))

  def getById(messageId: MessageId) = ZIO.serviceWithZIO[MessageRepo](_.getById(messageId))

  def getByMailbox(mailbox: Mailbox) = ZIO.serviceWithZIO[MessageRepo](_.getByMailbox(mailbox))

  def deleteById(messageId: MessageId) = ZIO.serviceWithZIO[MessageRepo](_.deleteById(messageId))

  def messages = ZIO.serviceWithZIO[MessageRepo](_.messages)

  def deleteMessagesOlderThan(timestamp: Instant) =
    ZIO.serviceWithZIO[MessageRepo](_.deleteMessagesOlderThan(timestamp))
}
