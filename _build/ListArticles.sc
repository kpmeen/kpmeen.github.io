import $ivy.`org.scala-lang.modules:scala-swing_2.13:3.0.0`
import $file.Common, Common._
import $file.Frontmatter, Frontmatter._

import ammonite.ops._

import javax.swing.UIManager

import scala.concurrent.duration._
import scala.language.implicitConversions
import scala.swing.BorderPanel.Position
import scala.swing.event.{ButtonClicked, SelectionChanged}
import scala.swing.{
  BorderPanel,
  Button,
  Dimension,
  FlowPanel,
  Frame,
  GridPanel,
  Label,
  ListView,
  Orientation,
  SplitPane
}
import scala.util.Try

println("JVM version:" + sys.props.get("java.version").getOrElse(""))

object ListArticles {

  def posts: Seq[(String, FrontMatter)] = {
    val posts = ls ! postsFolder |? (_.ext == "md")
    posts.map { p =>
      val (fileFm, _) = FrontMatter.parseFromPath(p)
      s"${p.baseName}.md" -> fileFm
    }
  }

  val initialPosts = posts
  val fileNames = initialPosts.map(_._1)
  val postMetadata = initialPosts.map(_._2)

  class DetailPanel(rows: Int, cols: Int) extends GridPanel(rows, cols) {

    def addLabels(fm: FrontMatter): Unit = {
      contents.clear()
      contents += new Label(fm.title)
      contents += new Label(fm.labelsString)
      contents += new Label(fm.date.map(_.toString).getOrElse(""))
      repaint()
    }

  }

  class FileNamesList(fnames: Seq[String], details: DetailPanel) extends ListView[String](fnames) {
    reactions += {
      case SelectionChanged(_) =>
        val toDisplay = postMetadata(peer.getSelectedIndex)
        details.addLabels(toDisplay)
    }
  }

  class SplitPanePanel extends BorderPanel {
    override val preferredSize = new Dimension(1200, 500)

    val cancelButton = new Button("Close")
    val fileNamesList = new ListView[String](fileNames)
    val filesPanel = new FlowPanel(fileNamesList)
    val detailsPanel = new DetailPanel(initialPosts.length, 1)
    val splitPane = new SplitPane(Orientation.Horizontal, filesPanel, detailsPanel)

    add(splitPane, Position.Center)
    add(cancelButton, Position.South)
  }

  class ListFrame extends Frame {
    title = "List Articles"

    val panel = new SplitPanePanel

    contents = panel

    pack()
    visible = true

    listenTo(panel.cancelButton)

    reactions += {
      case ButtonClicked(panel.`cancelButton`) =>
        visible = false
        dispose()
    }

    override def closeOperation(): Unit = {
      close
    }
  }

}

println("Attempting to set system L&F...")

Try {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
}.recover {
  case ex: Exception => ex.printStackTrace()
}

val frame = new ListArticles.ListFrame

while (frame.visible) {
  Thread.sleep(5.seconds.toMillis)
}
