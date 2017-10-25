import sbt._

object Dependencies {

  val akkaVersion = "2.5.3"
  val nd4jVersion = "0.8.0"
  val kamonVersion = "0.6.7"
  val kamonAkkaVersion = "0.6.8"

  val reflect = "org.scala-lang" % "scala-reflect" % "2.12.3"

  val cnfgs = "com.github.kxbmap" %% "configs" % "0.4.4"
  val akka_actor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akka_stream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akka_protobuf = "com.typesafe.akka" %% "akka-protobuf" % akkaVersion
  val netty_all = "io.netty" % "netty-all" % "4.1.15.Final"
  val netty_tcnative = "io.netty" % "netty-tcnative-boringssl-static" % "2.0.6.Final"
  val guava = "com.google.guava" % "guava" % "18.0"
  val cats = "org.typelevel" %% "cats" % "0.9.0"
  val classutil= "org.clapper" %% "classutil" % "1.1.2"
  val nd4j_platform = "org.nd4j" % "nd4j-native-platform" % nd4jVersion
  val nd4s = "org.nd4j" %% "nd4s" % nd4jVersion
  val scalnet = "org.deeplearning4j" %% "scalnet" % nd4jVersion
  val spark_core = "org.apache.spark" %% "spark-core" % "2.1.1"
  val clump = "io.getclump" %% "clump-scala" % "1.1.0"
  val javassist = "org.javassist" % "javassist" % "3.22.0-CR2"
  val linx = "no.arktekk" %% "linx" % "0.4"
  val webjars = "org.webjars" % "webjars-locator-core" % "0.34"
  //libraryDependencies += "tv.cntt" %% "sclasner" % "1.7.0"
  val scalext = "com.bfil" %% "scalext" % "0.4.0-SNAPSHOT"

  /** Kamon dependencies */
  val aspectj = "org.aspectj" % "aspectjweaver" % "1.8.12"
  val kamonCore = "io.kamon" %% "kamon-core" % kamonVersion
  val kamonStatsD = "io.kamon" %% "kamon-statsd" % kamonVersion
  val kamonScala = "io.kamon" %% "kamon-scala" % kamonVersion
  val kamonAkka = "io.kamon" %% "kamon-akka-2.5" % kamonAkkaVersion
  val kamonSystem = "io.kamon" %% "kamon-system-metrics" % kamonVersion
  //libraryDependencies += "io.kamon" %% "kamon-netty" % "1.0.0-RC1-2d0f0ab696b2949ced5ac8c286f47375e3503016",

  val kamonDeps = Seq(aspectj, kamonCore, kamonStatsD, kamonScala, kamonAkka, kamonSystem)
  val coreDeps = Seq(cnfgs, akka_actor, akka_protobuf, akka_stream, netty_all, 
	  guava, cats, classutil, reflect, netty_tcnative, javassist, webjars)
  val sandboxDeps = Seq(nd4j_platform, nd4s, scalnet, spark_core, clump, linx)
}
