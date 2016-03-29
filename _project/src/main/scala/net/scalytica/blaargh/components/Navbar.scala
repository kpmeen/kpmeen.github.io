/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.App.{About, Home, Posts, View}
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object Navbar {

  object Styles extends StyleSheet.Inline {

    import dsl._

    val blaarghNav = style("blaargh-nav")(
      addClassNames("navbar", "navbar-light", "navbar-fixed-top"),
      zIndex(14),
      backgroundColor.whitesmoke,
      boxShadow := "1px 2px, 8px lightgrey"
    )

    val navbarBrand = style("blaargh-brand")(
      addClassName("navbar-brand"),
      marginRight(10.rem)
    )

    val navbarLink = style("blaargh-navbar-link")(addClassName("nav-link"))

    val navItem = styleF.bool(isActive => styleS(
      if (isActive) addClassNames("nav-item", "active") else addClassName("nav-item")
    ))

    val navActiveHamburger = style(addClassName("sr-only"))
    val navToggler = style(addClassNames("navbar-toggler", "hidden-sm-up"))
    val navCollapse = style(addClassNames("collapse", "navbar-toggleable-xs"))
    val navbar = style(addClassNames("nav", "navbar-nav", "pull-right"))
  }

  case class Props(siteConfig: Config, currPage: View, ctl: RouterCtl[View])

  val component = ReactComponentB[Props]("Navbar")
    .render { $ =>
      val props = $.props
      def MenuItem(menuName: String, menuItem: View) = {
        val isActive = props.currPage == menuItem || (menuItem == Home && props.currPage.isInstanceOf[Posts])

        <.li(Styles.navItem(isActive),
          <.a(Styles.navbarLink, ^.href := "_", ^.onClick ==> { (e: ReactEventI) => e.preventDefaultCB >> props.ctl.set(menuItem) },
            <.span(menuName),
            if (isActive) <.span(Styles.navActiveHamburger, "(current)") else EmptyTag
          )
        )
      }
      <.nav(Styles.blaarghNav,
        <.div(BlaarghBootstrapCSS.container,
          <.button(
            Styles.navToggler,
            ^.tpe := "button",
            "data-toggle".reactAttr := "collapse",
            "data-target".reactAttr := "#collapseNav",
            "\u2630"
          ),
          <.div(Styles.navCollapse, ^.id := "collapseNav",
            <.a(
              Styles.navbarBrand,
              ^.href := "_",
              ^.onClick ==> { (e: ReactEventI) => e.preventDefaultCB >> props.ctl.set(Home) },
              s"${props.siteConfig.siteTitle.toUpperCase}"
            ),
            <.ul(Styles.navbar,
              MenuItem("BLOG", Home),
              MenuItem("ABOUT", About)
            )
          )
        )
      )
    }
    .build


  def apply(siteConf: Config, currPage: View, ctl: RouterCtl[View]) = component(Props(siteConf, currPage, ctl))

}
