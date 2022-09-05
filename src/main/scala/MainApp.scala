import apps.{ HealthCeckApp, MailinatorApp }
import repos.{ H2MailboxRepo, H2MessageRepo }
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {

  val appLayer = H2MailboxRepo.layer ++ H2MessageRepo.layer

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .start(
        port = 8080,
        http = HealthCeckApp() ++ MailinatorApp()
      )
      .provide(appLayer)
}
