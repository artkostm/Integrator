package com.artkostm.integrator.router

/**
  * Created by artsiom.chuiko on 12/03/2017.
  *
  *
  */

trait RouteMatcher {
  def anyMatched(requestPathTokens: Array[String]): Boolean
}

trait HttpMethod {

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
  def get(): Unit = {}
  def connect(): Unit = {}
  def delete(): Unit = {}
  def head(): Unit = {}
  def options(): Unit = {}
  def path(): Unit = {}
  def post(): Unit = {}
  def put(): Unit = {}
  def trace(): Unit = {}
}

case class RouteResult[+T](target: T, pathPrms: Map[String, String], queryPrms: Map[String, List[String]]) {
  def q(name: String): Option[String] = queryPrms.get(name).flatMap(values => values.headOption)

  def p(name: String): Option[String] = pathPrms.get(name) orElse q(name)

  def params(name: String): (List[String], Option[String]) = (queryPrms.getOrElse(name, List.empty), pathPrms.get(name))
}