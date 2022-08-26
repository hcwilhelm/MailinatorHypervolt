import apps.{GreetingApp, MailinatorApp}
import repos.H2MailboxRepo
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.start(
      port = 8080,
      http = GreetingApp() ++ MailinatorApp()
    ).provide(H2MailboxRepo.layer)
}
