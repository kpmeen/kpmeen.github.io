load.ivy("org.pegdown" % "pegdown" % "1.6.0")
load.module(ammonite.ops.cwd/"frontmatter.scala")
load.module(ammonite.ops.cwd/"paths.scala")
@
import ammonite.ops._
import org.pegdown.ast.{HeaderNode, SimpleNode, TableNode, TextNode, VerbatimNode}
import org.pegdown.{Extensions, LinkRenderer, PegDownProcessor, ToHtmlSerializer}
import scala.collection.JavaConverters._
import scala.collection.Map

class BlaarghSerializer extends ToHtmlSerializer(new LinkRenderer) {
  override def printImageTag(rendering: LinkRenderer.Rendering) {
    printer.print("<div style=\"text-align: center\"><img")
    printAttribute("src", s"posts/${rendering.href}")

    if (!rendering.text.equals("")) {
      printAttribute("alt", rendering.text)
    }
    import collection.JavaConversions._
    for (attr <- rendering.attributes) {
      printAttribute(attr.name, attr.value)
    }
    printer.print(" style=\"max-width: 100%; max-height: 500px\"")
    printer.print(" /></div>")
  }

  override def visit(node: HeaderNode) = {
    val tag = "h" + node.getLevel

    val id = node.getChildren.asScala.collect { case t: TextNode => t.getText }.mkString

    val setId = s"id=${'"' + sanitizeAnchor(id) + '"'}"
    printer.print(s"<$tag $setId>")
    visitChildren(node)
    printer.print(s"</$tag>")
  }

  override def visit(node: VerbatimNode) = {
    printer.println().print("<pre><code class=\"" + node.getType + "\">")

    var text = node.getText
    // print HTML breaks for all initial newlines
    while (text.charAt(0) == '\n') {
      printer.print("<br/>")
      text = text.substring(1)
    }
    printer.printEncoded(text)
    printer.print("</code></pre>")
  }

  override def visit(node: TableNode) = {
    currentTableNode = node
    printer.print("<table class=\"table table-bordered\">")
    visitChildren(node)
    printer.print("</table>")
    currentTableNode = null
  }

  def sanitize(s: String): String = {
    s.filter(_.isLetterOrDigit)
  }

  def sanitizeAnchor(s: String): String = {
    s.split(" |-", -1).map(_.filter(_.isLetterOrDigit)).mkString(" - ").toLowerCase
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
      val processor = new PegDownProcessor(Extensions.FENCED_CODE_BLOCKS | Extensions.TABLES)
      val fileContent = if (hasFrontMatter) parseFrontMatter(read ! path) else (FrontMatter.empty, read ! path)
      val ast = processor.parseMarkdown(fileContent._2.toArray)
      val rawHtmlContent = new BlaarghSerializer().toHtml(ast)

      val snippetNodes = ast.getChildren.asScala.takeWhile {
        case n: SimpleNode if n.getType == SimpleNode.Type.HRule => false
        case _ => true
      }

      ast.getChildren.clear()
      snippetNodes.foreach(ast.getChildren.add)

      val rawHtmlSnippet = new BlaarghSerializer().toHtml(ast)
      (name, fileContent._1, rawHtmlContent, rawHtmlSnippet)
    }
  }
}

object BlaarghWriter {
  val (pfMdFiles, pfOtherFiles) = ls ! postsFolder partition (_.ext == "md")
  val entries = BlaarghParser.parse(pfMdFiles, pfOtherFiles)

  val (pgsMdFiles, pgsOtherFiles) = ls ! pagesFolder partition (_.ext == "md")
  val pages = BlaarghParser.parse(pgsMdFiles, pgsOtherFiles, hasFrontMatter = false).map(n => (n._1, n._3, n._4))

  def generate() = {
    rm ! postsTargetFolder
    mkdir ! postsTargetFolder
    rm ! pagesTargetFolder
    mkdir ! pagesTargetFolder

    for (of <- pgsOtherFiles) {
      cp(of, pagesTargetFolder / (of relativeTo pagesFolder))
    }

    for ((name, rawHtmlContent, _) <- pages) {
      write(
        pagesTargetFolder / s"${name.replaceAll(" ", "_")}.html",
        rawHtmlContent
      )
    }

    for (otherFile <- pfOtherFiles) {
      cp(otherFile, postsTargetFolder / (otherFile relativeTo postsFolder))
    }

    // Build and write the json file containing all blog article metadata.
    val json: String = entries.map(e => FrontMatter.toJsonString(e._1, e._2)).mkString("[\n", ",\n", "\n]\n")
    write(
      postsTargetFolder / "posts.json",
      json
    )
    for ((name, fm, rawHtmlContent, _) <- entries) {
      write(
        postsTargetFolder / s"${name.replaceAll(" ", "_")}.html",
        rawHtmlContent
      )
    }
  }
}
