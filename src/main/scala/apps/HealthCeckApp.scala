package apps

import zhttp.http._

object HealthCeckApp {
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {

      // GET /health
      case Method.GET -> !! / "health" =>
        Response.text(s"The service is up and running")
    }
}
