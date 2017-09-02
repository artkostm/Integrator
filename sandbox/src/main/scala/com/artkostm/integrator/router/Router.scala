package com.artkostm.integrator.router

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by artsiom.chuiko on 12/03/2017.
  *
  *
  */

trait RouteMatcher {
  def anyMatched(requestPathTokens: Array[String]): Boolean
}

sealed trait Route {
  def path: String
}

class |[+T](val curr: MethodConcatenation[T], val prev: MethodConcatenation[T]) extends MethodConcatenation[T]

sealed trait MethodConcatenation[+T] {
  def |[S >: T](method: MethodConcatenation[S]): MethodConcatenation[S] = new |[S](method, this)
}

sealed trait TargetHandler[+T] {
  def ->[S >: T](f: => S): MethodConcatenation[S]
}

sealed trait HttpMethod[+T] extends TargetHandler[T] with MethodConcatenation[T] with Route { self =>
  private var pathStr: String = _
  private var targetHolder: Any = _

  def target: T = targetHolder.asInstanceOf[T]

  override def path: String = pathStr

  def /(path: String): TargetHandler[T] = {
    pathStr = path
    self
  }

  override def ->[S >: T](f: => S) = {
    targetHolder = f
    self
  }
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
  override def print(): Unit = routes.foreach {
    case route => println(s"$route: ${route.path}")
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
    val buffer = ListBuffer.empty[Route]
    getNextConcatenationAsRoute(concatenation, buffer)
    buffer.toList.reverse //is particular order necessary?
  }

  @tailrec
  private def getNextConcatenationAsRoute[T](c: MethodConcatenation[T], buffer: ListBuffer[Route]): Unit = {
    if (c.isInstanceOf[|[T]]) {
      buffer += c.asInstanceOf[|[T]].curr.asInstanceOf[Route]
      getNextConcatenationAsRoute(c.asInstanceOf[|[T]].prev, buffer)
    } else if (c.isInstanceOf[Route]) {
      buffer += c.asInstanceOf[Route]
    }
  }
}

case class RouteResult[+T](target: T, pathPrms: Map[String, String], queryPrms: Map[String, List[String]]) {
  def q(name: String): Option[String] = queryPrms.get(name).flatMap(values => values.headOption)

  def p(name: String): Option[String] = pathPrms.get(name) orElse q(name)

  def params(name: String): (List[String], Option[String]) = (queryPrms.getOrElse(name, List.empty), pathPrms.get(name))
}

case class WithPath(path: String)
case class WithTarget[T]()