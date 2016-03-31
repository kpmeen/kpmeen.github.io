/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.models

import org.scalajs.dom.ext.Ajax
import upickle.default._

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

  def shortDate = date.take(10)

}

case class ArticleRef(date: String, filename: String) {
  def htmlFilePath = s"posts/$filename.html"

  def navigationPath = s"posts/$date/$filename"
}

object Article {

  def get(a: ArticleRef) =
    Ajax.get(
      url = a.htmlFilePath
    ).map { xhr =>
      xhr.status match {
        case ok: Int if ok == 200 => Some(xhr.responseText)
        case err => None
      }
    }

  def findByFilename(filename: String, fsa: Future[Seq[Article]]): Future[Option[Article]] =
    fsa.map(_.find(_.filename == filename))

  def filterByAuthor(author: String, fsa: Future[Seq[Article]]): Future[Seq[Article]] =
    fsa.map(_.filter(_.author == author))

  def filterByLabel(label: String, fsa: Future[Seq[Article]]): Future[Seq[Article]] =
    fsa.map(_.filter(_.labels.contains(label)))

  def fetchAll: Future[Seq[Article]] =
    Ajax.get(
      url = "posts/posts.json",
      headers = Map(
        "Accept" -> "application/json",
        "Content-Type" -> "application/json"
      )
    ).map { xhr =>
      xhr.status match {
        case ok: Int if ok == 200 =>
          read[Seq[Article]](xhr.responseText).sortBy(a => a.date).reverse

        case err =>
          Seq.empty[Article]
      }
    }

}
