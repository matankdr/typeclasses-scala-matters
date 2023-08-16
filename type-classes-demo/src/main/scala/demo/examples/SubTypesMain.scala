package demo.examples

object SubTypesMain extends App {
  trait Shape
  case class Circle(radius: Double) extends Shape
  case class Rectangle(width: Double, height: Double) extends Shape

  trait Showable[T] {
    def show(t: T): String
  }

  implicit val showableCircle: Showable[Circle] = new Showable[Circle] {
    def show(c: Circle): String = s"Circle(radius=${c.radius})"
  }

  implicit val showableRectangle: Showable[Rectangle] = new Showable[Rectangle] {
    def show(r: Rectangle): String = s"Rectangle(width=${r.width}, height=${r.height})"
  }

  implicit def showableList[T](implicit s: Showable[T]): Showable[List[T]] =
    elems => s"[ ${elems.map(s.show).mkString(",")} ]"

  object Showable {
    def show[T](t: T)(implicit s: Showable[T]): String = s.show(t)
  }

  val circle = Circle(1.0)
  val rectangle = Rectangle(2.0, 3.0)

  val shapes = List(circle, rectangle)

//  println(Showable.show(shapes))
}
