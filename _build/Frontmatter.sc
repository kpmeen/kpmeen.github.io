val workingDir = ammonite.ops.pwd

import $file.Common, Common._
import $ivy.`org.yaml:snakeyaml:1.17`
import org.yaml.snakeyaml.Yaml
import scala.collection.JavaConverters._
import scala.collection.Map

case class FrontMatter(
  title: String,
  author: String,
  date: Option[java.util.Date],
  ingress: Option[String],
  labels: Option[Seq[String]],
  image: Option[String],
  misc: Map[String, Any]
) {

  def toYaml: String = {
    val yaml = new Yaml
    val m = {
      val builder = Map.newBuilder[String, Any]
      builder += "title" -> title
      builder += "author" -> author
      date.foreach(d => builder += "date" -> dateFormat.format(d))
      builder += "ingress" -> ingress.getOrElse("")
      builder += "labels" -> labels.getOrElse(Seq.empty).asJava
      builder.result()
    }.asJava
    s"---\n${yaml.dump(m)}---\n\n"
  }

}

sealed trait FrontMatterKey {
  self =>
  val strValue = self.getClass.getSimpleName.toLowerCase.stripPrefix("fm").stripSuffix("$")
}
case object FMTitle extends FrontMatterKey
case object FMAuthor extends FrontMatterKey
case object FMDate extends FrontMatterKey
case object FMIngress extends FrontMatterKey
case object FMLabels extends FrontMatterKey
case object FMImage extends FrontMatterKey

object FrontMatterKey {
  implicit def keyToString(fmk: FrontMatterKey): String = fmk.strValue

  implicit def strToKey(str: String): Option[FrontMatterKey] = {
    str match {
      case FMTitle.strValue => Some(FMTitle)
      case FMAuthor.strValue => Some(FMAuthor)
      case FMImage.strValue => Some(FMImage)
      case FMDate.strValue => Some(FMDate)
      case FMIngress.strValue => Some(FMIngress)
      case FMLabels.strValue => Some(FMLabels)
      case _ => None
    }
  }
}

object FrontMatter {

  val empty = FrontMatter(
    title = "",
    author = "",
    date = None,
    ingress = None,
    labels = None,
    image = None,
    misc = Map.empty
  )

  def parse(header: String): FrontMatter = {
    val yaml = new Yaml
    val res = yaml.load(header)
    fromObject(res)
  }

  def fromObject(jobj: Any): FrontMatter = {
    import FrontMatterKey._

    val smap = jobj.asInstanceOf[java.util.Map[String, Any]].asScala
    val nonDefaultKeys = smap.filterNot(kv => strToKey(kv._1).nonEmpty)

    FrontMatter(
      title = smap.get(FMTitle).map(_.asInstanceOf[String]).getOrElse(""),
      author = smap.get(FMAuthor).map(_.asInstanceOf[String]).getOrElse(""),
      date = smap.get(FMDate).map(_.asInstanceOf[java.util.Date]),
      ingress = smap.get(FMIngress).map(_.asInstanceOf[String]),
      labels = smap.get(FMLabels).map(_.asInstanceOf[java.util.ArrayList[String]].asScala),
      image = smap.get(FMImage).map(_.asInstanceOf[String]),
      misc = nonDefaultKeys
    )
  }

  def toJsonString(fileName: String, fm: FrontMatter) = {
    // TODO: Add misc keys...
    s"""  {
        |    "date": "${fm.date.map(d => dateFormat.format(d)).getOrElse("")}",
        |    "author": "${fm.author}",
        |    "title": "${fm.title}",
        |    "ingress": "${fm.ingress.getOrElse("")}",
        |    "labels": [${fm.labels.map(_.map(l => "\"" + l + "\"").mkString(",")).getOrElse("")}],
        |    "filename": "${fileName.replaceAll(" ", "_")}",
        |    "image": "${fm.image.map(i => "posts/" + i).getOrElse("")}"
        |  }""".stripMargin
  }
}
