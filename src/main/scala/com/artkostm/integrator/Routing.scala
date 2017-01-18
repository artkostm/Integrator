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