/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.{Article, ArticleRef}
import org.scalajs.dom.document

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.global

object ArticleView {

  case class Props(article: Future[Option[Article]], ref: ArticleRef)

  case class State(article: Option[Article] = None, content: Option[String] = None)

  class Backend($: BackendScope[Props, State]) {

    def init: Callback =
      $.props.map(p =>
        for {
          metadata <- p.article
          content <- Article.get(p.ref)
        } yield {
          $.setState(State(metadata, content)).runNow()
        }
      )

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
          <.div(^.className := "post",
            state.article.map(a => <.h1(a.title)).getOrElse(EmptyTag),
            <.span(
              ^.dangerouslySetInnerHtml(c)
            )
          )
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

  def apply(article: Future[Option[Article]], ref: ArticleRef) = component(Props(article, ref))

}
