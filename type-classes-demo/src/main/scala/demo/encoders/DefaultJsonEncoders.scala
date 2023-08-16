package demo.encoders

import JsonEncoder.ops._

trait DefaultJsonEncoders {
  implicit val stringEncoder: JsonEncoder[String] = str => s""""$str""""

  implicit val intEncoder: JsonEncoder[Int] = _.toString

  implicit val doubleEncoder: JsonEncoder[Double] = double => f"$double%1.2f"

  implicit def listEncoder[T](implicit enc: JsonEncoder[T]): JsonEncoder[List[T]] =
    elems => s"[ ${elems.map(_.encode).mkString(",")} ]"

  implicit def mapEncoder[V: JsonEncoder]: JsonEncoder[Map[String, V]] = {
    _.map {
      case (k, v) =>
        s"""${implicitly[JsonEncoder[String]].encode(k)}: ${implicitly[JsonEncoder[V]].encode(v)}"""
    }.mkString("{", ",", "}")
  }

  implicit def eitherEncoder[S: JsonEncoder, T: JsonEncoder]: JsonEncoder[Either[S, T]] =
    (e: Either[S, T]) => e match {
      case Left(s)  => implicitly[JsonEncoder[S]].encode(s)
      case Right(t) => implicitly[JsonEncoder[T]].encode(t)
    }
}
