
import ammonite.ops._

val baseFolder = cwd / up

val postsFolder = baseFolder / '_posts
val postsTargetFolder = baseFolder / 'posts

val pagesFolder = baseFolder / '_pages
val pagesTargetFolder = baseFolder / 'pages

val configFile = baseFolder / "config" / "config.json"

val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd")
