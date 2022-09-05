package model

import io.getquill.MappedEncoding
import zio.json.{ JsonDecoder, JsonEncoder }

final case class Subject(value: String)

object Subject {
  implicit val subjectEncoder = JsonEncoder[String].contramap[Subject](_.value)
  implicit val subjectDecoder = JsonDecoder[String].map(Subject(_))
  implicit val dBEncoder      = MappedEncoding[String, Subject](Subject(_))
}
