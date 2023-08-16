package demo.util

object PrettyPrinter {
  def prettyPrint(json: String): Unit = {
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

}
