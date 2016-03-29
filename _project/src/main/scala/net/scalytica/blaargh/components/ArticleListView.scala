/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.App.View
import net.scalytica.blaargh.models.Article

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ArticleListView {

  case class Props(articles: Future[Seq[Article]], ctl: RouterCtl[View])
  case class State(articles: Seq[Article], ctl: RouterCtl[View])

  class Backend($: BackendScope[Props, State]) {
    def init: Callback =
      $.props.map(p =>
        Callback.future[Unit] {
          p.articles.map(a => $.modState(_.copy(articles = a)))
        }.runNow()
      )

    def render(props: Props, state: State) =
      <.div(^.className := "container-fluid",
        <.div(^.className := "card-columns",
          state.articles.map(a => ArticlePreview(a, props.ctl))
        )
      )
  }

  val component = ReactComponentB[Props]("Home")
    .initialState_P(p => State(Seq.empty, p.ctl))
    .renderBackend[Backend]
    .componentWillMount($ => $.backend.init)
    .build

  def apply(articles: Future[Seq[Article]], ctl: RouterCtl[View]) = component(Props(articles, ctl))
}
