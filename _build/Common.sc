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

import scala.jdk.CollectionConverters._

val workingDir: os.Path = pwd

println(s"Current working directory is: $workingDir")

val baseFolder: os.Path = workingDir / up
val postsFolder: os.Path = baseFolder / Symbol("_posts")
val postsTargetFolder: os.Path = baseFolder / Symbol("posts")
val pagesFolder: os.Path = baseFolder / Symbol("_pages")
val pagesTargetFolder: os.Path = baseFolder / Symbol("pages")
val configFile: os.Path = baseFolder / Symbol("config") / Symbol("config.json")

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
