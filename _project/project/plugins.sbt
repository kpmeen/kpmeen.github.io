// Use coursier
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.3")
// Since this is a scala-js project...add the plugin
addSbtPlugin("org.scala-js"  % "sbt-scalajs"         % "0.6.29")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.15.0-0.6")
// Style and formatting
addSbtPlugin("org.scalameta"  %% "sbt-scalafmt"          % "2.0.4")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
