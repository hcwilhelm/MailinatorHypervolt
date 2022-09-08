package services
import java.time.Instant
import repos.MessageRepo
import zio._

object MessageGarbageCollector {

  def makeGC(repeat: Duration, retentionTime: Duration) = {

    def logInfo(now: Instant) =
      ZIO.logInfo(s"Running MessageGarbageCollector at: ${now.toString}")

    def deleteOldMessages(now: Instant) =
      MessageRepo.deleteMessagesOlderThan(now.minusMillis(retentionTime.toMillis))

    Clock.instant
      .flatMap(now => logInfo(now) *> deleteOldMessages(now))
      .repeat(Schedule.spaced(repeat)) *> ZIO.never
  }

}
