package model

import io.getquill.MappedEncoding
import zio.json.{ JsonDecoder, JsonEncoder }

final case class Subject(v: String)

object Subject {
  implicit val subjectEncoder = JsonEncoder[String].contramap[Subject](_.v)
  implicit val subjectDecoder = JsonDecoder[String].map(Subject(_))
  implicit val dBEncoder      = MappedEncoding[String, Subject](Subject(_))
}
