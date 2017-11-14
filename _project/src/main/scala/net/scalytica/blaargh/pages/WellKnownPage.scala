package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object WellKnownPage {

  val component = ScalaComponent.builder[String]("WellKnownPage")
    .initialStateFromProps(s => s)
    .render($ => <.body($.props))
    .build

  def apply(s: String) = component(s)

}
