lazy val commonSettings = Seq(
  version := "0.1.0"
)

import java.io.File

import Dependencies._

lazy val root = (project in file(".")).aggregate(core, examples)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    crossScalaVersions  := Seq("2.12.3", "2.11.8"),
    scalaVersion        := crossScalaVersions.value.head,
    scalacOptions      ++= Seq("-feature", "-deprecation", "-encoding", "utf-8", "-language:implicitConversions"),
    libraryDependencies ++= coreDeps
  )

lazy val examples = (project in file("examples"))
  .settings(resolvers += Resolver.bintrayRepo("kamon-io", "snapshots"))
  .settings(
    commonSettings,
    scalaVersion := "2.12.3",
    libraryDependencies += "io.kamon" %% "kamon-netty" % "1.0.0-RC1-2d0f0ab696b2949ced5ac8c286f47375e3503016",
	  libraryDependencies += "io.kamon" %% "kamon-core" % "1.0.0-RC1"
  )
  .settings(
    mainClass in assembly := Some("com.artkostm.integrator.example.netty.ServerApp")
  )
  .settings(
	  assemblyMergeStrategy in assembly := {
      {
        case PathList("org", "apache", _*) => MergeStrategy.last
			  case PathList("com", "google", _*) => MergeStrategy.last
			  case "log4j.properties" => MergeStrategy.last
        case x if x == s"META-INF${File.pathSeparator}io.netty.versions.properties" => MergeStrategy.concat
			  case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
				  oldStrategy(x)
      }
	  }
  )
  .dependsOn(core)

lazy val sandbox = (project in file("sandbox"))
  .settings(
    commonSettings,
    scalaVersion := "2.11.8",
    libraryDependencies ++= coreDeps,
    libraryDependencies ++= sandboxDeps
  )