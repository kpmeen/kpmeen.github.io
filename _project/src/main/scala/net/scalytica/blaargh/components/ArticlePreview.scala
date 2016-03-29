/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.App.{Posts, View}
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.utils.StringUtils._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object ArticlePreview {

  case class Props(article: Article, ctl: RouterCtl[View])

  object Styles extends StyleSheet.Inline {
    import dsl._

    val cardTitle = style("blaargh-card-title")(
      addClassName("card-title"),
      cursor.pointer
    )

    val cardImage = style("blaargh-card-image")(
      addClassName("card-img-top"),
      width(100.%%),
      cursor.pointer
    )
  }

  class Backend($: BackendScope[Props, Unit]) {

    def maybeTitle(props: Props)(content: TagMod => TagMod): TagMod =
      content(asOption(props.article.title).map(title =>
        <.h4(Styles.cardTitle, ^.onClick --> openArticleCB(props), title)
      ).getOrElse(EmptyTag))

    def maybeImage(props: Props) =
      asOption(props.article.image).map(image =>
        <.img(Styles.cardImage, ^.src := image, ^.onClick --> openArticleCB(props))
      )

    def openArticleCB(props: Props): Callback = props.ctl.set(Posts(props.article.articleRef))


    def render(props: Props) = {
      <.div(^.className := "card",
        maybeImage(props).getOrElse(EmptyTag),
        maybeTitle(props)(title =>
          <.div(^.className := "card-block",
            title,
            <.p(^.className := "card-text",
              props.article.ingress
            ),
            <.p(^.className := "card-text",
              <.small(^.className := "text-muted",
                <.span("by "),
                <.a(^.className := "author", props.article.author),
                <.span(s" on "),
                <.a(^.className := "date", s"${props.article.asJsDate.toDateString()}")
              )
            )
          )
        )
      )
    }

  }

  val component = ReactComponentB[Props]("ArticleView")
    .stateless
    .renderBackend[Backend]
    .build

  def apply(article: Article, ctl: RouterCtl[View]) = component(Props(article, ctl))

}
