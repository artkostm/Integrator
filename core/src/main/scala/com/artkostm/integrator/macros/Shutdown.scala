package com.artkostm.integrator.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Shutdown {
  def shutdown: Unit = macro shutdownImpl

  def shutdownImpl(c: blackbox.Context): c.Expr[Unit] = {
    import c.universe._
    c.Expr(q"""println("hello!")""")
  }
}

/**

Runtime.getRuntime.addShutdownHook(new Thread {
      override def run { webServer.stop() }
    })

  */