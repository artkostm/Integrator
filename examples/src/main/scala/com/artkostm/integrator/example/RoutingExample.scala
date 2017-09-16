package com.artkostm.integrator.example

import com.artkostm.integrator.routing._

object RoutingExample extends App {

  val People = Root / "people" | Root / "persons"
  val Person = People / 'person
  val Pets   = Person / "pets"
  val Pet    = Pets / 'pet

  println(Pet.links("personA", "petB"))

  "/people/personA/pets/petB" match {
    case Pet(a, b) => println(s"matches: $a & $b")
  }

  "/persons/personA/pets/petB" match {
    case Pet(a, b) => println(s"matches: $a & $b")
  }

  val Retweets = Root / "statuses" / "retweets" / 'id

  def twitter(v: String): String = s":$v"

  println(Retweets.template(twitter))
  println(Pet.templates(twitter))
}
