name := "Integrator"

version := "1.0"

scalaVersion := "2.11.8"

val cnfgs = "com.github.kxbmap" %% "configs" % "0.4.4"
val akka_actor = "com.typesafe.akka" %% "akka-actor" % "2.4.16"
val netty_all = "io.netty" % "netty-all" % "4.1.6.Final"

libraryDependencies += cnfgs
libraryDependencies += akka_actor
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.3"
libraryDependencies += "com.typesafe.akka" %% "akka-protobuf" % "2.5.3"

libraryDependencies += netty_all
libraryDependencies += "com.google.guava" % "guava" % "18.0"
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"

libraryDependencies += "io.suzaku" %% "diode" % "1.1.2"

libraryDependencies += "org.clapper" %% "classutil" % "1.1.2"

val nd4jVersion = "0.8.0"

libraryDependencies += "org.nd4j" % "nd4j-native-platform" % nd4jVersion
libraryDependencies += "org.nd4j" %% "nd4s" % nd4jVersion
libraryDependencies += "org.deeplearning4j" %% "scalnet" % nd4jVersion

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"

//resolvers ++= Seq(
//  "Sonatype OSS Snapshots" at
//    "https://oss.sonatype.org/content/repositories/snapshots",
//  "Sonatype OSS Releases" at
//    "https://oss.sonatype.org/content/repositories/releases"
//)
//libraryDependencies ++= Seq(
//  "com.storm-enroute" % "macrogl_2.11" % "0.4-SNAPSHOT")
//
//enablePlugins(UniversalPlugin)
//javacOptions in Universal ++= Seq(
//  "-Djava.library.path=lib/native"
//)