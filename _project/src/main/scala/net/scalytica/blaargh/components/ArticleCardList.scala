/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.View
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import scalacss.ScalaCssReact._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ArticleCardList {

  case class Props(ctl: RouterCtl[View])

  case class State(articles: Seq[Article], ctl: RouterCtl[View])

  class Backend($ : BackendScope[Props, State]) {

    def init: Callback = $.props.map { _ =>
      Callback
        .future[Unit] {
          Article.Articles.map(a => $.modState(_.copy(articles = a)))
        }
        .runNow()
    }

    def render(props: Props, state: State) =
      <.div(
        BlaarghBootstrapCSS.cardCols,
        state.articles.map(a => ArticleCard(a, props.ctl)).toVdomArray
      )
  }

  val component = ScalaComponent
    .builder[Props]("ArticleCardList")
    .initialStateFromProps(p => State(Seq.empty, p.ctl))
    .renderBackend[Backend]
    .componentWillMount(_.backend.init)
    .build

  def apply(ctl: RouterCtl[View]) = component(Props(ctl))
}
