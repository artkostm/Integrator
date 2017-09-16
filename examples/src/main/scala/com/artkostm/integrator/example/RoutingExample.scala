package com.artkostm.integrator.example

import com.artkostm.integrator.routing._

object RoutingExample extends App {

  val People = Root / "people" | Root / "persons"
  val Person = People / 'person
  val Pets   = Person / "pets"
  val Pet    = Pets / 'pet

  println(Pet.links("personA", "petB"))

  "/" match {
    case Root() => println("root")
    case Pet("","") => println("pet")
  }

  "/people/personA/pets/petB" match {
    case Pet(a, b) => println(s"matches: $a & $b")
  }
}
