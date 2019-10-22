/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.components.ArticleCard
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.{FilterCriteria, View}
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.ScalaCssReact._

object SearchResultsPage {

  case class Props(fc: FilterCriteria, ctl: RouterCtl[View])

  case class State(allArticles: Seq[Article] = Seq.empty)

  class Backend($ : BackendScope[Props, State]) {

    def init: Callback =
      $.props.map[Unit] { _ =>
        Article.Articles.map(a => $.setState(State(a)).runNow())
      }

    def render(props: Props, state: State) =
      <.div(
        BlaarghBootstrapCSS.containerFluid,
        <.p(
          <.b(^.marginRight := "1.1rem", "Showing results for:"),
          <.span(BlaarghBootstrapCSS.labelDefault, props.fc.value)
        ),
        <.div(
          BlaarghBootstrapCSS.cardCols,
          props.fc.field match {
            case "author" =>
              state.allArticles
                .filter(_.author == props.fc.value)
                .map(a => ArticleCard(a, props.ctl))
                .toVdomArray

            case "label" =>
              state.allArticles
                .filter(_.labels.contains(props.fc.value))
                .map(a => ArticleCard(a, props.ctl))
                .toVdomArray

            case _ =>
              EmptyVdom
          }
        )
      )
  }

  val component = ScalaComponent
    .builder[Props]("SearchResultsPage")
    .initialStateFromProps(_ => State())
    .renderBackend[Backend]
    .componentDidMount(_.backend.init)
    .build

  def apply(fc: FilterCriteria, ctl: RouterCtl[View]) =
    component(Props(fc, ctl))
}
