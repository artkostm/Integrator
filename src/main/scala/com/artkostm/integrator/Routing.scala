package com.artkostm.integrator

import io.netty.util.internal.ObjectUtil

import scala.collection.mutable

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

  def route(requestPathTokens: Array[String]): RouteResult[T]

  def path(target: T, params: Any*): String
}

class OrderlessRouter[T] extends Router[T] {
  val routes = mutable.Map.empty[Path, T]
  private val reverseRoutes = mutable.Map.empty[T, mutable.Set[Path]]

  override def addRoute(path: String, target: T): OrderlessRouter[T] = {
    val p = Path(path)
    if (routes.contains(p)) return this

    routes + (p -> target)
    addReverseRoute(target, p)
    this
  }

  override def removePath(path: String): Unit = ???

  override def removeTarget(target: T): Unit = ???

  override def anyMatched(requestPathTokens: Array[String]): Boolean = ???

  override def route(requestPathTokens: Array[String]): RouteResult[T] = ???

  override def path(target: T, params: Any*): String = ???

  private def addReverseRoute(target: T, path: Path): Unit = reverseRoutes.get(target) match {
    case Some(paths) => paths += path
    case None => reverseRoutes += (target -> mutable.Set(path))
  }
}
