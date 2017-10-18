import sbt._

object Dependencies {

  val akkaVersion = "2.5.3"
  val nd4jVersion = "0.8.0"

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
  //libraryDependencies += "tv.cntt" %% "sclasner" % "1.7.0"


  val coreDeps = Seq(cnfgs, akka_actor, akka_protobuf, akka_stream, netty_all, 
	  guava, cats, classutil, reflect, netty_tcnative, javassist)
  val sandboxDeps = Seq(nd4j_platform, nd4s, scalnet, spark_core, clump, linx)
}
