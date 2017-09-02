package com.artkostm.integrator.sandbox

import java.io.File

import akka.actor.Actor
import org.clapper.classutil.{ClassFinder, MapToBean, ScalaObjectToBean, ClassUtil => _c}

/**
  * Created by artsiom.chuiko on 19/06/2017.
  */

class MyActor {
  def receive = ""
}
object Classes extends App {
  println(_c.classSignature(classOf[MyActor]))
  val actor = new MyActor
  val map = Map("first" -> 1, "second" -> "2")
  val bean = MapToBean(map, true)
  println(bean.getClass)


  val finder = ClassFinder()
  val classes = finder.getClasses // classes is an Iterator[ClassInfo]
//  classes.foreach(println)
  val myClasses = ClassFinder.concreteSubclasses("com.artkostm.integrator.Configuration", classes)
  myClasses.foreach(println)
}
