name := """blaargh"""

version := "1.0"

scalaVersion := "2.12.4"

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

mainClass in Compile := Some("net.scalytica.blaargh.App")

// Create launcher file that searches for an object that extends JSApp.
// Make sure there is only one!
scalaJSUseMainModuleInitializer := true
scalaJSUseMainModuleInitializer in Test := false

scalaJSStage in Global := FastOptStage

// See more at: http://typesafe.com/blog/improved-dependency-management-with-sbt-0137#sthash.7hS6gFEu.dpuf
updateOptions := updateOptions.value.withCachedResolution(true)

// Dependency management...
val scalaJSReactVersion = "1.1.1"
val scalaCssVersion = "0.5.3"
val scalazVersion = "7.2.7"
val monocleVersion = "1.4.0"

val scalaJsReactLibs = Seq(
  "core",
  "extra",
  "ext-scalaz72",
  "ext-monocle"
)

libraryDependencies ++= Seq(
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  "be.doeraene"                  %%% "scalajs-jquery" % "0.9.1",
  "org.scala-js"                 %%% "scalajs-dom"    % "0.9.3",
  "com.github.julien-truffaut"   %%% "monocle-core"   % monocleVersion,
  "com.github.julien-truffaut"   %%% "monocle-macro"  % monocleVersion,
  "com.github.japgolly.scalacss" %%% "core"           % scalaCssVersion,
  "com.github.japgolly.scalacss" %%% "ext-react"      % scalaCssVersion,
  "com.typesafe.play"            %%% "play-json"      % "2.6.6"
)

libraryDependencies ++= scalaJsReactLibs.map { lib =>
  "com.github.japgolly.scalajs-react" %%% lib % scalaJSReactVersion
}

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"    % "3.0.4" % "test",
  "com.lihaoyi"   %% "ammonite-ops" % "1.0.3" % "test",
  "org.yaml"      % "snakeyaml"     % "1.17"  % "test",
  "org.pegdown"   % "pegdown"       % "1.6.0" % "test"
)

initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""

val reactJSVersion = "15.0.1"

val reactWebJars = Seq(
  "-with-addons" -> "React",
  "-dom"         -> "ReactDOM",
  "-dom-server"  -> "ReactDOMServer"
)

jsDependencies ++= reactWebJars.map { lib =>
  "org.webjars.bower" % "react" % reactJSVersion / s"${lib._1}.js" minified s"${lib._1}.min.js" commonJSName lib._2
}

// creates single js resource file for easy integration in html page
skip in packageJSDependencies := false

// copy javascript files to js folder,that are generated using fastOptJS/fullOptJS
val jsTarget = "../assets/js"
crossTarget in (Compile, fullOptJS) := file(jsTarget)
crossTarget in (Compile, fastOptJS) := file(jsTarget)
crossTarget in (Compile, packageJSDependencies) := file(jsTarget)
crossTarget in (Compile, scalaJSUseMainModuleInitializer) := file(jsTarget)
crossTarget in (Compile, packageMinifiedJSDependencies) := file(jsTarget)

artifactPath in (Compile, fastOptJS) := ((crossTarget in (Compile, fastOptJS)).value / ((moduleName in fastOptJS).value + "-opt.js"))
artifactPath in (Compile, fullOptJS) := ((crossTarget in (Compile, fullOptJS)).value / ((moduleName in fullOptJS).value + "-opt.js"))
