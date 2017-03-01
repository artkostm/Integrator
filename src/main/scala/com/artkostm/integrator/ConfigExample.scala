package com.artkostm.integrator

import io.netty.handler.codec.http.HttpMethod

//import com.typesafe.config.ConfigFactory
//import com.typesafe.config.Config
//import classy.config._
//
///**
//  * Created by artsiom.chuiko on 10/01/2017.
//  */
//case class Routes(routes: List[RouteHolder])
//case class Template(directory: Option[String])
//case class Netty(host: Option[String], port: Option[Int])
//case class RouteHolder(`class`: Option[String], name: Option[String], spin: Option[Int] = Some(1))
object ConfigExample extends App {
  val router = new Router[String]("not found")
    .get("/articles", "indexHandler")
    .get("/articles/:id", "showHandler")

  println(router.path(HttpMethod.GET, "indexHandler"))
//  val conf =
//    """
//      |routes = [
//      |  GET /path1 {
//      |    class : com.artkostm.integrator.TestActor1
//      |    spin: 1
//      |    name: first
//      |  },
//      |
//      |  GET /path2 {
//      |    class : "com.artkostm.integrator.TestActor1"
//      |    name: index
//      |    spin: 5
//      |  },
//      |
//      |  POST /path1 {
//      |    class : "com.artkostm.integrator.TestActor2"
//      |    name: postactor
//      |  }
//      |]
//      |
//      |app {
//      |  template {
//      |    directory = "core/src/test/resources/pages"
//      |  }
//      |
//      |  netty {
//      |    host : "0.0.0.0"
//      |    port : 8080
//      |  }
//      |}
//    """.stripMargin
//
//  val decodeRoutes: ConfigDecoder[RouteHolder] = readConfig[Option[String]]("class").join(
//    readConfig[Option[String]]("name")).join(
//    readConfig[Option[Int]]("spin")).map(RouteHolder.tupled)
//  val manualDecoder = readConfig[List[Config]]("routes") andThen decodeRoutes.sequence
//
//  val rawConfig = ConfigFactory.parseString(conf)
//
//  println(manualDecoder(rawConfig))
}