/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.styles

import net.scalytica.blaargh.components.{ArticlePreview, HeaderSVG, Navbar}
import net.scalytica.blaargh.pages.AboutPage

import scalacss.ScalaCssReact._
import scalacss.mutable.GlobalRegistry
import scalacss.Defaults._

object CSSRegistry {

  def load() = {
    GlobalRegistry.register(
      BaseCSS,
      BlaarghBootstrapCSS,
      HeaderSVG.Styles,
      Navbar.Styles,
      ArticlePreview.Styles,
      AboutPage.Styles
    )
  }

  GlobalRegistry.onRegistration(_.addToDocument())
}
