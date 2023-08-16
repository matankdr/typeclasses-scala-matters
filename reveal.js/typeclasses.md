## Type Classes
### Why are they useful?
<img src="https://media.giphy.com/media/3oKIPjHCmuXqdVvak8/giphy.gif">

### Matan Keidar

---

## Who is Matan?
- Current: Backend engineer @ Wix 👨‍💻 <!-- .element: class="fragment" -->
- Building cool stuff in Scala 🚀 <!-- .element: class="fragment" -->
  - For the last 8 years 
- Father of 2 clones 👧🏻👩‍🦰 <!-- .element: class="fragment" -->
- Best husband 🥇 <!-- .element: class="fragment" -->
  - (This is what I keep telling to my wife)

---

## Outline
- What is the problem?
- Explaining type classes
- Live coding (short) 
- Summary

---

## Let's Encode!
- Goal: write **generic** JSON encoder
- Given the following class:
```scala
case class Point(x: Int, y: Int)
```
- We would like to serialize it to a JSON string
- For example: <!-- .element: class="fragment" -->
  - Given `Point(3,4)`
  - The expected JSON output should be:
```json
{ "x": 3, "y" : 4}
```


<!-- .slide: data-auto-animate -->
## Naive solution <!-- .element: data-id="title" -->
Extend `Point` type to support this behavior:
```scala
case class Point(x: Int, y: Int) {
  def encode: String = 
    s"""{ "x": $x, "y": $y }"""
}
```


<!-- .slide: data-auto-animate -->
## Naive solution <!-- .element: data-id="title" -->
It works for a simple case. Let's examine a more complicated case:
<pre style="width:970px"><code data-trim class="scala" style="width:970px" data-line-numbers="1-4|6-10|11-15">
case class Point(x: Int, y: Int) {
  def encode: String = 
    s"""{ "x": $x, "y": $y }"""
}

case class Item(id: String, desc: String, price: Int) {
  def encode: String = 
    s"""{ "id": "${id}","desc": "${desc}","price": $price }"""

}
case class Delivery(item: Item, target: Point) {
  def encode: String = 
    s"""{ "item": ${item.encode}, "target": ${target.encode} }"""
}
</code></pre>


<!-- .slide: data-auto-animate -->
## Naive solution <!-- .element: data-id="title" -->
```scala
val delivery = Delivery(
  item = Item(id = "1", desc = "item-1", price = 12), 
  target = Point(1, 2)
)

delivery.encode 
```
Output:
```json
{
  "item": {
    "id": "1",
    "desc": "item-1",
    "price": 12
  },
  "target": {
   "x": 1,
   "y": 2
  }
}
```


## And the plot thickens...
How can we easily encode complex types?
```scala
case class Urgency(level: Int)

case class Courier(deliveries: List[Delivery], 
                   urgency: Option[Urgency] = None) {
  def encode: String = ???
}
```
## 🤯 


## The problem
<ul style="list-style-type:'👿  ';">
  <li>Solution cannot be applied if we do not have the source code
    <ul>
      <li>The class cannot be extended with new behavior</li>
    </ul>
  </li>
  <li>We had to manually JSON encode primitive types
    <ul>
    <li>No code reuse</li>
    </ul>
  </li>
  <li>Complex hierarchies lead to complicated implementation</li>
</ul>

---

## Fixing it (1)
- Extract the `encode` behavior to a dedicated type

<pre><code data-trim class="scala" style="width:930px" data-line-numbers="1-3|5-7|9|11">
trait JsonEncoder[T] {
  def encode(t: T): String
}

val pointEncoder = new JsonEncoder[Point] = {
  def encode(p: Point) = s"""{ "x": ${p.x}, "y": ${p.y} }"""
}

def printJson[T](t: T, enc: JsonEncoder[T]) = enc.encode(t)

printJson(Point(3,4), pointEncoder) // { "x": 3, "y": 4 }
</code></pre>
- (Will handle primitive types later)
- Almost there, we are still required to explicitly provide the encoding logic 🙁
<!-- .element: class="fragment" -->

---

## Reminder about implicits
- Given a function call with an implicit arg of type T <!-- .element: class="fragment" -->
- The compiler searches an implicit value of T <!-- .element: class="fragment" -->
- If such value exists in the scope, the compiler uses it <!-- .element: class="fragment" -->

```scala
implicit val shekels: String = "₪"

def printBalance(coins: Int)(implicit currency: String) = 
  s"$coins $currency"

// call with implicit args
printBalance(10) // "10 ₪"

// call with explicit args
printBalance(10)(shekels)
```
<!-- .element: class="fragment" -->

