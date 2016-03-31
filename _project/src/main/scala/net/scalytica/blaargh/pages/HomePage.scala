/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.components.ArticleCardList
import net.scalytica.blaargh.models.Article
import net.scalytica.blaargh.pages.Views.View

import scala.concurrent.Future


object HomePage {

  case class Props(articles: Future[Seq[Article]], ctl: RouterCtl[View])

  val component = ReactComponentB[Props]("Home")
    .initialState_P(p => p)
    .render { $ =>
      <.div(^.className := "container-fluid",
        ArticleCardList($.props.articles, $.props.ctl)
      )
    }
    .build


  def apply(articles: Future[Seq[Article]], ctl: RouterCtl[View]) = component(Props(articles, ctl))
}
