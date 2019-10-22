/**
 * Copyright(c) 2019 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._
import net.scalytica.blaargh.components._
import net.scalytica.blaargh.models.{Article, Config}
import net.scalytica.blaargh.pages.Views._
import net.scalytica.blaargh.pages._
import net.scalytica.blaargh.styles.{BlaarghBootstrapCSS, CSSRegistry}
import net.scalytica.blaargh.utils.StaticConfig

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExportTopLevel
import scalacss.ScalaCssReact._

object App {

  val slash = "[^\\/]*"
  val all   = "(.*)$"

  val postsRule = RouterConfigDsl[ArticleRef].buildRule { dsl =>
    import dsl._

    dynamicRouteCT((string(slash) / string(all)).caseClass[ArticleRef]) ~>
      dynRenderR { (ref, ctl) =>
        ArticleView(
          article = Article.Articles.map(_.find(_.filename == ref.filename)),
          ref = ref,
          ctl = ctl
        )
      }
  }

  val filterRule = RouterConfigDsl[FilterCriteria].buildRule { dsl =>
    import dsl._

    dynamicRouteCT((string(slash) / string(all)).caseClass[FilterCriteria]) ~>
      dynRenderR { (lbl, ctl) =>
        SearchResultsPage(lbl, ctl.contramap[View](v => lbl))
      }
  }

  val routerConfig = (cfg: Config) =>
    RouterConfigDsl[View].buildConfig { dsl =>
      import dsl._

      (trimSlashes
        | staticRoute(Home.basePath, Home) ~> renderR(ctl => HomePage(ctl))
        | staticRoute(About.basePath, About) ~> render(AboutPage(cfg))
        | staticRoute(NotFound.basePath, NotFound) ~> render(NotFoundPage())
        | filterRule.prefixPath_/(Filter.basePath).pmap[View](Filter.apply) {
          case Filter(criteria) => criteria
        }
        | postsRule.prefixPath_/(Posts.basePath).pmap[View](Posts.apply) {
          case Posts(ref) => ref
        })
        .notFound(_ => redirectToPage(NotFound)(Redirect.Replace))
        .renderWith((ctl, r) => layout(cfg)(ctl, r))
    }

  val router = (cfg: Config) => Router(StaticConfig.baseUrl, routerConfig(cfg))

  def layout(cfg: Config)(ctl: RouterCtl[View], r: Resolution[View]) =
    BlaarghLayout(cfg, ctl, r)

  @JSExportTopLevel("net.scalytica.blaargh.App")
  protected def getInstance(): this.type = this

  def main(): Unit = {
    CSSRegistry.load()
    Config.load().map { cfg =>
      val container =
        org.scalajs.dom.document.getElementsByClassName("blaargh")(0)
      val rn = ReactExt_DomNode(container)
      router(cfg)().renderIntoDOM(rn.domAsHtml)
    }
  }

  object BlaarghLayout {

    case class Props(conf: Config, ctl: RouterCtl[View], r: Resolution[View])

    case class State(conf: Config)

    class Backend($ : BackendScope[Props, State]) {
      val ga = Dynamic.global.ga

      def init: Callback = {
        $.props.map { p =>
          Callback[Unit] {
            ga("create", p.conf.owner.googleAnalytics, "auto")
            ga("send", "pageview")
            $.modState(_.copy(conf = p.conf))
          }.runNow()
        }
      }

      def feedAnalytics(props: Props): Callback =
        Callback {
          ga("set", "page", props.ctl.pathFor(props.r.page).value)
          ga("send", "pageview")
        }

      def render(props: Props, state: State) = {
        <.div(BlaarghBootstrapCSS.box)(
          Navbar(state.conf, props.r.page, props.ctl),
          <.header(
            BlaarghBootstrapCSS.blaarghHeader,
            <.div(
              BlaarghBootstrapCSS.blaarghHeaderSVGContainer,
              <.div(
                BlaarghBootstrapCSS.blaarghSVGHeaderText,
                HeaderSVG(props.conf)
              )
            )
          ),
          <.section(
            BlaarghBootstrapCSS.blaarghContent,
            <.div(BlaarghBootstrapCSS.container, props.r.render())
          ),
          <.footer(BlaarghBootstrapCSS.blaarghFooter, Footer(state.conf))
        )
      }
    }

    val component = ScalaComponent
      .builder[Props]("BlaarghLayout")
      .initialStateFromProps(p => State(Config.empty))
      .renderBackend[Backend]
      .componentWillMount(_.backend.init)
      .componentWillReceiveProps($ => $.backend.feedAnalytics($.nextProps))
      .build

    def apply(conf: Config, ctl: RouterCtl[View], r: Resolution[View]) =
      component(Props(conf, ctl, r))

  }

}
