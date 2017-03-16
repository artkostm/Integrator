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

sealed trait HttpMethod {

}

object HttpMethod {
  case class Get() extends HttpMethod
  case class Connect() extends HttpMethod
  case class Delete() extends HttpMethod
  case class Head() extends HttpMethod
  case class Options() extends HttpMethod
  case class Path() extends HttpMethod
  case class Post() extends HttpMethod
  case class Put() extends HttpMethod
  case class Trace() extends HttpMethod
}

object RoutingDsl {
  def get(): Get = Get()
  def connect(): Connect = Connect()
  def delete(): Delete = Delete()
  def head(): Head = Head()
  def options(): Options = Options()
  def path(): Path = Path()
  def post(): Post = Post()
  def put(): Put = Put()
  def trace(): Trace = Trace()
}

case class RouteResult[+T](target: T, pathPrms: Map[String, String], queryPrms: Map[String, List[String]]) {
  def q(name: String): Option[String] = queryPrms.get(name).flatMap(values => values.headOption)

  def p(name: String): Option[String] = pathPrms.get(name) orElse q(name)

  def params(name: String): (List[String], Option[String]) = (queryPrms.getOrElse(name, List.empty), pathPrms.get(name))
}