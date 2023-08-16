package demo.spray

import demo.data.Model._
import spray.json._

object Main extends App {
  import JsonProtocols._

  val item = Item("1", "item 1", 12.567)
  val point = Point(1, 2)

  val deliveries = List(
    Delivery(item, Point(1, 2)),
    Delivery(item, Point(3, 4))
  )

  println(deliveries.toJson.prettyPrint)
}
