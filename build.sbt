lazy val commonSettings = Seq(
  version := "0.1.0"
)

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
  .settings(resolvers += "BFil Nexus Snapshots" at "http://nexus.b-fil.com/nexus/content/groups/public/")
  .settings(
    commonSettings,
    scalaVersion := "2.12.3",
    //libraryDependencies += "io.kamon" %% "kamon-netty" % "1.0.0-RC1-2d0f0ab696b2949ced5ac8c286f47375e3503016",
	  libraryDependencies += "io.kamon" %% "kamon-core" % "0.6.7",
    libraryDependencies += "io.kamon" %% "kamon-statsd" % "0.6.7",//0.6.7
    libraryDependencies += "io.kamon" %% "kamon-scala" % "0.6.7",
    libraryDependencies += "io.kamon" %% "kamon-system-metrics" % "0.6.7",
    libraryDependencies += "io.kamon" %% "kamon-akka-2.5" % "0.6.8",
    libraryDependencies += "org.aspectj" % "aspectjweaver" % "1.8.12",
	  libraryDependencies += scalext
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
        case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.concat
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