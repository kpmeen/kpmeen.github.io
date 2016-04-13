/**
 * Copyright(c) 2016 Knut Petter Meen, all rights reserved.
 */
package net.scalytica.blaargh.pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import net.scalytica.blaargh.models.Config
import net.scalytica.blaargh.styles.BlaarghBootstrapCSS
import net.scalytica.blaargh.utils.StringUtils
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalacss.Defaults._
import scalacss.ScalaCssReact._

object AboutPage {

  case class Props(siteConf: Future[Config])

  case class State(conf: Config, content: Option[String])

  object Styles extends StyleSheet.Inline {

    import dsl._

    val profileCard = style("blaargh-profile-card")(
      BlaarghBootstrapCSS.Mixins.card,
      addClassName("text-xs-center"),
      border.`0`,
      boxShadow := "none"
    )

    val centeredAvatar = style("blaarg-profile-avatar")(
      BlaarghBootstrapCSS.Mixins.imgCircle,
      BlaarghBootstrapCSS.Mixins.centerBlock,
      height(120.px)
    )

    val authorSocial = style("blaargh-author-social")(
      fontSize(1.2.em),
      marginBottom(5.px),
      color.black,
      unsafeChild(".fa")(
        marginRight(5.px)
      ),
      lineHeight(47 px),
      transitionProperty := "font-size",
      BlaarghBootstrapCSS.Mixins.easeOutAnimation,
      &.hover(
        fontSize(1.5.em)
      )
    )
  }

  class Backend($: BackendScope[Props, State]) {
    def init: Callback = {
      $.props.map(p =>
        Callback.future[Unit] {
          for {
            config <- p.siteConf
            page <- loadPage
          } yield {
            $.modState(_.copy(conf = config, content = page))
          }
        }.runNow()
      )
    }

    def loadPage: Future[Option[String]] =
      Ajax.get(url = "pages/about.html").map(xhr =>
        xhr.status match {
          case ok: Int if ok == 200 => Some(xhr.responseText)
          case _ => None
        }
      )

    def render(props: Props, state: State) = {
      <.div(BlaarghBootstrapCSS.container,
        <.div(BlaarghBootstrapCSS.row,
          <.div(BlaarghBootstrapCSS.col(8),
            <.div(BlaarghBootstrapCSS.container,
              ^.dangerouslySetInnerHtml(state.content.map(c => c).getOrElse(""))
            )
          ),
          <.div(BlaarghBootstrapCSS.col(4),
            <.div(Styles.profileCard,
              <.img(
                Styles.centeredAvatar,
                ^.src := StringUtils.asOption(state.conf.owner.avatar).getOrElse("assets/images/default_avatar.png")
              ),
              <.div(BlaarghBootstrapCSS.cardBlock,
                <.h4(BlaarghBootstrapCSS.cardTitle, state.conf.owner.name),
                <.p(BlaarghBootstrapCSS.cardText, state.conf.owner.bio),
                <.p(BlaarghBootstrapCSS.cardText,
                  StringUtils.asOption(state.conf.owner.email).map(email =>
                    <.a(
                      Styles.authorSocial,
                      ^.href := s"mailto:$email",
                      <.i(^.className := "fa fa-envelope")
                    )
                  ).getOrElse(EmptyTag),
                  StringUtils.asOption(state.conf.owner.twitter).map(twitter =>
                    <.a(
                      Styles.authorSocial,
                      ^.href := s"http://twitter.com/$twitter",
                      ^.target := "_blank",
                      <.i(^.className := "fa fa-twitter")
                    )
                  ).getOrElse(EmptyTag),
                  StringUtils.asOption(state.conf.owner.github).map(github =>
                    <.a(
                      Styles.authorSocial,
                      ^.href := s"http://github.com/${state.conf.owner.github}",
                      ^.target := "_blank",
                      <.i(^.className := "fa fa-github")
                    )
                  ).getOrElse(EmptyTag)
                )
              )
            )
          )
        )
      )
    }
  }

  val component = ReactComponentB[Props]("About")
    .initialState_P(p => State(Config.empty, None))
    .renderBackend[Backend]
    .componentWillMount(_.backend.init)
    .build

  def apply(conf: Future[Config]) = component(Props(conf))

}
