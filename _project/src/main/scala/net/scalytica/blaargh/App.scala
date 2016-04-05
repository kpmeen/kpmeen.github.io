/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.components._
import net.scalytica.blaargh.models.{Article, Config}
import net.scalytica.blaargh.pages.Views._
import net.scalytica.blaargh.pages._
import net.scalytica.blaargh.styles.{BlaarghBootstrapCSS, CSSRegistry}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js._
import scala.scalajs.js.annotation.JSExport
import scalacss.ScalaCssReact._


object App extends JSApp {

  val SiteConfig = Config.load()

  val postsRule = RouterConfigDsl[ArticleRef].buildRule { dsl =>
    import dsl._

    dynamicRouteCT((string("[^\\/]*") / string("(.*)$")).caseClass[ArticleRef]) ~>
      dynRenderR { (ref, ctl) =>
        ArticleView(Article.Articles.map(_.find(_.filename == ref.filename)), ref, ctl)
      }
  }

  val filterRule = RouterConfigDsl[FilterCriteria].buildRule { dsl =>
    import dsl._

    dynamicRouteCT((string("[^\\/]*") / string("(.*)$")).caseClass[FilterCriteria]) ~>
      dynRenderR { (lbl, ctl) =>
        SearchResultsPage(lbl, ctl.contramap[View](v => lbl))
      }
  }

  val routerConfig = RouterConfigDsl[View].buildConfig { dsl =>
    import dsl._

    (trimSlashes
      | staticRoute("", Home) ~> renderR(ctl => HomePage(ctl))
      | staticRoute("#about", About) ~> render(AboutPage(SiteConfig))
      | staticRoute("#notfound", NotFound) ~> render(NotFoundPage())
      | filterRule.prefixPath_/("#filter").pmap[View](Filter.apply) { case Filter(criteria) => criteria }
      | postsRule.prefixPath_/("#posts").pmap[View](Posts.apply) { case Posts(ref) => ref }
      )
      .notFound(nfp => redirectToPage(NotFound)(Redirect.Replace))
      .renderWith((ctl, r) => layout(ctl, r))
  }
  val baseUrl = BaseUrl.until_#
  val router = Router(baseUrl, routerConfig) //.logToConsole)

  def layout(ctl: RouterCtl[View], r: Resolution[View]) = {
    BlaarghLayout(SiteConfig, ctl, r)
  }

  @JSExport
  override def main(): Unit = {
    CSSRegistry.load()
    router().render(org.scalajs.dom.document.getElementsByClassName("blaargh")(0))
  }

  object BlaarghLayout {

    case class Props(futureConf: Future[Config], ctl: RouterCtl[View], r: Resolution[View])

    case class State(conf: Config)

    class Backend($: BackendScope[Props, State]) {
      val ga = Dynamic.global.ga

      def init: Callback = {
        $.props.map { p =>
          Callback.future[Unit] {
            p.futureConf.map { c =>
              ga("create", c.owner.googleAnalytics, "auto")
              ga("send", "pageview")
              $.modState(_.copy(conf = c))
            }
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
      .initialState_P(p => State(Config.empty))
      .renderBackend[Backend]
      .componentWillMount(_.backend.init)
      .componentWillReceiveProps(ctx => ctx.$.backend.feedAnalytics(ctx.nextProps))
      .build

    def apply(conf: Future[Config], ctl: RouterCtl[View], r: Resolution[View]) =
      component(Props(conf, ctl, r))

  }

}
