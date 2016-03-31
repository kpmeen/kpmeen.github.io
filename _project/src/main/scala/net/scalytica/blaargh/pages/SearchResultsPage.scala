/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.components.{ArticleCard, Label}
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.View

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object SearchResultsPage {

  case class Props(filter: String, articles: Future[Seq[Article]], ctl: RouterCtl[View])

  case class State(articles: Seq[Article], ctl: RouterCtl[View])

  class Backend($: BackendScope[Props, State]) {
    def init: Callback =
      $.props.map(p =>
        Callback.future[Unit] {
          Article.filterByLabel(p.filter, p.articles).map(a => $.modState(_.copy(articles = a)))
        }.runNow()
      )

    def render(props: Props, state: State) =
      <.div(^.className := "container-fluid",
        <.p(
          <.b(^.marginRight := "1.1rem", "Showing results for:"),
          Label(props.filter, props.ctl)
        ),
        <.div(^.className := "card-columns",
          state.articles.map(a => ArticleCard(a, state.ctl))
        )
      )
  }

  val component = ReactComponentB[Props]("SearchResultsPage")
    .initialState_P(p => State(Seq.empty, p.ctl))
    .renderBackend[Backend]
    .componentWillMount($ => $.backend.init)
    .build

  def apply(filter: String, articles: Future[Seq[Article]], ctl: RouterCtl[View]) =
    component(Props(filter, articles, ctl))
}
