/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.{ArticleRef, View}
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

    val code = style("blaargh-code")(
      marginRight(10.px),
      marginLeft(10.px),
      BlaarghBootstrapCSS.Mixins.cardShadow,
      unsafeChild("code")(
        backgroundColor.whitesmoke
      )
    )
  }

  case class Props(article: Future[Option[Article]], ref: ArticleRef, ctl: RouterCtl[ArticleRef])

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

    def highlight = $.state.map { s =>
      s.content.foreach { _ =>
        val elems = document.getElementsByTagName("code")
        for (i <- 0 to elems.length) {
          Option(elems(i).parentNode).foreach { parent =>
            if (parent.nodeName.equalsIgnoreCase("pre")) {
              parent.domAsHtml.classList.add("blaargh-code")
              parent.domAsHtml.classList.add("card")
              global.hljs.highlightBlock(elems(i))
            }
          }
        }
      }
    }

    def render(props: Props, state: State) =
      <.div(BlaarghBootstrapCSS.container,
        state.content.map(c =>
          <.div(Styles.post,
            state.article.map(a => <.h1(a.title)).getOrElse(EmptyTag),
            state.article.map(a =>
              <.p(
                <.span(BlaarghBootstrapCSS.textMuted,
                  s"Written by ${a.author} on ${a.asJsDate.toDateString()}"
                ),
                <.span(^.marginLeft := "2rem",
                  a.labels.map { l =>
                    Label(l, props.ctl.contramap[View](v => props.ref))
                  }
                )
              )
            ).getOrElse(EmptyTag),
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

  def apply(article: Future[Option[Article]], ref: ArticleRef, ctl: RouterCtl[ArticleRef]) =
    component(Props(article, ref, ctl))

}
