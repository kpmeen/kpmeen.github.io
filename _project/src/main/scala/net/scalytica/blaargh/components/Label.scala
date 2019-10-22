/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.pages.Views.{Filter, FilterCriteria, View}
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import scalacss.ScalaCssReact._

object Label {

  case class Props(lbl: String, ctl: RouterCtl[View])

  val component = ScalaComponent
    .builder[Props]("Label")
    .initialStateFromProps(p => p)
    .render { $ =>
      <.span(
        BlaarghBootstrapCSS.labelDefault,
        ^.cursor.pointer,
        ^.onClick --> $.state.ctl.byPath
          .set(Filter(FilterCriteria("label", $.props.lbl)).asPath),
        $.props.lbl
      )
    }
    .build

  def apply(label: String, ctl: RouterCtl[View]) = component(Props(label, ctl))
}
