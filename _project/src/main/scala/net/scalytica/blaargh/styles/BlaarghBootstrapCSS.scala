/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.styles

import scalacss.Defaults._

object BlaarghBootstrapCSS extends StyleSheet.Inline {

  import dsl._

  object Mixins {
    val container = mixin(addClassName("container"))
    val row = mixin(addClassName("row"))
    val card = mixin(addClassName("card"))
    val cardBlock = mixin(addClassName("card-block"))
    val cardTitle = mixin(addClassName("card-title"))
    val cardText = mixin(addClassName("card-text"))
    val imgCircle = mixin(addClassName("img-circle"))
    val centerBlock = mixin(addClassName("center-block"))
    val textMuted = mixin(addClassName("text-muted"))
    val textXsRight = mixin(addClassName("text-xs-right"))
    val textXsCenter = mixin(addClassName("text-xs-center"))
    val navbarFixedBottom = mixin(addClassName("navbar-fixed-bottom"))
    val labelDefault = mixin(addClassNames("label", "label-default"))
    val labelInfo = mixin(addClassNames("label", "label-info"))
  }

  val blaarghHeader = style("blaargh-header")(
    flex := "0 1 auto",
    minHeight(20.em)
  )

  val blaarghHeaderSVGContainer = style(
    minHeight(20.em),
    backgroundImage := "url(assets/images/banner.png)",
    backgroundSize := "cover",
    backgroundRepeat := "no-repeat",
    backgroundPosition := "center",
    marginBottom(20.px),
    width(100.%%),
    left.`0`,
    right.`0`
  )

  val blaarghSVGHeaderText = style(
    position.relative,
    left.`0`,
    width(100.%%),
    height(10.em),
    zIndex(10)
  )

  val blaarghContent = style("blaargh-content")(
    flex := "1 1 auto",
    paddingTop(2.em)
  )

  val blaarghFooter = style("blaargh-footer")(
    Mixins.textXsRight,
    Mixins.textMuted,
    Mixins.navbarFixedBottom,
    backgroundColor.whitesmoke,
    flex := "0 1 40px"
  )

  val box = style("box")(
    display.flex,
    flexFlow := "column",
    height(100.%%)
  )

  val container = style("blaargh-container")(Mixins.container)
  val row = style("blaargh-row")(Mixins.row)
  val col = styleF.int(1 to 12)(size => addClassName(s"col-xs-$size"))
  val card = style("blaargh-card")(Mixins.card)
  val cardBlock = style("blaargh-card-block")(Mixins.cardBlock)
  val cardTitle = style("blaargh-card-title")(Mixins.cardTitle)
  val cardText = style("blaargh-card-text")(Mixins.cardText)
  val imgCircle = style("blaargh-img-circle")(Mixins.imgCircle)
  val centerBlock = style("blaargh-center-block")(Mixins.centerBlock)
  val textMuted = style("blaargh-text-muted")(Mixins.textMuted)
  val textXsRight = style("blaargh-text-xs-right")(Mixins.textXsRight)
  val textXsCenter = style("blaargh-text-xs-center")(Mixins.textXsCenter)
  val labelInfo = style("blaargh-label-info")(
    Mixins.labelInfo,
    padding(.2.em),
    marginLeft(.2.em),
    marginRight(.2.em)
  )
  val labelDefault = style("blaargh-label-default")(
    Mixins.labelDefault,
    padding(.2.em),
    marginLeft(.2.em),
    marginRight(.2.em)
  )

}
