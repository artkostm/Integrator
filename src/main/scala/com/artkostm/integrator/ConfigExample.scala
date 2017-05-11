package com.artkostm.integrator

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

  import cats._
  import cats.instances.all._
  println(Functor[List].map(List("qw", "43", "dfff"))(_.length))

  import cats.syntax.functor._
  println(List("qw", "43", "dfff").fproduct(_.length).toMap)

  import cats.data._
  import cats.implicits._
  ({(_:Int) + 1}.some) ap 5.some
}