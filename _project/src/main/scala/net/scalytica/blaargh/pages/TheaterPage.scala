/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.CssSettings._
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import net.scalytica.blaargh.utils.StringUtils
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import scalacss.ScalaCssReact._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object TheaterPage {

  case class Props(siteConf: Config)

  case class State(conf: Config, content: Option[String])

  object Styles extends StyleSheet.Inline {

    import dsl._

    val theaterContainer = style("blaargh-theater-card")(
      BlaarghBootstrapCSS.Mixins.containerFluid,
      addClassName("text-xs-center"),
      border.`0`,
      boxShadow := "none"
    )

    val theaterLogo = style("blaarg-theater-logo")(
      BlaarghBootstrapCSS.Mixins.centerBlock,
      height(120.px),
      width(120.px)
    )
  }

  class Backend($ : BackendScope[Props, State]) {

    def init: Callback = $.props.map { p =>
      Callback
        .future[Unit] {
          loadPage.map { page =>
            $.modState(_.copy(conf = p.siteConf, content = page))
          }
        }
        .runNow()
    }

    def loadPage: Future[Option[String]] =
      Ajax.get(url = "pages/theater.html").map { xhr =>
        xhr.status match {
          case ok: Int if ok == 200 => Some(xhr.responseText)
          case _                    => None
        }
      }

    // scalastyle:off magic.number method.length

    def render(props: Props, state: State) = {
      def redirectHack() = Callback[Unit] {
        dom.window.location.href = state.conf.theaterConfig.redirectUrl
      }

      <.div(
        BlaarghBootstrapCSS.container,
        Styles.theaterContainer,
        if (state.conf.theaterConfig.streamingServices.nonEmpty) {
          state.conf.theaterConfig.streamingServices.map { ss =>
            <.div(
              <.a(
                ^.href := ss.url,
                <.figure(
                  ^.cls := "theater_figure",
                  <.img(
                    Styles.theaterLogo,
                    ^.src := ss.logoImage
                  )
                )
              )
            )
          }.toVdomArray
        } else {
          <.span("There are no services in the config!")
        },
        <.div(
          <.button(
            ^.onClick --> redirectHack(),
            "ENABLE FULLSCREEN"
          )
        )
      )
    }

    // scalastyle:on magic.number method.length
  }

  val component = ScalaComponent
    .builder[Props]("Theater")
    .initialStateFromProps(p => State(p.siteConf, None))
    .renderBackend[Backend]
    .build

  def apply(conf: Config) = component(Props(conf))

}
