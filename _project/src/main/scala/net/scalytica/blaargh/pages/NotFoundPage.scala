/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import net.scalytica.blaargh.CssSettings._
import scalacss.ScalaCssReact._
import scalacss.internal.StyleA

object NotFoundPage {

  val combStyle: StyleA =
    BlaarghBootstrapCSS.container + BlaarghBootstrapCSS.textXsCenter

  val component = ScalaComponent
    .builder[Unit]("NotFoundPage")
    .stateless
    .render { _ =>
      val binaryPageNotFound = Seq(
        "01110000",
        "01100001",
        "01100111",
        "01100101",
        "00100000",
        "01101110",
        "01101111",
        "01110100",
        "00100000",
        "01100110",
        "01101111",
        "01110101",
        "01101110",
        "01100100"
      )
      // scalastyle:off magic.number
      <.div(
        combStyle,
        binaryPageNotFound
          .grouped(7)
          .zipWithIndex
          .map { grp =>
            <.h2(
              ^.key := s"${grp._2}_notfound",
              grp._1.map(bin => <.span(^.marginRight := "1.2rem", bin)).toTagMod
            )
          }
          .toTagMod
      )
    // scalastyle:on magic.number
    }
    .build

  def apply() = component()
}
