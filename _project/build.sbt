name := """blaargh"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  """-deprecation""",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

lazy val root = (project in file(".")).enablePlugins(ScalaJSPlugin)

sourcesInBase := false

// Create launcher file that searches for an object that extends JSApp.
// Make sure there is only one!
persistLauncher := true
persistLauncher in Test := false

scalaJSStage in Global := FastOptStage

// See more at: http://typesafe.com/blog/improved-dependency-management-with-sbt-0137#sthash.7hS6gFEu.dpuf
updateOptions := updateOptions.value.withCachedResolution(true)

// Dependency management...
val scalaJSReactVersion = "0.11.0"
val scalaCssVersion = "0.4.1"
val scalazVersion = "7.1.2"
val monocleVersion = "1.2.0"

libraryDependencies ++= Seq(
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
  "org.scala-js" %%% "scalajs-dom" % "0.9.0",
  "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
  "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
  "com.github.japgolly.scalajs-react" %%% "ext-scalaz72" % scalaJSReactVersion,
  "com.github.japgolly.scalajs-react" %%% "ext-monocle" % scalaJSReactVersion,
  "com.github.japgolly.fork.monocle" %%%! s"monocle-core" % monocleVersion,
  "com.github.japgolly.fork.monocle" %%%! s"monocle-macro" % monocleVersion,
  "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
  "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
  "com.lihaoyi" %%% "upickle" % "0.3.8"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.yaml" % "snakeyaml" % "1.17" % "test",
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
  "com.lihaoyi" % "ammonite-ops_2.11" % "0.5.6" % "test"
)

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""


val reactJSVersion = "15.0.1"

jsDependencies ++= Seq(
  "org.webjars.bower" % "react" % reactJSVersion / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
  "org.webjars.bower" % "react" % reactJSVersion / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
  "org.webjars.bower" % "react" % reactJSVersion / "react-dom-server.js" minified "react-dom-server.min.js" dependsOn "react-dom.js" commonJSName "ReactDOMServer"
)

// creates single js resource file for easy integration in html page
skip in packageJSDependencies := false

// copy javascript files to js folder,that are generated using fastOptJS/fullOptJS
val jsTarget = "../assets/js"
crossTarget in(Compile, fullOptJS) := file(jsTarget)
crossTarget in(Compile, fastOptJS) := file(jsTarget)
crossTarget in(Compile, packageJSDependencies) := file(jsTarget)
crossTarget in(Compile, packageScalaJSLauncher) := file(jsTarget)
crossTarget in(Compile, packageMinifiedJSDependencies) := file(jsTarget)

artifactPath in(Compile, fastOptJS) := ((crossTarget in(Compile, fastOptJS)).value / ((moduleName in fastOptJS).value + "-opt.js"))
artifactPath in(Compile, fullOptJS) := ((crossTarget in(Compile, fullOptJS)).value / ((moduleName in fullOptJS).value + "-opt.js"))
