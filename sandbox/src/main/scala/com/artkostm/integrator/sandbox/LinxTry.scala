package com.artkostm.integrator.sandbox

import linx._

object LinxApp extends App {
  val People = Root / "people" | Root / "persons"
  val Person = People / 'person
  val Pets   = Person / "pets"
  val Pet    = Pets / 'pet / "/?p=pap"

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
  println(Pet.templates(twitter))
}
