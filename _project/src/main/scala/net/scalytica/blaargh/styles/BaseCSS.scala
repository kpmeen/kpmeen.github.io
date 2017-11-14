/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.styles

import scalacss.ProdDefaults._
import scalacss.internal.Attr

object BaseCSS extends StyleSheet.Inline {

  import dsl._

  val html = style(unsafeRoot("html")(
    textRendering := "optimizelegibility",
    Attr.real("-webkit-font-smoothing") := "grayscale",
    Attr.real("-moz-osx-font-smoothing") := "optimizelegibility"
  ))

  val base = style(unsafeRoot("html, body")(
    margin.`0`,
    fontSize(14.px),
    height(100.%%)
  ))

  val app = style("blaargh")(
    height(100.%%),
    margin.`0`
  )

  val imageWrap = style("image-wrap")(
    position.relative,
    marginBottom(2.em),
    unsafeChild("img")(style(
      width(100.%%),
      height.auto
    ))
  )

}