---

## Fixing it (2)
Let's extract the `encode` behavior to a dedicated type
<pre><code data-trim class="scala" style="width:930px" data-line-numbers="1-7|9-12">
trait JsonEncoder[T] {
  def encode(t: T): String
}

implicit val pointEncoder = new JsonEncoder[Point] = {
  def encode(p: Point) = s""" { "x": ${p.x}, "y": ${p.y} }"""
}

def printJson[T](t: T)(implicit enc: JsonEncoder[T]) = 
  enc.encode(t)

printJson(Point(3,4)) // { "x": 3, "y": 4 }
</code></pre>
And we have just implemented a type class! 😎
<!-- .element: class="fragment" -->


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
  <li >Solution cannot be applied if we do not have the source code
    <ul>
      <li>The class cannot be extended with new behavior</li>
    </ul>
  </li>
  <li>We had to manually JSON encode primitive types
    <ul>
    <li>No code reuse</li>
    </ul>
  </li>
  <li>Complex hierarchies lead to complicated implementation</li>
</ul>


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
  <li style="list-style-type:'✅  ';"><s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li>We had to manually JSON encode primitive types
    <ul>
    <li>No code reuse</li>
    </ul>
  </li>
  <li>Complex hierarchies lead to complicated implementation</li>
</ul>

---

