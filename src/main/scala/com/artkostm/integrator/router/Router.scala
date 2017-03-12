package com.artkostm.integrator.router

/**
  * Created by artsiom.chuiko on 12/03/2017.
  *
  *
  */

trait RouteMatcher {
  def anyMatched(requestPathTokens: Array[String]): Boolean
}

object RoutingDsl {
  trait path {
    def >>(f: => AnyRef): AnyRef = f
  }
  trait test {

    def /(f: String): AnyRef = f
  }
  object GET extends test {
//    override def /(path: String): path = this
  }
}

case class RouteResult[+T](target: T, pathPrms: Map[String, String], queryPrms: Map[String, List[String]]) {
  def q(name: String): Option[String] = queryPrms.get(name).flatMap(values => values.headOption)

  def p(name: String): Option[String] = pathPrms.get(name) orElse q(name)

  def params(name: String): (List[String], Option[String]) = (queryPrms.getOrElse(name, List.empty), pathPrms.get(name))
}