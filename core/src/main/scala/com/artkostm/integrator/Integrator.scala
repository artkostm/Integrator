package com.artkostm.integrator

import akka.actor.ActorSystem

import scala.util.control.NonFatal

object Integrator {
  lazy val ACTOR_SYSTEM_NAME = "Integrator"
  lazy val system = ActorSystem(ACTOR_SYSTEM_NAME)

  lazy val config = try { system.settings.config } catch {
    case NonFatal(e) =>
      println(s"Could not load application.conf. Details: ${e.getMessage}")
      throw e
  }
}


