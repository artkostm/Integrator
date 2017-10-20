package com.artkostm.integrator.example.netty

import com.artkostm.integrator.routing._
import scala.concurrent.Future
//http://mojolicious.org/ - trying to reflect Mojolicious(perl) routing dsl
object dslexercises extends App {
  import Endpoints._
  
  val persons = Root / "persons"
  val first = get(persons, { request => Future.successful(Response())})
  
  
}
case class Request()
case class Response()
case class Endpoint()
object Endpoints {
  type Service = Request => Future[Response]
//  type End = 
  def get(pattern: Linx[_, _], f: => Service): Endpoint = new Endpoint
}