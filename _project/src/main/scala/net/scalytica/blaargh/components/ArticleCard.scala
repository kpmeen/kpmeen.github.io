/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.{Filter, FilterCriteria, Posts, View}
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
      marginRight(0.75.rem),
      marginTop(0.75.rem),
      transitionProperty := "box-shadow",
      BlaarghBootstrapCSS.Mixins.easeInOutAnimation,
      &.hover(
        BlaarghBootstrapCSS.Mixins.cardShadow
      )
    )

    val cardTitle = style("blaargh-article-card-title")(
      addClassName("card-title"),
      color.black,
      cursor.pointer,
      &.hover(
        textDecoration := "none"
      )
    )

    val cardImage = style("blaargh-article-card-image")(
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
          <.div(BlaarghBootstrapCSS.cardBlock,
            title,
            <.p(BlaarghBootstrapCSS.cardText,
              props.article.ingress
            ),
            <.p(BlaarghBootstrapCSS.cardText,
              <.small(BlaarghBootstrapCSS.textMuted,
                <.span("by "),
                <.a(
                  BlaarghBootstrapCSS.author,
                  ^.onClick --> props.ctl.byPath.set(Filter(FilterCriteria("author", props.article.author)).asPath),
                  props.article.author
                ),
                <.span(s" on "),
                <.a(BlaarghBootstrapCSS.date, s"${props.article.asJsDate.toDateString()}")
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
