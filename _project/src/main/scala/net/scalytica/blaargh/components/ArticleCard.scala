/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.{Posts, View}
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import net.scalytica.blaargh.utils.StringUtils._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object ArticleCard {

  case class Props(article: Article, ctl: RouterCtl[View])

  object Styles extends StyleSheet.Inline {

    import dsl._

    val articleCard = style("blaargh-article-card")(
      mixin(BlaarghBootstrapCSS.Mixins.card),
      &.hover(
        BlaarghBootstrapCSS.Mixins.cardShadow,
        BlaarghBootstrapCSS.Mixins.cardShadowAnimation
      )
    )

    val cardTitle = style("blaargh-card-title")(
      addClassName("card-title"),
      color.black,
      cursor.pointer,
      &.hover(
        textDecoration := "none"
      )
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
        <.a(Styles.cardTitle,
          <.h4(^.onClick --> openArticleCB(props), title)
        )
      ).getOrElse(EmptyTag))

    def maybeImage(props: Props) =
      asOption(props.article.image).map(image =>
        <.img(Styles.cardImage, ^.src := image, ^.onClick --> openArticleCB(props))
      )

    def openArticleCB(props: Props): Callback = props.ctl.byPath.set(Posts(props.article.articleRef).asPath)


    def render(props: Props) = {
      <.div(Styles.articleCard,
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
            ),
            if (props.article.labels.nonEmpty) <.span(props.article.labels.map(l => Label(l, props.ctl)))
            else EmptyTag
          )
        )
      )
    }

  }

  val component = ReactComponentB[Props]("ArticleCard")
    .stateless
    .renderBackend[Backend]
    .build

  def apply(article: Article, ctl: RouterCtl[View]) = component(Props(article, ctl))

}
