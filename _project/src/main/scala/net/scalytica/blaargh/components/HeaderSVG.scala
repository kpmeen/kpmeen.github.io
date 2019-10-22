/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import net.scalytica.blaargh.models.Config

import java.nio.charset.StandardCharsets
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

object HeaderSVG {

  object Styles extends StyleSheet.Inline {

    import dsl._

    val svg = style("svg-header")(
      width(100.%%),
      height(20.em),
      unsafeChild("text")(
        svgTextAnchor := "middle"
      )
    )

    val alpha = style("svg-header-alpha")(
      svgFill := rgb(190, 190, 190)
    )
    val title = style("svg-header-title")(
      letterSpacing :=! "-2px",
      fontSize(6.em),
      fontWeight :=! "800"
    )
    val subTitle = style("svg-header-subtitle")(
      letterSpacing(6.px),
      fontSize(1.2.em),
      fontWeight :=! "300",
      textTransform.uppercase
    )
    val base = style("base")(
      svgFill := "grey",
      mask := "url(#mask)"
    )
  }

  class Backend($ : BackendScope[Config, Unit]) {

    def render(props: Config) = {
      val subTitle = props.owner.name
        .filter(_.isUpper)
        .getBytes(StandardCharsets.UTF_8)
        .map(b => Integer.toBinaryString(b: Int))

      <.svg(
        Styles.svg,
        <.defs(
          <.maskTag(
            ^.id := "mask",
            ^.x := "0",
            ^.y := "0",
            ^.width := "100%",
            ^.height := "100%",
            <.rect(
              Styles.alpha,
              ^.id := "alpha",
              ^.x := "0",
              ^.y := "0",
              ^.width := "100%",
              ^.height := "100%"
            ),
            <.text(
              Styles.title,
              ^.id := "title",
              ^.x := "50%",
              ^.y := "0",
              ^.dy := "1.58em",
              props.siteTitle
            ),
            <.text(
              Styles.subTitle,
              ^.id := "subtitle",
              ^.x := "50%",
              ^.y := "0",
              ^.dy := "9.8em",
              subTitle.mkString(" ")
            )
          )
        ),
        <.rect(
          Styles.base,
          ^.id := "base",
          ^.x := "0",
          ^.y := "0",
          ^.width := "100%",
          ^.height := "100%"
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Config]("HeaderSVG")
    .stateless
    .renderBackend[Backend]
    .build

  def apply(conf: Config) = component(conf)

}
