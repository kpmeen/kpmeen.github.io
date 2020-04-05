import $file.Common
import $file.Frontmatter
import $ivy.`com.vladsch.flexmark:flexmark-all:0.61.0`
import $ivy.`org.yaml:snakeyaml:1.26`
import Common._
import Frontmatter._
import ammonite.ops._
import com.vladsch.flexmark.ast._
import com.vladsch.flexmark.ext.tables.{TableBlock, TableHead}
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.html.renderer.{
  DelegatingNodeRendererFactory,
  NodeRenderer,
  NodeRendererContext,
  NodeRenderingHandler
}
import com.vladsch.flexmark.html.{HtmlRenderer, HtmlWriter}
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.{DataHolder, MutableDataHolder}
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._

val yaml = new Yaml
lazy val conf = yaml.load(read ! configFile).asInstanceOf[java.util.Map[String, Any]].asScala

class BlaarghSerializer {

  object BlaarghNodeRenderer extends NodeRenderer with DelegatingNodeRendererFactory {

    val imageRenderer = new NodeRenderingHandler(classOf[Image], {
      (node: Image, ctx: NodeRendererContext, writer: HtmlWriter) =>
        writer
          .raw("""<div style="text-align: center">""")
          .endsWithEOL()
        writer
          .indent()
          .raw(s"""<img src="posts/${node.getUrl}" alt="${node.getText}" style="max-width: 100%; max-height: 500px;"/>""")
          .endsWithEOL()
        writer
          .unIndent()
          .raw("</div>")
          .endsWithEOL()
    })

    val headerRenderer = new NodeRenderingHandler(classOf[Heading], {
      (node: Heading, _: NodeRendererContext, writer: HtmlWriter) =>
        val tag = s"h${node.getLevel}"
        val anchor = sanitizeAnchor(node.getText.normalizeEOL())
        node.setAnchorRefId(anchor)
        writer.withAttr()
        writer.tag(tag)
        writer.attr("id", anchor)
        writer.text(node.getText)
        writer.closeTag(tag).endsWithEOL()
    })

    val tableRenderer = new NodeRenderingHandler(classOf[TableBlock], {
      (node: TableBlock, ctx: NodeRendererContext, writer: HtmlWriter) =>
        writer.attr("class", "table table-bordered table-striped")
        writer
          .srcPosWithEOL(node.getChars)
          .withAttr()
          .tagLineIndent("table", () => ctx.renderChildren(node))
          .line()
    })

    val tableHeadRenderer = new NodeRenderingHandler(classOf[TableHead], {
      (node: TableHead, ctx: NodeRendererContext, writer: HtmlWriter) =>
        writer.attr("class", "thead-inverse")
        writer
          .withAttr()
          .withCondIndent()
          .tagLine("thead", () => ctx.renderChildren(node))
    })

    val renderingHandlers = new java.util.HashSet[NodeRenderingHandler[_]]
    renderingHandlers.add(imageRenderer)
    renderingHandlers.add(headerRenderer)
    renderingHandlers.add(tableRenderer)
    renderingHandlers.add(tableHeadRenderer)

    override def getNodeRenderingHandlers = renderingHandlers

    override def getDelegates = null // scalastyle:ignore

    override def apply(dataHolder: DataHolder) = this
  }

  object BlaarghHtmlExtensions extends HtmlRendererExtension {
    override def rendererOptions(mutableDataHolder: MutableDataHolder) = {}

    override def extend(builder: HtmlRenderer.Builder, s: String) = {
      builder.nodeRendererFactory(BlaarghNodeRenderer)
    }
  }

  val opts = options
    .set(Parser.EXTENSIONS, (mdExtensions :+ BlaarghHtmlExtensions).asJava)
  val renderer = HtmlRenderer.builder(opts).indentSize(2).build()

  def toHtml(doc: Document) = {
    renderer.render(doc)
  }

  def sanitize(s: String): String = {
    s.filter(_.isLetterOrDigit)
  }

  def sanitizeAnchor(s: String): String = {
    s.split(" |-", -1).map(_.filter(_.isLetterOrDigit)).mkString("-").toLowerCase
  }
}

