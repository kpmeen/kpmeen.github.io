/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import scalacss.ScalaCssReact._

object Label {

  val component = ReactComponentB[String]("Label")
    .stateless
    .render { $ =>
      <.span(BlaarghBootstrapCSS.label, $.props)
    }
    .build

  def apply(s: String) = component(s)
}
