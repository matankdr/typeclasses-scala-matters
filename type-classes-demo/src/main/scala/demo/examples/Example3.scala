package demo.examples

import demo.data.Model._
import demo.encoders.DomainJsonEncoders._
import demo.encoders.JsonEncoder.ops._
import demo.util.PrettyPrinter

object Example3 extends App {

  val item = Item("1", "item 1", 12.567)
  val point = Point(1, 2)

  val deliveries = List(
    Delivery(item, Point(1, 2)),
    Delivery(item, Point(3, 4))
  )

  PrettyPrinter.prettyPrint(deliveries.encode)
}
