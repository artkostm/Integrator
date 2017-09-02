name := "Integrator"

scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.8"
)

import Dependencies._

lazy val root = (project in file(".")).aggregate(core, examples)

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= coreDeps
  )

lazy val examples = (project in file("examples"))
  .settings(
    commonSettings
  )
  .dependsOn(core)

lazy val sandbox = (project in file("sandbox"))
  .settings(
    commonSettings,
    libraryDependencies ++= coreDeps,
    libraryDependencies ++= sandboxDeps
  )