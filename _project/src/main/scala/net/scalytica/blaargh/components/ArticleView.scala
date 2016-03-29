/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.{Article, ArticleRef}
import org.scalajs.dom.document

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.global

object ArticleView {

  case class Props(ref: ArticleRef)

  case class State(content: Option[String] = None)

  class Backend($: BackendScope[Props, State]) {

    def init: Callback = $.props.map(p => Article.get(p.ref).map(res => $.modState(_.copy(res)).runNow()))

    def highlight = Callback {
      val elems = document.getElementsByTagName("code")
      for (i <- 0 to elems.length) {
        if (elems(i).parentNode.nodeName.equalsIgnoreCase("pre")) {
          global.hljs.highlightBlock(elems(i))
        }
      }
    }

    def render(props: Props, state: State) =
      <.div(^.className := "container",
        state.content.map(c =>
          <.div(^.className := "post", ^.dangerouslySetInnerHtml(c))
        ).getOrElse(
          <.div(^.className := "post")
        )
      )
  }

  val component = ReactComponentB[Props]("ArticleView")
    .initialState_P(p => State())
    .renderBackend[Backend]
    .componentWillMount(_.backend.init)
    .componentDidUpdate(_.$.backend.highlight)
    .build

  def apply(p: Props) = component(p)

  def apply(ref: ArticleRef) = component(Props(ref))

}
