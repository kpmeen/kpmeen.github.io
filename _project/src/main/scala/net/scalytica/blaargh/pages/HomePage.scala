/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import net.scalytica.blaargh.components.ArticleCardList
import net.scalytica.blaargh.pages.Views.View

object HomePage {

  case class Props(ctl: RouterCtl[View])

  val component = ScalaComponent
    .builder[Props]("Home")
    .render($ => ArticleCardList($.props.ctl).vdomElement)
    .build

  def apply(ctl: RouterCtl[View]) = component(Props(ctl))
}
