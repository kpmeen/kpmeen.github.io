/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import net.scalytica.blaargh.components.ArticleCardList
import net.scalytica.blaargh.pages.Views.View


object HomePage {

  case class Props(ctl: RouterCtl[View])

  val component = ReactComponentB[Props]("Home")
    .initialState_P(p => p)
    .render($ => ArticleCardList($.props.ctl))
    .build


  def apply(ctl: RouterCtl[View]) = component(Props(ctl))
}
