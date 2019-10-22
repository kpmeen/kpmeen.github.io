/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.models

import net.scalytica.blaargh.pages.Views.ArticleRef
import net.scalytica.blaargh.utils.StaticConfig
import org.scalajs.dom.ext.Ajax
import play.api.libs.json._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

case class Article(
    date: String,
    author: String,
    title: String,
    ingress: String,
    labels: Seq[String],
    filename: String,
    image: String
) {

  def asJsDate: js.Date = new js.Date(date)

  def articleRef: ArticleRef = ArticleRef(shortDate, filename)

  def shortDate: String = date.take(10) // scalastyle:ignore

}

object Article {

  lazy val Articles = fetchAll

  implicit val formats: OFormat[Article] = Json.format[Article]

  def get(a: ArticleRef) =
    Ajax.get(url = a.htmlFilePath).map { xhr =>
      xhr.status match {
        case ok: Int if ok == 200 => Some(xhr.responseText)
        case _                    => None
      }
    }

  def fetchAll: Future[Seq[Article]] =
    Ajax
      .get(
        url = s"${StaticConfig.baseUrl.value}posts/posts.json",
        headers = Map(
          "Accept"       -> "application/json",
          "Content-Type" -> "application/json"
        )
      )
      .map { xhr =>
        xhr.status match {
          case ok: Int if ok == 200 =>
            val js = Json.parse(xhr.responseText)
            Json
              .fromJson[Seq[Article]](js)
              .map(_.sortBy(a => a.date).reverse)
              .getOrElse(Seq.empty)

          case _ =>
            Seq.empty[Article]
        }
      }

}
