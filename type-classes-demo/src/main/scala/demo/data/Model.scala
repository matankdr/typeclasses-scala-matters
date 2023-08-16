package demo.data

object Model {
  case class Point(x: Int, y: Int)
  case class Item(id: String, desc: String, price: Double)
  case class Delivery(item: Item, target: Point)
}
