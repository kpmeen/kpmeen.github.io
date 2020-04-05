import $ivy.`com.vladsch.flexmark:flexmark-all:0.61.0`
import ammonite.ops._
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.parser.{Parser, ParserEmulationProfile}
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension

import scala.collection.JavaConverters._

val workingDir = pwd

println(s"Current working directory is: $workingDir")

val baseFolder = workingDir / up
val postsFolder = baseFolder / '_posts
val postsTargetFolder = baseFolder / 'posts
val pagesFolder = baseFolder / '_pages
val pagesTargetFolder = baseFolder / 'pages
val configFile = baseFolder / "config" / "config.json"

val abbrExts = AbbreviationExtension.create()
val defnExts = DefinitionExtension.create()
val footExts = FootnoteExtension.create()
val typoExts = TypographicExtension.create()
val tableExts = TablesExtension.create()

val mdExtensions: List[Extension] = List(
  abbrExts,
  defnExts,
  footExts,
  tableExts,
  typoExts
)

val options: MutableDataSet = new MutableDataSet()
  .setFrom(ParserEmulationProfile.GITHUB)
  .set(Parser.EXTENSIONS, mdExtensions.asJava)

println("initializing...")
