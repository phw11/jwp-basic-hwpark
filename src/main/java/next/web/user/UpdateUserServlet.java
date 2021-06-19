package next.web.user;

import core.db.DataBase;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@WebServlet("/user/update")
public class UpdateUserServlet extends HttpServlet {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (isEmpty(req.getSession().getAttribute("user"))) {
      resp.sendRedirect("/user/login.html");

      return;
    }

    req.setAttribute("user", DataBase.findUserById(req.getParameter("userId")));

    var rd = req.getRequestDispatcher("/user/update.jsp");
    rd.forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    User user =
        new User(
            req.getParameter("userId"),
            req.getParameter("password"),
            req.getParameter("name"),
            req.getParameter("email"));

    logger.debug("user : {}", user);

    DataBase.addUser(user);

    resp.sendRedirect("/user/list");
  }
}
