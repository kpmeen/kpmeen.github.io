/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.App.View
import net.scalytica.blaargh.models.Article

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object ArticleListView {

  case class Props(articles: Seq[Article], ctl: RouterCtl[View])

  class Backend($: BackendScope[Props, Props]) {
    def init: Callback = Callback.future[Unit] {
      Article.fetchAll.map(res => $.modState(_.copy(res)))
    }

    def render(props: Props, state: Props) =
      <.div(^.className := "container-fluid",
        <.div(^.className := "card-columns",
          state.articles.map(a => ArticlePreview(a, state.ctl))
        )
      )
  }

  val component = ReactComponentB[Props]("Home")
    .initialState_P(p => p)
    .renderBackend[Backend]
    .componentWillMount($ => $.backend.init)
    .build


  def apply(ctl: RouterCtl[View]) = component(Props(Seq.empty, ctl))

  def apply(articles: Seq[Article], ctl: RouterCtl[View]) = component(Props(articles, ctl))
}
