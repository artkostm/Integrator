package com.artkostm.integrator

import io.netty.util.internal.ObjectUtil

/**
  * Created by arttsiom.chuiko on 12/01/2017.
  */
object Routing extends App{
  val test = "/path1/:var/path2/"
  println(Path.removeSlashesAtBothEnds(test))
}

class Path(path: String) {
  val tokens = Path.removeSlashesAtBothEnds(path).split("/")
}


object Path {

  def removeSlashesAtBothEnds(path: String): String = {
    ObjectUtil.checkNotNull(path, "path")

    if (path.isEmpty) return path

    path.stripPrefix("/").stripSuffix("/")
  }
}