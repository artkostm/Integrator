package com.artkostm.integrator.routing

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

object scaladsl {
  
}

/* 
:METHOD /path/path1 ? param1 & param2 { request =>

}
*/

trait MethodOps {
  private[routing] val path = Queue.empty[String]
//  def /(root: String): MethodOps = {
//    path += root
//    this
//  }
  
  def +(path: Linx[_, _]): MethodOps = {
    
    this
  }
  
  def ?(paramName: String): Unit = {}
}

class Method extends MethodOps

object Method {
  case object Get extends Method
  case object Post
  case object Put
  case object Delete
}