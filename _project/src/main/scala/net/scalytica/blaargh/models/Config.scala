/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.models

import net.scalytica.blaargh.utils.StaticConfig
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
  val empty: Owner = Owner(
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

case class StreamingService(
    displayName: String,
    url: String,
    logoImage: String
)

case class TheaterConfig(
    redirectUrl: String,
    streamingServices: Seq[StreamingService]
)

object TheaterConfig {
  val empty: TheaterConfig = TheaterConfig("", Seq.empty)
}

case class Config(
    siteTitle: String,
    authors: Seq[Author],
    owner: Owner,
    theaterConfig: TheaterConfig
)

object Config {

  val empty: Config = Config(
    siteTitle = "",
    authors = Seq.empty,
    owner = Owner.empty,
    theaterConfig = TheaterConfig.empty
  )

  implicit val authFormat: OFormat[Author] = Json.format[Author]
  implicit val ownrFormat: OFormat[Owner]  = Json.format[Owner]
  implicit val strsFormat: OFormat[StreamingService] =
    Json.format[StreamingService]
  implicit val theaFormat: OFormat[TheaterConfig] = Json.format[TheaterConfig]

  implicit val confFormat: OFormat[Config] = Json.format[Config]

  def load(): Future[Config] =
    Ajax.get(url = s"${StaticConfig.baseUrl.value}config/config.json").map {
      xhr =>
        xhr.status match {
          case ok: Int if ok == 200 =>
            val js = Json.parse(xhr.responseText)
            Json.fromJson[Config](js).getOrElse(empty)
          case _ => empty
        }
    }

}
