/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react.extra.router.Path
import net.scalytica.blaargh.models.LetsEncryptConfig
import net.scalytica.blaargh.utils.StaticConfig

object Views {

  trait ViewType {
    val basePath: String
  }

  sealed trait View

  case object Home extends View with ViewType {
    override val basePath = ""
  }

  case object LetsEncrypt extends View

  object LetsEncryptPath extends ViewType {
    override val basePath = s".well-known/acme-challenge"
    val fullPath = (wk: String) => s"$basePath/$wk"
  }

  case object About extends View with ViewType {
    override val basePath = s"${StaticConfig.PathPrefix}about"
  }

  case object NotFound extends View with ViewType {
    override val basePath = s"${StaticConfig.PathPrefix}notfound"
  }

  case class Posts(ref: ArticleRef) extends View {
    def asPath: Path = Path(ref.navigationPath)
  }

  object Posts extends ViewType {
    override val basePath = s"${StaticConfig.PathPrefix}posts"
  }

  case class Filter(fc: FilterCriteria) extends View {
    def asPath: Path = Path(s"${Filter.basePath}/${fc.field}/${fc.value}")
  }

  object Filter extends ViewType {
    override val basePath = s"${StaticConfig.PathPrefix}filter"
  }

  case class ArticleRef(date: String, filename: String) {
    def htmlFilePath = s"${StaticConfig.baseUrl.value}posts/$filename.html"

    def navigationPath = s"${StaticConfig.PathPrefix}posts/$date/$filename"
  }

  case class FilterCriteria(field: String, value: String)

}
