/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.{Article, ArticleRef}
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import org.scalajs.dom.document

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.Dynamic.global
import scalacss.Defaults._
import scalacss.ScalaCssReact._

object ArticleView {

  object Styles extends StyleSheet.Inline {

    import dsl._

    val post = style("blaargh-post")(
      paddingBottom(5.rem)
    )
  }

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
      <.div(BlaarghBootstrapCSS.container,
        state.content.map(c =>
          <.div(Styles.post,
            state.article.map(a => <.h1(a.title)).getOrElse(EmptyTag),
            <.span(
              ^.dangerouslySetInnerHtml(c)
            )
          )
        ).getOrElse(
          <.div(Styles.post)
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