object BlaarghParser {
  private val separator = "---"
  private val remove = "€€€REMOVE€€€"

  // Separate FrontMatter header from the markdown content
  private def parseFrontMatter(source: String): (FrontMatter, String) = {
    val split = source.stripPrefix(separator).replaceFirst(separator, remove).split(remove)
    val fm = FrontMatter.parse(split(0))
    val md = split(1)

    (fm, md)
  }

  private def resolveName(mfd: Path): (String, Path) = (mfd.last.stripSuffix(".md"), mfd)

  def parse(mdFiles: Seq[Path], otherFiles: Seq[Path], hasFrontMatter: Boolean = true) = {
    val split = for (filePath <- mdFiles) yield resolveName(filePath)

    for ((name, path) <- split.sortBy(_._1)) yield {
      val processor = Parser.builder(options).build()
      val fileContent = if (hasFrontMatter) parseFrontMatter(read ! path) else (FrontMatter.empty, read ! path)
      val doc = processor.parse(fileContent._2)
      val rawHtmlContent = new BlaarghSerializer().toHtml(doc)

      (name, fileContent._1, rawHtmlContent)
    }
  }
}

object SitemapBuilder {

  lazy val baseUrl = conf.getOrElse("url", "http://localhost")

  private def page(url: String) = {
    <url>
      <loc>
        {baseUrl + "/" + url}
      </loc>
      <priority>.5</priority>
      <changefreq>weekly</changefreq>
    </url>
  }

  def build(pages: Seq[(String, String)]) = {
    val root = <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"></urlset>
    val children = Seq.newBuilder[scala.xml.Node]
    children += page("")
    children += page("#about")
    pages.foreach(p => children += page("#posts/" + p._1 + "/" + p._2))
    root.copy(child = children.result())
  }
}

object BlaarghWriter {
  val (pfMdFiles, pfOtherFiles) = ls ! postsFolder partition (_.ext == "md")
  lazy val entries = BlaarghParser.parse(pfMdFiles, pfOtherFiles)

  val (pgsMdFiles, pgsOtherFiles) = ls ! pagesFolder partition (_.ext == "md")
  lazy val pages = BlaarghParser.parse(pgsMdFiles, pgsOtherFiles, hasFrontMatter = false).map(n => (n._1, n._3))

  def generate() = {
    println("Cleaning up posts folder...")
    rm ! postsTargetFolder
    mkdir ! postsTargetFolder
    println("Cleaning up pages folder...")
    rm ! pagesTargetFolder
    mkdir ! pagesTargetFolder

    println("Cleaning up sitemap.xml...")
    rm ! baseFolder / "sitemap.xml"

    for (of <- pgsOtherFiles) {
      cp(of, pagesTargetFolder / (of relativeTo pagesFolder))
    }

    println("Generating html files from _pages...")
    for ((name, rawHtmlContent) <- pages) {
      write(
        pagesTargetFolder / s"${name.replaceAll(" ", "_")}.html",
        rawHtmlContent
      )
    }

    for (otherFile <- pfOtherFiles) {
      cp(otherFile, postsTargetFolder / (otherFile relativeTo postsFolder))
    }

    // Build and write the json file containing all blog article metadata.
    println("Generating posts/posts.json...")
    val json: String = entries
      .map(e => FrontMatter.toJsonString(e._1, e._2))
      .mkString("[\n", ",\n", "\n]\n")
    write(
      postsTargetFolder / "posts.json",
      json
    )

    println("Preparing sitemap...")
    val sb = SitemapBuilder.build(
      entries.map {
        case (name, fm, _) => (fm.date.get.toString, name.replaceAll(" ", "_"))
      }
    )

    println("Generating sitemap.xml...")
    write(
      baseFolder / "sitemap.xml",
      sb.mkString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "\n", "\n")
    )

    println("Generating html files from _posts...")
    for ((name, _, rawHtmlContent) <- entries) {
      write(
        postsTargetFolder / s"${name.replaceAll(" ", "_")}.html",
        rawHtmlContent
      )
    }
  }
}
