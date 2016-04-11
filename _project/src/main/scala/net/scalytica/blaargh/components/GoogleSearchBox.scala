/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.document


object GoogleSearchBox {

  class Backend($: BackendScope[Unit, Unit]) {

    def scriptGen() = {
      val cx = "007449460818485879211:oyngikjfvr0"
      val googleScript = {
        val scr = document.createElement("script")
        scr.setAttribute("type", "text/javascript")
        scr.setAttribute("async", "true")
        scr.setAttribute("src", s"${document.location.protocol}//cse.google.com/cse.js?cx=$cx")
        scr
      }

      val s = document.getElementsByTagName("script")(0)
      s.parentNode.insertBefore(googleScript, s)
    }

    def render() = {
      <.div(^.dangerouslySetInnerHtml("<gcse:search></gcse:search>"))
    }
  }

  val component = ReactComponentB[Unit]("GoogleSearchBox")
    .stateless
    .renderBackend[Backend]
    .componentWillMount($ => Callback($.backend.scriptGen()))
    .buildU


  def apply() = component()

}
