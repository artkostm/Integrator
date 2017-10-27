logLevel := Level.Warn

resolvers += Resolver.bintrayIvyRepo("kamon-io", "sbt-plugins")
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC11")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.3")
addSbtPlugin("com.dwijnand" % "sbt-project-graph" % "0.2.2")
addSbtPlugin("io.kamon" % "sbt-aspectj-runner" % "1.0.3")
//addSbtPlugin("io.frees" % "sbt-freestyle" % "0.3.7")
//addSbtPlugin("com.typelead" % "sbt-eta" % "0.1.0")