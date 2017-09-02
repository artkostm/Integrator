package com.artkostm.integrator.sandbox

/**
  * Created by artsiom.chuiko on 21/06/2017.
  */
import org.nd4s.Implicits._
//import org.nd4j.linalg.factory.Nd4j

object Application extends App {
  val arr = (1 to 9).asNDArray(3,3)
  println(arr)
}