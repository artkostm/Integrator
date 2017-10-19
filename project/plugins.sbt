logLevel := Level.Warn

resolvers += Resolver.bintrayIvyRepo("kamon-io", "sbt-plugins")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.2")
addSbtPlugin("com.dwijnand" % "sbt-project-graph" % "0.2.2")
//addSbtPlugin("io.kamon" % "sbt-aspectj-runner" % "1.0.3")
addSbtPlugin("com.typelead" % "sbt-eta" % "0.1.0")