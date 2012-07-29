package jskills.ui

import com.vaadin.Application
import com.vaadin.ui._
import com.vaadin.terminal.ExternalResource
import vaadin.scala.Button
import jskills.trueskill.TwoTeamTrueSkillCalculator
import jskills.GameInfo
import jskills.Team
import jskills.{ Player => TSPlayer }
import jskills.Rating

class TrueSkillUI extends Application with LoginLogout {
  lazy val in = new InputPanel
  lazy val out = new OutputPanel
  lazy val calculator = new TwoTeamTrueSkillCalculator
  lazy val gameInfo = GameInfo.defaultGameInfo

  override def init(): Unit = {
    setMainWindow(new Window("TrueSkill 2"))
    getMainWindow.addComponent(in)
    getMainWindow.addComponent(new Button("Compute", _ => compute()))
    getMainWindow.addComponent(out)

    super.init
    if (loggedUser != null)
      getMainWindow.addComponent(new Label("Hello " + loggedUser.getNickname))
    getMainWindow.addComponent(new Link("logout", new ExternalResource(userService.createLogoutURL(getURL.toString))))
  }

  private def compute() {
    val players = in.teams()
    val teamNames = (players map (_.team)) toSet
    val teams = teamNames map { teamName =>
      Team(players filter (_.team == teamName) map (p => (new TSPlayer(p.name), new Rating(p.mu, p.sigma))))
    }
    //TODO UI for team rankings
    val ratings = calculator.calculateNewRatings(gameInfo, teams.toSeq, Seq(1, 2))
    println(ratings)
  }

}