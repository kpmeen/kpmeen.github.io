/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react.ReactComponentB
import japgolly.scalajs.react.vdom.prefix_<^._

object NotFoundPage {

  val component = ReactComponentB[Unit]("NotFoundPage")
    .stateless
    .render { _ =>
      <.div("NotFound")
    }
    .buildU

  def apply() = component()
}
