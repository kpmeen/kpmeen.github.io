/**
  * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
  */
package net.scalytica.blaargh

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.components.{ArticleView, Footer, HeaderSVG, Navbar}
import net.scalytica.blaargh.models.{ArticleRef, Config}
import net.scalytica.blaargh.pages._
import net.scalytica.blaargh.styles.{BlaarghBootstrapCSS, CSSRegistry}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalacss.ScalaCssReact._


object App extends JSApp {

  sealed trait View

  case object Home extends View

  case object About extends View

  case object NotFound extends View

  case class Posts(ref: ArticleRef) extends View

  val siteConfig = Config.load()

  val postsRule = RouterConfigDsl[ArticleRef].buildRule { dsl =>
    import dsl._

    dynamicRouteCT((string("[^\\/]*") / string("(.*)$")).caseClass[ArticleRef]) ~> dynRenderR((ref, ctl) => ArticleView(ref))
  }

  val routerConfig = RouterConfigDsl[View].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute("", Home) ~> renderR(ctl => HomePage(ctl))
      | staticRoute("#about", About) ~> render(AboutPage(siteConfig))
      | staticRoute("#notfound", NotFound) ~> render(NotFoundPage())
      | postsRule.prefixPath_/("#posts").pmap[View](Posts) { case Posts(ref) => ref }
      )
      .notFound(nfp => redirectToPage(NotFound)(Redirect.Replace))
      .renderWith((ctl, r) => layout(ctl, r))
  }

  def layout(ctl: RouterCtl[View], r: Resolution[View]) = {
    BlaarghLayout(siteConfig, ctl, r)
  }

  val baseUrl = BaseUrl.until_#

  val router = Router(baseUrl, routerConfig)

  @JSExport
  override def main(): Unit = {
    CSSRegistry.load()
    router().render(org.scalajs.dom.document.getElementsByClassName("blaargh")(0))
  }

  object BlaarghLayout {

    case class Props(conf: Future[Config], ctl: RouterCtl[View], r: Resolution[View])
    case class State(conf: Config, ctl: RouterCtl[View], r: Resolution[View])

    class Backend($: BackendScope[Props, State]) {
      def init: Callback = {
        $.props.map(p =>
          Callback.future[Unit] {
            p.conf.map(c => $.modState(_.copy(conf = c)))
          }.runNow()
        )
      }

      def render(props: Props, state: State) = {
        <.div(BlaarghBootstrapCSS.box)(
          Navbar(state.conf, props.r.page, props.ctl),
          <.header(BlaarghBootstrapCSS.blaarghHeader,
            <.div(BlaarghBootstrapCSS.blaarghHeaderSVGContainer,
              <.div(BlaarghBootstrapCSS.blaarghSVGHeaderText,
                HeaderSVG(state.conf)
              )
            )
          ),
          <.section(BlaarghBootstrapCSS.blaarghContent,
            <.div(BlaarghBootstrapCSS.container,
              props.r.render()
            )
          ),
          <.footer(BlaarghBootstrapCSS.blaarghFooter,
            Footer(state.conf)
          )
        )
      }
    }

    val component = ReactComponentB[Props]("BlaarghLayout")
      .initialState_P(p => State(Config.empty, p.ctl, p.r))
      .renderBackend[Backend]
      .componentWillMount(_.backend.init)
      .build

    def apply(conf: Future[Config], ctl: RouterCtl[View], r: Resolution[View]) = component(Props(conf, ctl, r))
  }

}
