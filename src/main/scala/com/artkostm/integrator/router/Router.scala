package com.artkostm.integrator.router

import com.artkostm.integrator.router.HttpMethod._

/**
  * Created by artsiom.chuiko on 12/03/2017.
  *
  *
  */

trait RouteMatcher {
  def anyMatched(requestPathTokens: Array[String]): Boolean
}
sealed trait Route
sealed trait TargetHandler[+T] {
  def ->[S >: T](f: => S): MethodConcatenation[S]
}
sealed trait MethodConcatenation[+T] {
  def |[S >: T](method: MethodConcatenation[S]): MethodConcatenation[S] = this //TODO
}

sealed trait HttpMethod[+T] extends TargetHandler[T] with MethodConcatenation[T] with Route { self =>

  def /(path: String): TargetHandler[T] = self //TODO

  override def ->[S >: T](f: => S) = self //TODO
}

object HttpMethod {
  case class Get[+T]() extends HttpMethod[T]
  case class Connect[+T]() extends HttpMethod[T]
  case class Delete[+T]() extends HttpMethod[T]
  case class Head[+T]() extends HttpMethod[T]
  case class Options[+T]() extends HttpMethod[T]
  case class Path[+T]() extends HttpMethod[T]
  case class Post[+T]() extends HttpMethod[T]
  case class Put[+T]() extends HttpMethod[T]
  case class Trace[+T]() extends HttpMethod[T]
}

trait Router[+T] {
  def print(): Unit
}
case class StandardRouter[+T](routes: List[Route]) extends Router[T] {
  override def print(): Unit = routes.foreach { route =>
    println(s"$route")
  }
}

object RoutingDsl {
  def get[T](): Get[T] = Get()
  def connect[T](): Connect[T] = Connect()
  def delete[T](): Delete[T] = Delete()
  def head[T](): Head[T] = Head()
  def options[T](): Options[T] = Options()
  def path[T](): Path[T] = Path()
  def post[T](): Post[T] = Post()
  def put[T](): Put[T] = Put()
  def trace[T](): Trace[T] = Trace()

  def router[T](f: => List[Route]): Router[T] = StandardRouter(f)

  implicit def concatenationToList[T](concatenation: MethodConcatenation[T]): List[Route] = {
    null //TODO
  }
}

case class RouteResult[+T](target: T, pathPrms: Map[String, String], queryPrms: Map[String, List[String]]) {
  def q(name: String): Option[String] = queryPrms.get(name).flatMap(values => values.headOption)

  def p(name: String): Option[String] = pathPrms.get(name) orElse q(name)

  def params(name: String): (List[String], Option[String]) = (queryPrms.getOrElse(name, List.empty), pathPrms.get(name))
}

case class WithPath(path: String)
case class WithTarget[T]()