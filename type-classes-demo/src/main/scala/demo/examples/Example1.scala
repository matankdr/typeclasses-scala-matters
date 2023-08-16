package demo.examples

import demo.data.Model._
import demo.encoders.DomainJsonEncoders._
import demo.encoders.JsonEncoder.ops._
import demo.util.PrettyPrinter

object Example1 extends App {
  val point = Point(1, 2)

  PrettyPrinter.prettyPrint(point.encode)
}
