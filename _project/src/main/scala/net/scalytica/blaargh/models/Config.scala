/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.models

import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import play.api.libs.json._

case class Author(
    name: String,
    email: String
)

case class Owner(
    name: String,
    avatar: String,
    bio: String,
    email: String,
    disqusShortname: String,
    twitter: String,
    googleAnalytics: String,
    github: String
)

object Owner {
  val empty = Owner(
    name = "",
    avatar = "",
    bio = "",
    email = "",
    disqusShortname = "",
    twitter = "",
    googleAnalytics = "",
    github = ""
  )
}

case class LetsEncryptConfig(
    wellKnownBase: String,
    wellKnownValue: String
)

object LetsEncryptConfig {
  val empty = LetsEncryptConfig("", "")
}

case class Config(
    siteTitle: String,
    authors: Seq[Author],
    owner: Owner,
    letsEncrypt: Option[LetsEncryptConfig]
) {

  def letsEncryptOrEmpty = letsEncrypt.getOrElse(LetsEncryptConfig.empty)
}

object Config {

  val empty = Config(
    siteTitle = "",
    authors = Seq.empty,
    owner = Owner.empty,
    letsEncrypt = None
  )

  implicit val authFormat = Json.format[Author]
  implicit val ownrFormat = Json.format[Owner]
  implicit val lencFormat = Json.format[LetsEncryptConfig]
  implicit val confFormat = Json.format[Config]

  def load(): Future[Config] =
    Ajax
      .get(
        url = "config/config.json"
      )
      .map { xhr =>
        xhr.status match {
          case ok: Int if ok == 200 =>
            val js = Json.parse(xhr.responseText)
            Json.fromJson[Config](js).getOrElse(empty)
          case _ => empty
        }
      }

}