<!-- .slide: data-auto-animate -->
# Type Class <!-- .element: data-id="title" -->
> In computer science, a type class is a type system construct that supports ad hoc polymorphism. This is achieved by adding constraints to type variables in parametrically polymorphic types.
>
> [ [Wikipedia — Type class](https://en.wikipedia.org/wiki/Type_class)]
<!-- .element: data-id="box" -->


<!-- .slide: data-auto-animate -->
# Type Class <!-- .element: data-id="title" -->
> A group of types that share a known set of behaviors.
<!-- .element: data-id="box" -->

Examples: 
- All types that support generating a unique identifier.
- All types that support encoding to a JSON string.


## Why type classes?
- Single responsibility <!-- .element: class="fragment" -->
- Enables adding new behaviors to existing types <!-- .element: class="fragment" -->
  - *without* having access to the source of those types
- Ad-hoc polymorphism <!-- .element: class="fragment" -->
- Errors at compile time <!-- .element: class="fragment" -->

---

<!-- .slide: data-auto-animate -->
## Json Encoder Revisited <!-- .element: data-id="title" -->
Although it works, it is far from perfect
<pre style="width:870px"><code data-trim class="scala" style="width:870px" >
implicit val pointEncoder = new JsonEncoder[Point] = {
  def encode(p: Point) = s""" { "x": ${p.x}, "y": ${p.y} }"""
}
</code></pre>
Let's support better code reuse


<!-- .slide: data-auto-animate -->
## Json Encoder Revisited <!-- .element: data-id="title" -->
<pre style="width:900px"><code data-trim class="scala" style="width:900px" data-line-numbers="1-3|5-12">
implicit val intEncoder = new JsonEncoder[Int] = {
  def encode(i: Int) = i.toString
}

implicit def pointEncoder(implicit enc: JsonEncoder[Int]) = 
  new JsonEncoder[Point] {
    def encode(p: Point) = 
      s"""{ 
            "x": ${enc.encode(p.x)}, 
            "y": ${enc.encode(p.y)} 
          }
      """
  }
</code></pre>


<!-- .slide: data-auto-animate -->
## Json Encoder Revisited <!-- .element: data-id="title" -->
<pre><code data-trim class="scala" style="width:900px" data-line-numbers="1-6|7-17">
case class Item(id: String, desc: String, price: Int)

implicit val strEncoder = new JsonEncoder[String] = {
  def encode(str: String) = s""""$str""""
}

implicit val itemEncoder = new JsonEncoder[Item] {
  def encode(i: Item)
            (implicit se: JsonEncoder[String], 
                      ie: JsonEncoder[Int]): String =
    s"""{
          "id": ${se.encode(i.id)},
          "desc": ${se.encode{(i.desc)},
          "price": ${ie.encode(i.price)}}
        }
     """
}
</code></pre>


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
  <li style="list-style-type:'✅  ';"> <s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li>We had to manually JSON encode primitive types
    <ul>
    <li>No code reuse</li>
    </ul>
  </li>
  <li>Complex hierarchies lead to complicated implementation</li>
</ul>


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
  <li style="list-style-type:'✅  ';"><s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>We had to manually JSON encode primitive types</s>
    <ul>
    <li><s>No code reuse</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>Complex hierarchies lead to complicated implementation</s></li>
</ul>


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
  <li style="list-style-type:'✅  ';"><s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>We had to manually JSON encode primitive types</s>
    <ul>
    <li><s>No code reuse</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>Complex hierarchies lead to complicated implementation</s></li>
  <li>Type class has to be referenced by its name</li>
</ul>


<!-- .slide: data-auto-animate -->
## Json Encoder Revisited <!-- .element: data-id="title" -->
- <span style="color:red">Important</span>: We added a new behavior to `String`
  - Despite the fact it is a final class 
- Alternative solution: create a wrapper to the <!-- .element: class="fragment" data-fragment-index="3" --> `String` <!-- .element: class="fragment" data-fragment-index="3" --> type that implements <!-- .element: class="fragment" data-fragment-index="3" --> `JsonEncoder` <!-- .element: class="fragment" data-fragment-index="3" -->
  - All usages in the application should refer to the wrapper class <!-- .element: class="fragment" data-fragment-index="4" -->
  - Not practical at all... <!-- .element: class="fragment" data-fragment-index="4" -->

---

<!-- .slide: data-auto-animate -->
## Syntactic sugar <!-- .element: data-id="title" -->
- We were able to invoke the `JsonEncoder` implementation of our data model classes 
  - However, it was not very nice
- Must have a reference to our type class instance <!-- .element: class="fragment" data-fragment-index="2" -->
  - And know its name
- Let's make the API calls look as natural as possible <!-- .element: class="fragment" data-fragment-index="3" -->
  - e.g., avoiding `encoder.encode(x)`
  - Call JSON encoding directly as if it was a part of the object behavior


<!-- .slide: data-auto-animate -->
## Syntactic sugar <!-- .element: data-id="title" -->
- Let's define an invocation helper 
- Exposes an extension method for calling the type class in scope

<pre style="width:950px"><code data-trim class="scala" style="width:950px">
  implicit class JsonEncoderSyntax[T](t: T) {
    def toJson(implicit enc: JsonEncoder[T]): String = enc.encode(t)
  }
</code></pre>


<!-- .slide: data-auto-animate -->
## Syntactic sugar <!-- .element: data-id="title" -->
- Now, we can call the extension method directly
- Pretending that all objects have a `toJson` method

```scala[|3,11-13]
implicit def pointEncoder(implicit enc: JsonEncoder[Int]) = new JsonEncoder[Point] = {
  def encode(p: Point) = 
    s""" { "x": ${p.x.toJson}, "y": ${p.y.toJson} }"""
}

implicit val itemEncoder = new JsonEncoder[Item] {
  def toJson(i: Item)
             (implicit se: JsonEncoder[String], 
              ie: JsonEncoder[Int]): String =
    s"""{
          "id": ${i.id.toJson},
          "desc": ${i.desc.toJson},
          "price": ${i.price.toJson}
        }
     """
}
```


<!-- .slide: data-auto-animate -->
## The problem
<ul style="list-style-type:'👿  ';">
    <li style="list-style-type:'✅  ';"><s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>We had to manually JSON encode primitive types</s>
    <ul>
    <li><s>No code reuse</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>Complex hierarchies lead to complicated implementation</s></li>
  <li>Type class has to be referenced by its name</li>
</ul>


<!-- .slide: data-auto-animate -->
## The problem - Solved!
<ul style="list-style-type:'✅  ';">
    <li style="list-style-type:'✅  ';"><s>Solution cannot be applied if we do not have the source code</s>
    <ul>
      <li><s>The class cannot be extended with new behavior</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>We had to manually JSON encode primitive types</s>
    <ul>
    <li><s>No code reuse</s></li>
    </ul>
  </li>
  <li style="list-style-type:'✅  ';"><s>Complex hierarchies lead to complicated implementation</s></li>
  <li><s>Type class has to be referenced by its name</s></li>
</ul>


## Compile time safety
- We cannot call `encode` on a type that does not have a meaning of `encode` 
  - Because it does not have an implementation
- No instance of `JsonEncoder[T]` in the scope 
  - Result: compiler throws an error

---

## Type Classes: The bad side
- The solution is based on traits with generic types
- Generics do not work well with sub-typing
- The key idea is to have an implementation of a concrete type


## Type Classes: The bad side
<!-- <div class="r-strech"> -->
<pre style="left:-130px;"><code class="scala" data-trim style="width:1100px" data-line-numbers="1-3|5-7|8-17|18-21|22-26">
trait Showable[T] {
  def show(t: T): String
}

trait Shape
case class Circle(radius: Double) extends Shape
case class Rectangle(height: Double, width: Double) extends Shape

implicit val showableCircle = new Showable[Circle] {
  def show(c: Circle): String = 
    s"Circle(r=${c.radius})"
}

implicit val showableRectangle = new Showable[Rectangle] {
  def show(r: Rectangle): String = 
    s"Rectangle(w=${r.width}, h=${r.height})"
}

implicit def showableList[T](implicit s: Showable[T]): Showable[List[T]] =
  elems => s"[ ${elems.map(s.show).mkString(",")} ]"

def callShow[T](t: T)(implicit s: Showable[T]): String = s.show(t)

val shapes = List(Circle(1.0), Rectangle(2.0, 3.0))

println(callShow(shapes))
</code></pre>



<!-- .element style="overflow-wrap: break-word;" -->
<pre style="left:-120px;"><code class="json" data-trim style="width:950px" >
could not find implicit value for parameter s: Showable[List[Shape]]
</code></pre>
## 😡

---

## 3 Steps for a type class


<!-- .slide: data-auto-animate -->
## Step 1: Define the type class <!-- .element: data-id="title" -->
Create the generic trait that defines new behavior

```scala
trait JsonEncoder[T] {
  def encode(t: T): String
}
```


<!-- .slide: data-auto-animate -->
## Step 2: Create type class instance <!-- .element: data-id="title" -->
- Create an implicit instance 
- Implement the behavior that should be added

<pre style="width:960px"><code data-trim class="scala" style="width:960px">
implicit def pointEncoder(implicit enc: JsonEncoder[Int]) = 
  new JsonEncoder[Point] = {
    def encode(p: Point) = 
      s""" { "x": ${enc.encode(p.x)}, "y": ${enc.encode(p.y)} } """
  }
</code></pre>


<!-- .slide: data-auto-animate -->
## Step 3: Create the extension method <!-- .element: data-id="title" -->
- For accessing the behavior directly from the type
- Instead of calling it from the type class 

```scala
  implicit class JsonEncoderSyntax[T](t: T) {
    def toJson(implicit enc: JsonEncoder[T]): String = 
      enc.toJson(t)
  }
```  
<!-- .element: data-id="code" data-auto-animate-easing="cubic-bezier(0.42, 0.0, 1.0, 1.0)" -->


## Demo

<!-- ## Why type classes?
- Type Class: an interface that defines some behavior.
- Enables us to add new behavior to an existing data type *without* altering the source code
- Naive way: directly inherit/extend an existing type for adding new behavior
  - What if we are not able to do it? (final class) -->
<!-- - Type class pattern enables us to add new behaviors *without* having access to the source of those types. -->

---

### In Summary, Type classes:
- Are **interfaces** that define **new behaviors** <!-- .element: class="fragment" -->
- Enable adding new behavior to existing types **without altering the source code** <!-- .element: class="fragment" -->
- Provide ad-hoc polymorphism <!-- .element: class="fragment" -->
- Usually expressed with generic traits and implicit calls to make a clean API <!-- .element: class="fragment" -->
- Enables the compiler to write code instead of the developer <!-- .element: class="fragment" -->
  - Type class derivation

---

<!-- .slide: data-auto-animate -->
# Questions? <!-- .element: style="color: white; border: 10px; border-color: black;" -->
<img style='right:left;width:300px; font-size: 50px' src="matan-linkedin-qr.jpg">
<!-- .slide: data-background="https://thehomebasedmom.com/wp-content/uploads/2019/05/Frequently-Asked-Questions.jpg"  data-background-opacity="0.7" -->

---

<!-- .slide: data-auto-animate -->
## Thank you 🙏
<img class="left" style='float:left;width:400px; font-size: 50px' src="https://media.giphy.com/media/Pnh0Lou03fv92J4puZ/giphy.gif" >
<img class="right" style='right:left;width:400px; font-size: 50px' src="matan-linkedin-qr.jpg">
              

<!-- - Type classes allow us to define a set of behaviors wholly separately from the objects and types that will implement those behaviors.
- Type classes are expressed in pure Scala with traits that take type parameters, and implicits to make syntax clean.
- Type classes allow us to extend or implement behavior for types and objects whose source we cannot modify.
  - Provide the ad hoc polymorphism necessary to solve the expression problem).
- Type classes enables the compiler to write code instead of the developer
  - Type class derivation   -->



<!-- .slide: data-background="https://media.giphy.com/media/Pnh0Lou03fv92J4puZ/giphy.gif" data-background-size="50%" -->




---

<!-- .slide: data-background="https://media.giphy.com/media/Pnh0Lou03fv92J4puZ/giphy.gif" data-background-size="50%" -->