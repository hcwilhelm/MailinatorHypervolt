import apps.{ HealthCeckApp, MailinatorApp }
import io.getquill.jdbczio.Quill
import repos.{ H2MailboxRepo, H2MessageRepo }
import services.MessageGarbageCollector
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {

  val dataSourceLayer = Quill.DataSource.fromPrefix("MailinatorApp")
  val appLayer        = dataSourceLayer >>> H2MailboxRepo.layer ++ H2MessageRepo.layer

  val server = Server.start(
    port = 8080,
    http = HealthCeckApp() ++ MailinatorApp()
  )

  val repeatDuration    = Duration.fromSeconds(10)
  val retentionDuration = Duration.fromSeconds(30)
  val gc                = MessageGarbageCollector.makeGC(repeatDuration, retentionDuration)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    (for {
      serverFiber <- server.fork
      gcFiber     <- gc.fork
      _           <- serverFiber.join.race(gcFiber.join)
    } yield ()).provide(appLayer)
}
