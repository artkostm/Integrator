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
	resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.bintrayRepo("scalameta", "maven"),
	addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
	libraryDependencies += scalext,
    libraryDependencies ++= kamonDeps,
	libraryDependencies += "io.frees" %% "frees-core"               % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-core"               % "0.4.1",
    libraryDependencies += "io.frees" %% "frees-effects"            % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-tagless"            % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-async"              % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-async-cats-effect"  % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-async-guava"        % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-cache"              % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-config"             % "0.4.1",
	libraryDependencies += "io.frees" %% "frees-logging"            % "0.4.1"
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