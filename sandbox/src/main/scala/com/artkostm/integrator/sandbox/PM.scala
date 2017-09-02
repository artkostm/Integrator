package com.artkostm.integrator.sandbox

/**
  * Created by artsiom.chuiko on 09/06/2017.
  */
object Pm extends App {
  val arr = Array("1", "2", "3", "4")
  val tuple: (String, String, String, String) = ("1", "2", "3", "4")

  arr match {
    case Array("1", _*) => println("1!")
  }

  arr match {
    case Array("1", rest@_*) => println(s"rest: $rest")
  }

  tuple.productIterator.toList match {
    case _ :: "2" :: rest => println(s"Wow! rest: $rest")
  }
}
