package com.artkostm.integrator.routing

import java.util.UUID
import scala.concurrent.Future

object testdsl {
//  type Out = Future[Response]
//  sealed trait Method {
//    def on[R](routeDef: RouteDef[R]): R = routeDef.withMethod(this)
//  }
//  case object ANY extends Method
//  case object GET extends Method
//  case object POST extends Method
//
//  sealed trait PathElem
//  case class Static(name: String) extends PathElem
//  case object * extends PathElem
//
//  sealed trait RouteDef[Self] {
//    def withMethod(method: Method): Self
//    def method: Method
//    def elems: List[PathElem]
//  }
//  
//  case class RouteDef0(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef0] {
//    def withMethod(method: Method) = RouteDef0(method, elems)
//    def /(static: Static) = RouteDef0(method, elems :+ static)
//    def /(p: PathElem) = RouteDef1(method, elems :+ p)
//    def to(f0: () => Out) = Route0(this, f0)
//  }
//  case class RouteDef1(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef1]{
//    def withMethod(method: Method) = RouteDef1(method, elems)
//    def /(static: Static) = RouteDef1(method, elems :+ static)
//    def /(p: PathElem) = RouteDef2(method, elems :+ p)
//    def to[A: PathParam : Manifest](f1: (A) => Out) = Route1(this, f1)
//  }
//  case class RouteDef2(method: Method, elems: List[PathElem]) extends RouteDef[RouteDef2]{
//    def withMethod(method: Method) = RouteDef2(method, elems)
//    def /(static: Static) = RouteDef2(method, elems :+ static)
//    def to[A: PathParam : Manifest, B: PathParam : Manifest](f2: (A, B) => Out) = Route2(this, f2)
//  }
//  
//  implicit def stringToRouteDef0(name: String) = RouteDef0(ANY, Static(name) :: Nil)
//
//  implicit def asterixToRoutePath1(ast: *.type) = RouteDef1(ANY, ast :: Nil)
//  
//  implicit def stringToStatic(name: String) = Static(name)
//
//  sealed trait Route[RD] {
//    def routeDef: RouteDef[RD]
//  }
//  
//  case class Route0(routeDef: RouteDef0, f0: () => Out) extends Route[RouteDef0]
//  case class Route1[A: PathParam : Manifest](routeDef: RouteDef1, f2: (A) => Out) extends Route[RouteDef1] {
//    def apply(a: A) = PathMatcher1(routeDef.elems)(a)
//  }
//  case class Route2[A: PathParam : Manifest, B: PathParam : Manifest](routeDef: RouteDef2, f2: (A, B) => Out) extends Route[RouteDef2] {
//    def apply(a: A, b: B) = PathMatcher2(routeDef.elems)(a, b)
//  }
//  
//  trait PathParam[T]{
//    def apply(t: T): String
//    def unapply(s: String): Option[T]
//  }
//  
//  implicit val StringPathParam: PathParam[String] = new PathParam[String] {
//    def apply(s: String) = s
//    def unapply(s: String) = Some(s)
//  }
//  
//  implicit val BooleanPathParam: PathParam[Boolean] = new PathParam[Boolean] {
//    def apply(b: Boolean) = b.toString
//    def unapply(s: String) = s.toLowerCase match {
//      case "1" | "true" | "yes" => Some(true)
//      case "0" | "false" | "no" => Some(false)
//      case _ => None
//    }
//  }
//  
//  implicit val UUIDPathParam: PathParam[UUID] = new PathParam[UUID] {
//    def apply(uuid: UUID) = uuid.toString
//    def unapply(s: String) = try {
//      Some(UUID.fromString(s))
//    } catch {
//      case _ => None
//    }
//  }

  
}