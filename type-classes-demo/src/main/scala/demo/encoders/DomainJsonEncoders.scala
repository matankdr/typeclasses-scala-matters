package demo.encoders

import demo.data.Model._

object DomainJsonEncoders extends DefaultJsonEncoders {
  import JsonEncoder.ops._

  implicit val pointEncoder = new JsonEncoder[Point] {
    override def encode(p: Point): String =
      s"""{"x": ${implicitly[JsonEncoder[Int]].encode(p.x)},"y": ${implicitly[JsonEncoder[Int]].encode(p.y)}}"""
  }

  implicit val itemEncoder = new JsonEncoder[Item] {
    override def encode(i: Item): String = {
      Seq(
        "id"    -> implicitly[JsonEncoder[String]].encode(i.id),
        "desc"  -> implicitly[JsonEncoder[String]].encode(i.desc),
        "price" -> implicitly[JsonEncoder[Double]].encode(i.price)
      ).map{ case (key, value) => s""""$key": $value"""}
        .mkString("{", ",", "}")
    }
  }

  implicit def deliveryEncoder: JsonEncoder[Delivery] = new JsonEncoder[Delivery] {
    override def encode(d: Delivery): String =
      s"""{"item": ${d.item.encode},"target": ${d.target.encode}}"""
  }
}
