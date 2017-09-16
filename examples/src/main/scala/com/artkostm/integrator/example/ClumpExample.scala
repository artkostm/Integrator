package com.artkostm.integrator.example

import com.artkostm.integrator.clump.Clump

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object ClumpApp extends App {

  implicit val context = scala.concurrent.ExecutionContext.global

  class Service {
    val stringService = Map(1 -> "first", 2 -> "second", 3 -> "third")

    def fetch(ids: List[Int]): Future[Map[Int, String]] = Future {
      stringService.filter(entry => ids.contains(entry._1))
    }
  }

  val service = new Service

  val stringSource = Clump.source(service.fetch _)

  val second = stringSource.get(2)

  val third = stringSource.get(3)

  val nullClumb = stringSource.get(5)

  println(Await.result(second.getOrElse("unknown"), Duration.Inf))
  println(Await.result(nullClumb.getOrElse("unknown"), Duration.Inf))

}
