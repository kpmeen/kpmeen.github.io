import ammonite.ops._

val workingDir = pwd

println(s"Current working directory is: $workingDir")

val baseFolder = workingDir / up
val postsFolder = baseFolder / '_posts
val postsTargetFolder = baseFolder / 'posts
val pagesFolder = baseFolder / '_pages
val pagesTargetFolder = baseFolder / 'pages
val configFile = baseFolder / "config" / "config.json"

println("initializing")
