package com.artkostm.integrator

/**
  * Created by artsiom.chuiko on 10/01/2017.
  */
object ConfigExample extends App {
  import com.artkostm.integrator.router.RoutingDsl._
  get[String] / "" >> {
    ""
  }

  val result = com.artkostm.integrator.router.RouteResult("target", Map("id" -> "32"), Map("score" -> List("32"), "id" -> List("32")))

  println(result.p("id"))
  println(result.p("ids"))
  println(result.params("id"))
  val (queryP, pathP) = result.params("id")
  println(queryP)
}