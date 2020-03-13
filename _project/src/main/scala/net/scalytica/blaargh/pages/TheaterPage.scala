/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.CssSettings._
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
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

    val theaterContainer = style("blaargh-theater-container")(
      BlaarghBootstrapCSS.Mixins.container,
      addClassName("text-xs-center"),
      border.`0`,
      boxShadow := "none"
    )

    val streamingServiceRow = style("blaargh-streaming-service-row")(
      BlaarghBootstrapCSS.Mixins.row,
      display.flex,
      marginLeft.auto,
      marginRight.auto
    )

    val streamingServiceWrapper = style("blaargh-streaming-service-wrapper")(
      padding(2.px),
      height(200.px),
      width(200.px),
      display.flex,
      marginLeft.auto,
      marginRight.auto
    )

    val theaterLogoLink = style("blaargh-streaming-service--logo-link")(
      display.flex,
      margin.auto
    )

    val theaterLogo = style("blaargh-streaming-service-logo")(
      BlaarghBootstrapCSS.Mixins.centerBlock,
      width(180.px),
      display.block,
      margin.auto
    )

    val fullscreenButtonWrapper = style("blaargh-fullscreen-wrapper")(
      marginTop(60.px)
    )

    val fullscreenButton = style("blaargh-fullscreen-btn")(
      BlaarghBootstrapCSS.Mixins.button,
      BlaarghBootstrapCSS.Mixins.buttonPrimary
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
        Styles.theaterContainer,
        if (state.conf.theaterConfig.streamingServices.nonEmpty) {
          state.conf.theaterConfig.streamingServices
            .grouped(4)
            .map { grpd =>
              <.div(
                Styles.streamingServiceRow,
                grpd.map { ss =>
                  <.div(
                    Styles.streamingServiceWrapper,
                    BlaarghBootstrapCSS.col(4),
                    <.a(
                      Styles.theaterLogoLink,
                      ^.href := ss.url,
                      <.figure(
                        <.img(
                          Styles.theaterLogo,
                          ^.src := ss.logoImage
                        )
                      )
                    )
                  )
                }.toVdomArray
              )
            }
            .toVdomArray
        } else {
          <.span("There are no services in the config!")
        },
        <.div(
          Styles.fullscreenButtonWrapper,
          <.button(
            Styles.fullscreenButton,
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
