---
author: Knut Petter Meen
title: Reads/Writes for abstract classes/traits
ingress: The built-in JSON (de-)serialization mechanisms in the playframework are really powerful. But they have a couple of shortcomings. One of which is being able to (de-)serialize a trait or an abstract class as the correct child implementation.
labels: [playframework, scala]
date: 2015-03-29
---

The built-in JSON (de-)serialization mechanisms in the playframework are really powerful.
But they have a couple of shortcomings. One of which is being able to (de-)serialize a trait or an abstract class as the correct child implementation.

I've come across this scenario a couple of times now. Each time I've tried all sorts of different approaches. However, I keep falling back to this pattern based on [a gist by @mandubian](https://gist.github.com/mandubian/8955241).

## The problem
Say we have the following simple representation of Animals

```scala
trait Animal
case class Cat(name: String) extends Animal
case class Dog(name: String) extends Animal
```

If we now want need to expose Cats and Dogs as JSON, we would typically implement an implicit Reads and Writes for those case classes. A common pattern is to do this in the companion objects.

```scala
object Cat {
  implicit val r: Reads[Cat] = Json.reads[Cat]
  implicit val w: Writes[Cat] = Json.writes[Cat]
}

object Dog {
  implicit val r: Reads[Dog] = Json.reads[Dog]
  implicit val w: Writes[Dog] = Json.writes[Dog]
}
```

Now, let's say we want to create a case class for ```PetOwner``` that contains a list of owned pets.

```scala
case class PetOwner(name: String, pets: List[Animal])

object PetOwner {
  implicit val r: Reads[PetOwner] = Json.reads[PetOwner]
  implicit val w: Writes[PetOwner] = Json.writes[PetOwner]
}
```

Of course we would also like to be able to (de-)serialize this class from/to JSON. This is where the trouble begins. Typically you will get this type of error:

```
No implicit Reads for List[Animal] available.
```

and

```
No implicit Writes for List[Animal] available.
```

Because we are in the world of JSON, there is little to no information for the (de-)serialization mechanism to know which child type of Animal it should resolve to.

## A solution
To help resolve the JSON to the correct type we introduce a "discriminator" attribute (I've called it ```val $tpe = "$type"``` here). This is basically a String value that identifies each child type of Animal.
For the ```Reads[Animal]``` implementation we can then look for the "$type" key in the JSON message. And based on the value, we can explicitly use the reads defined for each child.
A similar approach can be done for ```Writes[Animal]```. Only here we do a pattern match on the child type, and for each of them we explicitly invoke its OWrites function ```Cat.w.writes(...)```. Then we append a JsObject with the $tpe attribute ```Json.obj($tpe -> catClsName)``` to ensure each Animal JSON contains which animal type it represents.

```scala
object Animal {
  private val $tpe = "$type"
  private val catClsName = classOf[Cat].getSimpleName
  private val dogClsName = classOf[Dog].getSimpleName

  implicit val reads: Reads[Animal] = Reads { jsv =>
    (jsv \ $tpe).as[String] match {
      case `catClsName` => JsSuccess(jsv.as(Cat.r))
      case `dogClsName` => JsSuccess(jsv.as(Dog.r))
      case err: String => JsError(s"Not a supported Animal type $err")
    }
  }
  implicit val writes: Writes[Animal] = Writes {
    case mf: Cat => Cat.w.writes(mf).as[JsObject] ++ Json.obj($tpe -> catClsName)
    case yf: Dog => Dog.w.writes(yf).as[JsObject] ++ Json.obj($tpe -> dogClsName)
  }
}
```

We are now able to read and write JSON correctly from our PetOwner class.

```scala
val owner = PetOwner("John", List(Cat("Garfield"), Dog("Odie")))
val ownerJson = Json.toJson[PetOwner](owner)
```

produces:

```javascript
{
  "name" : "John",
  "pets" : [ {
    "name" : "Garfield",
    "$type" : "Cat"
  }, {
    "name" : "Odie",
    "$type" : "Dog"
  } ]
}
```

## Summary

This pattern does solve the problem. However it is a bit fiddly having to manipulate the parent companion object every time a new child implementation is needed. If someone has a better solution to this, I am very interested to hear about it.
