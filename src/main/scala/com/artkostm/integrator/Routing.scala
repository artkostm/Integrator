package com.artkostm.integrator

import io.netty.util.internal.ObjectUtil
import router.Path

import scala.collection.mutable

/**
  * Created by artsiom.chuiko on 12/01/2017.
  */
object Routing extends App{
  val test = "/path1/:var/path2/"
  //println(Path.removeSlashesAtBothEnds(test))

  val path = Path(test)
  path.`match`(Array.empty[String], mutable.Map.empty)
}

case class Path(path: String) {
  val tokens = Path.removeSlashesAtBothEnds(path).split("/")

  def `match`(requestPathTokens: Array[String], params: mutable.Map[String, String]): Boolean = {
    tokens.length match {
      case l if l == requestPathTokens.length => tokens.zipWithIndex.foreach({
        case (a, i) => {
          val value = requestPathTokens(i)
          if (a.length > 0 && a.charAt(0) == ':') params += (a.substring(1) -> value) // This is a placeholder
          else if (a != value) return false // This is a constant
          return true
        }
      })
      case l if l > 0 &&
        tokens(l - 1) == ":*" &&
        l <= requestPathTokens.length => tokens.iterator.slice(0, tokens.length - 1).zipWithIndex.foreach({
        case (a, i) => {
          val value = requestPathTokens(i)
          if (a.length > 0 && a.charAt(0) == ':') params += (a.substring(1) -> value) // This is a placeholder
          else if (a != value) return false // This is a constant
          return true
        }
      })
    }

    val b = new StringBuilder(requestPathTokens(tokens.length - 1))
    for (i <- tokens.length to requestPathTokens.length) {
      b += '/'
      b ++= requestPathTokens(i)
    }

    params += ("*" -> b.toString)
    true
  }

}

object Path {
  def removeSlashesAtBothEnds(path: String): String = ObjectUtil.checkNotNull(path, "path").stripPrefix("/").stripSuffix("/")
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

  /** Checks if there's any matching route. */
  def anyMatched(requestPathTokens: Array[String]): Boolean

  def route(path: String): Option[RouteResult[T]]

  def route(requestPathTokens: Array[String]): Option[RouteResult[T]]

  def path(target: T, params: Any*): Option[String]
}

/**
  * Router that doesn't contain information about HTTP request methods and route matching orders.
  * @tparam T
  */
class OrderlessRouter[T] extends Router[T] {
  val routes = mutable.Map.empty[Path, T]
  private val reverseRoutes = mutable.Map.empty[T, mutable.Set[Path]]

  override def addRoute(path: String, target: T): OrderlessRouter[T] = {
    val p = Path(path)
    if (routes.contains(p)) return this
    routes += (p -> target)
    addReverseRoute(target, p)
    this
  }

  override def removePath(path: String): Unit = {
    val p = Path(path)
    routes.remove(p).foreach(reverseRoutes.remove(_).foreach(_.remove(p)))
  }

  override def removeTarget(target: T): Unit = reverseRoutes.remove(target).foreach(_.map(routes.remove(_)))

  override def anyMatched(requestPathTokens: Array[String]): Boolean = {
    val params = mutable.Map.empty[String, String]
    routes.keysIterator.exists({params.clear(); _.`match`(requestPathTokens, params)})
  }

  override def route(path: String): Option[RouteResult[T]] = route(Path.removeSlashesAtBothEnds(path).split("/"))

  override def route(requestPathTokens: Array[String]): Option[RouteResult[T]] = {
    val params = mutable.Map.empty[String, String]
    routes.find({params.clear(); _._1.`match`(requestPathTokens, params)}).map(entry => RouteResult(entry._2, params.toMap, Map.empty)).headOption
  }

  override def path(target: T, params: Any*): Option[String] = params.length match {
    case 0 => path(target, Map.empty)
    case l if l == 1 && params(0).isInstanceOf[Map[Any, Any]] => pathMap(target, params(0).asInstanceOf[Map[Any, Any]])
    case l if l % 2 == 1 => throw new IllegalArgumentException(s"Missing value for param: ${params(l - 1)}")
    case _ => pathMap(target, params.grouped(2).map(list => (list(0), list(1))).toMap)
  }

  private def addReverseRoute(target: T, path: Path): Unit = reverseRoutes.get(target) match {
    case Some(paths) => paths += path
    case None => reverseRoutes += (target -> mutable.Set(path))
  }

  private def pathMap(target: T, params: Map[Any, Any]): Option[String] = {
//    reverseRoutes.get(target)
    None
  }
}

/**
  * Router that contains information about route matching orders, but doesn't
  * contain information about HTTP request methods.
  *
  * Routes are devided into 3 sections: "first", "last", and "other".
  * Routes in "first" are matched first, then in "other", then in "last".
  * @tparam T
  */
class MethodlessRouter[T] extends Router[T] {
  val first = new OrderlessRouter[T]
  val other = new OrderlessRouter[T]
  val last = new OrderlessRouter[T]

  def size: Int = first.routes.size + other.routes.size + last.routes.size

  def addRouteFirst(path: String, target: T): MethodlessRouter[T] = { first.addRoute(path, target); this }

  override def addRoute(path: String, target: T): MethodlessRouter[T] = { other.addRoute(path, target); this }

  def addRouteLast(path: String, target: T): MethodlessRouter[T] = { last.addRoute(path, target); this }

  override def removePath(path: String): Unit = {
    first.removePath(path)
    other.removePath(path)
    last.removePath(path)
  }

  override def removeTarget(target: T): Unit = {
    first.removeTarget(target)
    other.removeTarget(target)
    last.removeTarget(target)
  }

  override def anyMatched(requestPathTokens: Array[String]): Boolean = first.anyMatched(requestPathTokens) ||
    other.anyMatched(requestPathTokens) ||
    last.anyMatched(requestPathTokens)

  override def route(path: String): Option[RouteResult[T]] = route(Path.removeSlashesAtBothEnds(path).split("/"))

  override def route(requestPathTokens: Array[String]): Option[RouteResult[T]] = first.route(requestPathTokens) orElse other.route(requestPathTokens) orElse last.route(requestPathTokens)

  override def path(target: T, params: Any*): Option[String] = first.path(target, params) orElse other.path(target, params) orElse last.path(target, params)
}
