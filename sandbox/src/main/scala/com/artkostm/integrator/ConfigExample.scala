package com.artkostm.integrator

import cats._
import cats.implicits._
/**
  * Created by artsiom.chuiko on 10/01/2017.
  */
object ConfigExample extends App {
  import com.artkostm.integrator.router.RoutingDsl._
  val r = router[String] {
    post / "/path/" -> {
      ""
    } | connect / "/path/" -> {
      ""
    } | trace / "" -> "" | head / "/path/" -> {
      ""
    }
  }

  r.print()


  val result = com.artkostm.integrator.router.RouteResult("target", Map("id" -> "32"), Map("score" -> List("32"), "id" -> List("32")))

  println(result.p("id"))
  println(result.p("ids"))
  println(result.params("id"))
  val (queryP, pathP) = result.params("id")
  println(queryP)

  println(Functor[List].map(List("qw", "43", "dfff"))(_.length))

  println(List("qw", "43", "dfff").fproduct(_.length).toMap)


  println(List({(_:Int) + 1}) ap List(1, 2, 3, 4))
  println((List(5) |@| List(6)).map(_ + _))


  println((5.some |@| 6.some).map(_ + _))
}