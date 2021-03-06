/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.styles

import net.scalytica.blaargh.components._
import net.scalytica.blaargh.pages.{AboutPage, TheaterPage}
import scalacss.ProdDefaults._
import scalacss.internal.mutable.GlobalRegistry

object CSSRegistry {

  def load(): Unit = {
    GlobalRegistry.register(
      BaseCSS,
      BlaarghBootstrapCSS,
      HeaderSVG.Styles,
      Navbar.Styles,
      ArticleCard.Styles,
      AboutPage.Styles,
      ArticleView.Styles,
      TheaterPage.Styles
    )
  }

  GlobalRegistry.onRegistration(_.addToDocument())
}
