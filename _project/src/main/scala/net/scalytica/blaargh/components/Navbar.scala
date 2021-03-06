/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.PackageBase.VdomAttr
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.CssSettings._
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.pages.Views.{About, Home, Posts, Theater, View}
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import org.scalajs.dom
import scalacss.ScalaCssReact._

object Navbar {

  object Styles extends StyleSheet.Inline {

    import dsl._

    val blaarghBrand = style("blaargh-brand")(
      addClassName("navbar-brand"),
      marginRight(10.rem)
    )

    val blaarghNavbarLink =
      style("blaargh-navbar-link")(addClassName("nav-link"))

    val blaarghNavItem = styleF("blaargh-nav-item").bool { isActive =>
      styleS(
        if (isActive) addClassNames("nav-item", "active")
        else addClassName("nav-item")
      )
    }

    val navActiveHamburger = style(addClassName("sr-only"))

    val navToggler = style(addClassNames("navbar-toggler", "hidden-sm-up"))

    val navCollapse = style(addClassNames("collapse", "navbar-toggleable-xs"))

    val navbar = style(addClassNames("nav", "navbar-nav", "pull-right"))

    val blaarghNav = styleF("blaargh-nav").bool { isScrolling =>
      // scalastyle:off magic.number
      val bg =
        if (isScrolling) backgroundColor.rgba(245, 245, 245, 0.9)
        else backgroundColor.transparent
      val fg = if (isScrolling) color.inherit else color.rgb(245, 245, 245)

      styleS(
        addClassNames("navbar", "navbar-light", "navbar-fixed-top"),
        bg,
        fg,
        unsafeChild(".blaargh-brand")(style(color.inherit.important)),
        unsafeChild(".nav-item .nav-link")(
          style(
            color.rgb(150, 150, 150),
            backgroundColor.inherit
          )
        ),
        unsafeChild(".active")(
          style(
            unsafeChild(".nav-link")(
              style(
                color.inherit.important
              )
            )
          )
        ),
        zIndex(14),
        boxShadow := "1px 2px, 8px lightgrey",
        transitionProperty := "background-color, color",
        BlaarghBootstrapCSS.Mixins.easeOutAnimation
      )
      // scalastyle:on magic.number
    }
  }

  case class Props(siteConfig: Config, currPage: View, ctl: RouterCtl[View])

  case class State(isScrolling: Boolean = false)

  class Backend($ : BackendScope[Props, State]) extends OnUnmount {

    def onScroll: Callback =
      $.state.flatMap { _ =>
        if (dom.window.pageYOffset > 5.0) $.setState(State(isScrolling = true))
        else $.setState(State())
      }

    def MenuItem(props: Props, menuName: String, menuItem: View) = {
      val isActive =
        props.currPage == menuItem || (menuItem == Home && props.currPage
          .isInstanceOf[Posts])

      <.li(
        Styles.blaarghNavItem(isActive),
        <.a(
          Styles.blaarghNavbarLink,
          ^.href := "_",
          ^.onClick ==> { e: ReactEventFromInput =>
            e.preventDefaultCB >> props.ctl.set(menuItem)
          },
          <.span(menuName),
          if (isActive) <.span(Styles.navActiveHamburger, "(current)")
          else EmptyVdom
        )
      )
    }

    def render(props: Props, state: State) = {
      <.nav(
        Styles.blaarghNav(state.isScrolling),
        <.div(
          BlaarghBootstrapCSS.container,
          <.button(
            Styles.navToggler,
            ^.tpe := "button",
            VdomAttr("data-toggle") := "collapse",
            VdomAttr("data-target") := "#collapseNav",
            "\u2630"
          ),
          <.div(
            Styles.navCollapse,
            ^.id := "collapseNav",
            <.a(
              Styles.blaarghBrand,
              ^.href := "_",
              ^.onClick ==> { e: ReactEventFromInput =>
                e.preventDefaultCB >> props.ctl.set(Home)
              },
              s"${props.siteConfig.siteTitle.toUpperCase}"
            ),
            <.ul(
              Styles.navbar,
              MenuItem(props, "BLOG", Home),
              MenuItem(props, "ABOUT", About)
//              MenuItem(props, "THEATER", Theater)
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent
    .builder[Props]("Navbar")
    .initialState(State())
    .renderBackend[Backend]
    .configure(
      EventListener.install(
        eventType = "scroll",
        listener = _.backend.onScroll,
        target = _ => dom.window,
        useCapture = true
      )
    )
    .build

  def apply(siteConf: Config, currPage: View, ctl: RouterCtl[View]) =
    component(Props(siteConf, currPage, ctl))

}
