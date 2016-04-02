/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react.extra.router.Path

object Views {

  trait ViewType {
    val basePath: String
  }

  sealed trait View

  case object Home extends View with ViewType {
    override val basePath = ""
  }

  case object About extends View with ViewType {
    override val basePath = "#about"
  }

  case object NotFound extends View with ViewType {
    override val basePath = "#notfound"
  }

  case class Posts(ref: ArticleRef) extends View {
    def asPath: Path = Path(s"#${ref.navigationPath}")
  }

  object Posts extends ViewType {
    override val basePath = "#posts"
  }

  case class Filter(fc: FilterCriteria) extends View {
    def asPath: Path = Path(s"${Filter.basePath}/${fc.field}/${fc.value}")
  }

  object Filter extends ViewType {
    override val basePath = "#filter"
  }

  case class ArticleRef(date: String, filename: String) {
    def htmlFilePath = s"posts/$filename.html"

    def navigationPath = s"posts/$date/$filename"
  }

  case class FilterCriteria(field: String, value: String)

}
