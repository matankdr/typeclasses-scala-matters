package demo.spray

import demo.data.Model._
import spray.json.DefaultJsonProtocol

object JsonProtocols extends DefaultJsonProtocol {
  implicit val pointFormat    = jsonFormat2(Point)
  implicit val itemFormat     = jsonFormat3(Item)
  implicit val deliveryFormat = jsonFormat2(Delivery)
}
