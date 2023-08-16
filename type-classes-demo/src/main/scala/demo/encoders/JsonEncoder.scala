package demo.encoders

@annotation.implicitNotFound("No JsonEncoder[T] found for type ${T}. \nTry defining a new JsonEncoder[${T}] or importing DomainJsonEncoders")
trait JsonEncoder[T] {
  def encode(t: T): String
}

object JsonEncoder {

  object ops {
    def encode[T: JsonEncoder](t: T): String =
      implicitly[JsonEncoder[T]].encode(t)

    implicit class JsonEncoderSyntax[T: JsonEncoder](t: T) {
      def encode: String = implicitly[JsonEncoder[T]].encode(t)
    }
  }

}

