package com.artkostm.integrator.routing

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.concurrent.Future

object scaladsl {
  type Action = Request => Future[Response]
}

/* 
:METHOD /path/path1 ? param1 & param2 { request =>

}
*/

trait MethodOps {
  private[routing] val path = Queue.empty[Linx[_, _]]
  
  def +(path: Linx[_, _]): MethodOps = {
    this.path += path
    this
  }
}

class Method extends MethodOps

object Method {
  class Get extends Method
  case object Post
  case object Put
  case object Delete
  import scaladsl._
  def apply(path: Linx[_, _], action: Action): Method = {
    null
  }
}