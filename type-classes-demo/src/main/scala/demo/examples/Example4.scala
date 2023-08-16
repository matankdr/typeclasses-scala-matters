package demo.examples

import demo.data.Model._
import demo.encoders.DomainJsonEncoders._
import demo.encoders.JsonEncoder.ops._
import demo.util.PrettyPrinter

object Example4 extends App {

  val item = Item("1", "item 1", 12.567)
  val point = Point(1, 2)
  val delivery = Delivery(item, point)

  val stuff = Map(
    "p1"   -> Left(Point(1, 2)),
    "p2"   -> Left(Point(3, 4)),
    "item" -> Right(item)
  )

  PrettyPrinter.prettyPrint(stuff.encode)
}
