/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.App.View
import net.scalytica.blaargh.components.ArticleListView


object HomePage {

  case class Props(ctl: RouterCtl[View])

  val component = ReactComponentB[Props]("Home")
    .initialState_P(p => p)
    .render { $ =>
      <.div(^.className := "container-fluid",
        ArticleListView($.props.ctl)
      )
    }
    .build


  def apply(ctl: RouterCtl[View]) = component(Props(ctl))
}
