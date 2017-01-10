package com.artkostm.integrator

import akka.actor.{Actor, ActorSystem}
import akka.event.Logging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by artsiom.chuiko on 10/01/2017.
  */
object ConfigExample extends App {

  implicit val actorSystem = ActorSystem("test_system")

  Configuration.get(actorSystem).createRoutes()

  Await.result(actorSystem.terminate(), Duration.create(2, "s"))
}

class TestActor1 extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case "test" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}

class TestActor2 extends TestActor1