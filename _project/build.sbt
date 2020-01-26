name := """scalytica.net"""

version := "1.0"

scalaVersion := "2.12.10"

scalacOptions ++= Seq(
  "-feature",
  """-deprecation""",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

lazy val root =
  (project in file(".")).enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

sourcesInBase := false

mainClass in Compile := Some("net.scalytica.blaargh.App")

// Create launcher file that searches for an object that extends JSApp.
// Make sure there is only one!
scalaJSUseMainModuleInitializer := true
scalaJSUseMainModuleInitializer in Test := false

scalaJSStage in Global := FastOptStage

onChangedBuildSource in Global := ReloadOnSourceChanges

updateOptions := updateOptions.value.withCachedResolution(true)

// Dependency management...
val scalaJSReactVersion  = "1.6.0"
val scalaCssVersion      = "0.5.6"
val scalaJsDomVersion    = "0.9.7"
val scalaJsJQueryVersion = "0.9.5"

val scalazVersion    = "7.2.7"
val monocleVersion   = "1.5.0"
val ammoniteVersion  = "1.7.4"
val snakeYamlVersion = "1.25"
val pegdownVersion   = "1.6.0"
val playJsonVersion  = "2.7.4"
val scalaTestVersion = "3.0.8"

val scalaJsReactLibs = Seq(
  "core",
  "extra",
  "ext-scalaz72",
  "ext-monocle"
)

libraryDependencies ++= Seq(
  compilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
  ),
  "be.doeraene"                  %%% "scalajs-jquery" % scalaJsJQueryVersion,
  "org.scala-js"                 %%% "scalajs-dom"    % scalaJsDomVersion,
  "com.github.julien-truffaut"   %%% "monocle-core"   % monocleVersion,
  "com.github.julien-truffaut"   %%% "monocle-macro"  % monocleVersion,
  "com.github.japgolly.scalacss" %%% "core"           % scalaCssVersion,
  "com.github.japgolly.scalacss" %%% "ext-react"      % scalaCssVersion,
  "com.typesafe.play"            %%% "play-json"      % playJsonVersion
)

libraryDependencies ++= scalaJsReactLibs.map { lib =>
  "com.github.japgolly.scalajs-react" %%% lib % scalaJSReactVersion
}

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"    % scalaTestVersion % "test",
  "com.lihaoyi"   %% "ammonite-ops" % ammoniteVersion  % "test",
  "org.yaml"      % "snakeyaml"     % snakeYamlVersion % "test",
  "org.pegdown"   % "pegdown"       % pegdownVersion   % "test"
)

val reactJSVersion = "16.8.0"

npmDependencies in Compile ++= Seq(
  "react",
  "react-dom"
).map(l => l -> reactJSVersion)

// creates single js resource file for easy integration in html page
skip in packageJSDependencies := false

val jsTarget = "../assets/js"

lazy val fastBlaargh = taskKey[Unit]("Runs compile and webpack using fastOptJS")
lazy val fullBlaargh = taskKey[Unit]("Runs compile and webpack using fullOptJS")

fastBlaargh := {
  def setTargetFileName(origFileName: String): String = {
    val fname = name.value.replaceAllLiterally(".", "-")
    if (origFileName.endsWith(".map")) s"$fname.js.map"
    else s"$fname.js"
  }

  val webpackFiles = (webpack in (Compile, fastOptJS)).value

  val bundleFiles = webpackFiles.filter { af =>
    af.metadata
      .get(BundlerFileTypeAttr)
      .contains(BundlerFileType.ApplicationBundle) || af.data.name.endsWith(
      ".js.map"
    )
  }.map(af => af.data -> setTargetFileName(af.data.name))
    .map(fileMapping => fileMapping._1 -> file(jsTarget) / fileMapping._2)

  IO.copy(
    sources = bundleFiles,
    overwrite = true,
    preserveLastModified = true,
    preserveExecutable = true
  )
}

fullBlaargh := {
  def setTargetFileName(origFileName: String): String = {
    val fname = name.value.replaceAllLiterally(".", "-")
    if (origFileName.endsWith(".map")) s"$fname.js.map"
    else s"$fname.js"
  }

  val webpackFiles = (webpack in (Compile, fullOptJS)).value

  val bundleFiles = webpackFiles.filter { af =>
    af.metadata
      .get(BundlerFileTypeAttr)
      .contains(BundlerFileType.ApplicationBundle) || af.data.name.endsWith(
      ".js.map"
    )
  }.map(af => af.data -> setTargetFileName(af.data.name))
    .map(fileMapping => fileMapping._1 -> file(jsTarget) / fileMapping._2)

  IO.copy(
    sources = bundleFiles,
    overwrite = true,
    preserveLastModified = true,
    preserveExecutable = true
  )
}
