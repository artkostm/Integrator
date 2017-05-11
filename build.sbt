name := "Integrator"

version := "1.0"

scalaVersion := "2.12.1"

val cnfgs = "com.github.kxbmap" % "configs_2.12" % "0.4.4"
val akka_actor = "com.typesafe.akka" % "akka-actor_2.12" % "2.4.16"
val netty_all = "io.netty" % "netty-all" % "4.1.6.Final"

libraryDependencies += cnfgs
libraryDependencies += akka_actor
libraryDependencies += netty_all
libraryDependencies += "com.google.guava" % "guava" % "16.0"
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"