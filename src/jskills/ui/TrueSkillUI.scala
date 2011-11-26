package jskills.ui

import com.vaadin.Application
import com.vaadin.ui._
import com.vaadin.terminal.ExternalResource

class TrueSkillUI extends Application with LoginLogout {

  override def init(): Unit = {
    setMainWindow(new Window("TrueSkill 2"))
    getMainWindow.addComponent(new Label("v2"))
    super.init
    if (loggedUser != null)
      getMainWindow.addComponent(new Label("Hello " + loggedUser.getNickname))
    getMainWindow.addComponent(new Link("logout", new ExternalResource(userService.createLogoutURL(getURL.toString))))
  }
}