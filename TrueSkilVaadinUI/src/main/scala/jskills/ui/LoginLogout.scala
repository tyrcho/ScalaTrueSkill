package jskills.ui

import java.security.Principal
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
import com.vaadin.Application
import com.vaadin.service.ApplicationContext
import com.vaadin.service.ApplicationContext.TransactionListener
import com.vaadin.terminal.ExternalResource
import com.vaadin.ui._
import javax.servlet.http.HttpServletRequest

trait LoginLogout { self: Application =>
  @transient val requestListeners = collection.mutable.Map.empty[ApplicationContext, TransactionListener]

  def userService = UserServiceFactory.getUserService
  def loggedUser = userService.getCurrentUser

  def init() {
    val context = getContext
    if (context != null && !requestListeners.contains(context)) {
      val listener = new TransactionListener {
        def transactionStart(app: Application, req: Object) {
          if (!userService.isUserLoggedIn)
            getMainWindow.open(new ExternalResource(userService.createLoginURL(
              req.asInstanceOf[HttpServletRequest].getRequestURI)))
        }
        def transactionEnd(app: Application, req: Object) {}
      }
      context.addTransactionListener(listener)
      requestListeners(context) = listener
    }
  }

}