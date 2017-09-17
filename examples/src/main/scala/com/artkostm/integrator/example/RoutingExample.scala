package com.artkostm.integrator.example

import com.artkostm.integrator.routing._

object RoutingExample extends App {

  val People = Root / "people" | Root / "persons"
  val Person = People / 'person
  val Pets   = Person / "pets"
  val Pet    = Pets / 'pet / "?p=pap"

  println(Pet.links("personA", "petB"))
  println(People.links())

  "/people/personA/pets/petB" match {
    case Pet(a, b) => println(s"matches: $a & $b")
    case tt => println(tt)
  }

  "/persons/personA/pets/petB" match {
    case Pet(a, b) => println(s"matches: $a & $b")
    case tt => println(tt)
  }

  val Retweets = Root / "statuses" / "retweets" / 'id

  def twitter(v: String): String = s":$v"

  println(Retweets.template(twitter))
  println("-------------")
  Pet.templates(twitter).foreach(println)

  val list = Pet.templates(twitter)
  list match {
    case List(a, b) => s"Got it! $a, $b"
    case h #:: tl => println(s"And now: $h -> $tl")
  }

  println("WTF!")

  val request = new Request(Map("id" -> "1234", "search" -> "by_name"))

  println(request.query.id)
  println(request.query.search)
  println(request.query.unknown)
}
