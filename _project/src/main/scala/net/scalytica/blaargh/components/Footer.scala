/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import scala.scalajs.js
import scalacss.ScalaCssReact._

object Footer {

  val component = ScalaComponent
    .builder[Config]("Footer")
    .render { $ =>
      <.div(
        BlaarghBootstrapCSS.container,
        <.span(
          s"© ${new js.Date().getFullYear()} ${$.props.owner.name}, " +
            "all rights reserved. Powered by "
        ),
        <.a(
          BlaarghBootstrapCSS.textMuted,
          ^.href := "https://github.com/kpmeen/blaargh",
          ^.target := "_blank",
          "Blaargh!"
        )
      )
    }
    .build

  def apply(siteConfig: Config) = component(siteConfig)

}
