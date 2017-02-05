package com.artkostm.integrator

import io.netty.util.internal.ObjectUtil

/**
  * Created by arttsiom.chuiko on 12/01/2017.
  */
object Routing extends App{
  val test = "/path1/:var/path2/"
  //println(Path.removeSlashesAtBothEnds(test))

  val path = Path(test)
  path.`match`(Array.empty[String], Map.empty)
}

case class Path(path: String) {
  val tokens = Path.removeSlashesAtBothEnds(path).split("/")

  def `match`(requestPathTokens: Array[String], params: Map[String, String]): Boolean = {
    println(tokens.toList)
    tokens.zipWithIndex.map(Function.tupled((a, b) => {
      println(s"$b)$a")
    }))

    true
  }

}


object Path {

  def removeSlashesAtBothEnds(path: String): String = {
    ObjectUtil.checkNotNull(path, "path")

    if (path.isEmpty) return path

    path.stripPrefix("/").stripSuffix("/")
  }
}

object RouteResult {
//  def apply[T](target: T, pathParams: Map[String, String], queryParams: Map[String, List[String]]): RouteResult[T] =
//    RouteResult(ObjectUtil.checkNotNull(target, "target"),
//      ObjectUtil.checkNotNull(pathParams, "pathParams"),
//      ObjectUtil.checkNotNull(queryParams, "queryParams"))
}

case class RouteResult[T](target: T, pathParams: Map[String, String], queryParams: Map[String, List[String]]) {
  def queryParam(name: String): Option[String] = queryParams.get(name).map(values => values.head)

  def param(name: String): Option[String] = pathParams.get(name) orElse queryParam(name)

  def params(name: String): List[String] =  List concat (queryParams.get(name).getOrElse(List.empty), pathParams.get(name))
}

trait Router[T] {
  def addRoute(path: String, target:T): Router[T]

  def removePath(path: String): Unit

  def removeTarget(target: T): Unit

  def anyMatched(requestPathTokens: Array[String]): Boolean
}

