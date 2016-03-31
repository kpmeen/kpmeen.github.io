/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react.extra.router.Path
import net.scalytica.blaargh.models.ArticleRef

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

  case class LabelSearch(label: String) extends View {
    def asPath: Path = Path(s"${LabelSearch.basePath}/$label")
  }

  object LabelSearch extends ViewType {
    override val basePath = "#search/label"
  }

}
