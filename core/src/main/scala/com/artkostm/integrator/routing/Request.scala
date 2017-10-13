package com.artkostm.integrator.routing

import scala.language.dynamics

case class Query [integrator](params: Map[String, String]) extends Dynamic {
  def selectDynamic(tag: String): Option[String] = params.get(tag)
  
  def some(): Unit = {}
}

class Request(params: Map[String, String]) {
  lazy val query = Query(params)
}

class Response