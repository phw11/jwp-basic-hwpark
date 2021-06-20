package next.controller.users;

import next.controller.Controller;
import next.controller.UserSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutController implements Controller {

  @Override
  public String execute(HttpServletRequest request, HttpServletResponse response) {

    HttpSession session = request.getSession();
    session.removeAttribute(UserSessionUtils.USER_SESSION_KEY);

    return "redirect:/";
  }
}
