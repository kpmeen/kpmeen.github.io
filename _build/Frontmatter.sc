import $file.Common, Common._
import $ivy.`org.yaml:snakeyaml:1.26`
import org.yaml.snakeyaml.Yaml
import scala.collection.JavaConverters._
import scala.collection.Map

case class FrontMatter(
  title: String,
  author: String,
  date: Option[java.time.LocalDate],
  ingress: Option[String],
  labels: Option[Seq[String]],
  image: Option[String],
  misc: Map[String, Any]
) {

  def toYaml: String = {
    val m = {
      val builder = Map.newBuilder[String, Any]
      builder += "title" -> title
      builder += "author" -> author
      date.foreach(d => builder += "date" -> d.toString)
      builder += "ingress" -> ingress.map(_.replaceAll("\n", " ")).getOrElse("")
      builder += "labels" -> labels.getOrElse(Seq.empty).asJava
      builder.result()
    }

    val yamlStr = m.map(kv => s"${kv._1}: ${kv._2}").mkString("\n")
    s"---\n$yamlStr\n---\n\n"
  }

}

sealed trait FrontMatterKey { self =>
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
    val res = yaml.load[java.util.Map[String, Any]](header)
    fromObject(res)
  }

  def fromObject(jobj: Any): FrontMatter = {
    import FrontMatterKey._

    val smap = jobj.asInstanceOf[java.util.Map[String, Any]].asScala
    val nonDefaultKeys = smap.filterNot(kv => strToKey(kv._1).nonEmpty)

    FrontMatter(
      title = smap.get(FMTitle).map(_.asInstanceOf[String]).getOrElse(""),
      author = smap.get(FMAuthor).map(_.asInstanceOf[String]).getOrElse(""),
      date = smap.get(FMDate).map { fmd =>
        val d = fmd.asInstanceOf[java.util.Date]
        d.toInstant.atZone(java.time.ZoneId.systemDefault()).toLocalDate
      },
      ingress = smap.get(FMIngress).map(_.asInstanceOf[String]),
      labels = smap.get(FMLabels).map(_.asInstanceOf[java.util.ArrayList[String]].asScala.toSeq),
      image = smap.get(FMImage).map(_.asInstanceOf[String]),
      misc = nonDefaultKeys
    )
  }

  def toJsonString(fileName: String, fm: FrontMatter) = {
    try {
      s"""{
         |  "date": "${fm.date.map(_.toString).getOrElse("")}",
         |  "author": "${fm.author}",
         |  "title": "${fm.title}",
         |  "ingress": "${fm.ingress.getOrElse("")}",
         |  "labels": [${fm.labels.map(_.map(l => "\"" + l + "\"").mkString(",")).getOrElse("")}],
         |  "filename": "${fileName.replaceAll(" ", "_")}",
         |  "image": "${fm.image.map(i => "posts/" + i).getOrElse("")}"
         |}""".stripMargin
    } catch {
      case e: Throwable =>
        println("THIS FAILED!!!!")
        e.printStackTrace()
        throw e

    }
  }
}
