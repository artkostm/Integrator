package com.artkostm.integrator

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem}

/**
  * Created by artsiom.chuiko on 10/01/2017.
  */
object ConfigExample extends App {
  implicit val actorSystem = ActorSystem("test_system")

  Configuration.get(actorSystem).createRoutes()
}

class TestActor extends Actor {
  override def receive: Receive = ???
}
