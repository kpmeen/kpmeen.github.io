/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.components.{ArticleCard, Label}
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.{FilterCriteria, View}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object SearchResultsPage {

  case class Props(fc: FilterCriteria, ctl: RouterCtl[View])

  case class State(allArticles: Seq[Article] = Seq.empty)

  class Backend($: BackendScope[Props, State]) {

    def init: Callback =
      $.props.map(p =>
        Article.Articles.map { a =>
          println(s"Changing state with ${p.fc}, ${a.map(_.title).mkString(", ")}")
          // Set the state once and for all
          $.setState(State(a)).runNow()
        }
      )

    def render(props: Props, state: State) =
      <.div(^.className := "container-fluid",
        <.p(
          <.b(^.marginRight := "1.1rem", "Showing results for:"),
          Label(props.fc.value, props.ctl)
        ),
        <.div(^.className := "card-columns",
          state.allArticles.filter(_.labels.contains(props.fc.value)).map(a => ArticleCard(a, props.ctl))
        )
      )
  }

  val component = ReactComponentB[Props]("SearchResultsPage")
    .initialState_P(p => State())
    .renderBackend[Backend]
    .componentDidMount(_.backend.init)
    .build

  def apply(fc: FilterCriteria, ctl: RouterCtl[View]) =
    component(Props(fc, ctl))
}
