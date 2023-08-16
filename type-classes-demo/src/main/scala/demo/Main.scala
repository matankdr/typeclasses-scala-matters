package demo

import demo.data.Model._
import demo.encoders.{DefaultJsonEncoders, JsonEncoder}

object Main extends App with DefaultJsonEncoders {


  import JsonEncoder.ops._
  import demo.encoders.DomainJsonEncoders._

  val item = Item("1", "item 1", 12.567)
  val point = Point(1, 2)
  val delivery = Delivery(item, point)

  val points = Map(
    "p1" -> Left(Point(1, 2)),
    "p2" -> Left(Point(3, 4)),
    "item" -> Right(item)
  )

  val deliveries = List(Delivery(item, Point(1, 2)), Delivery(item, Point(3, 4)))

  // pretty print json string
  def prettyPrintJson(json: String): Unit = {
    val indent = "  "
    var level = 0
    json.foreach {
      case '{' =>
        println("{")
        level += 1
        print(indent * level)
      case '}' =>
        println()
        level -= 1
        print(indent * level)
        print("}")
      case ',' =>
        println(",")
        print(indent * level)
      case '[' =>
        println("[")
        level += 1
        print(indent * level)
      case ']' =>
        println()
        level -= 1
        print(indent * level)
        print("]")
      case c =>
        print(c)
    }
    println()
  }

//  prettyPrintJson(delivery.encode)
//  println(JsonEncoder.toJson(item))
//  println(JsonEncoder.toJson(point))
//  println(JsonEncoder.toJson(delivery))
//  prettyPrintJson(points.encode)
//  prettyPrintJson(deliveries.encode)
}
