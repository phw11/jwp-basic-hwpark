package core.web.servlet;

import core.mvc.Controller;
import core.mvc.RequestMapping;
import next.controller.HomeController;
import next.controller.qna.AddAnswerController;
import next.controller.qna.RemoveAnswerController;
import next.controller.qna.ShowController;
import next.controller.users.*;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.dao.UserDao;
import next.service.user.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

  private final RequestMapping mapping = new RequestMapping();

  public DispatcherServlet() {

    /*
     * dao
     */
    var userDao = new UserDao();
    var questionDao = new QuestionDao();
    var answerDao = new AnswerDao();

    /*
     * service
     */
    var userService = new UserService(userDao);

    mapping
        // home
        .add("/", new HomeController(questionDao))
        // users
        .add("/users/list", new ListUserController(userService))
        .add("/users/create", new CreateUserController(userService))
        .add("/users/login", new LoginController(userService))
        .add("/users/logout", new LogoutController())
        .add("/users/profile", new ProfileController(userService))
        .add("/users/update", new UpdateUserController(userService))
        // qna
        .add("/qna/show", new ShowController(questionDao, answerDao));

    /*
     * api
     */
    mapping
        // qna
        .add("/api/qna/addAnswer", new AddAnswerController(answerDao))
        .add("/api/qna/removeAnswer", new RemoveAnswerController(answerDao));
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

    String uri = req.getRequestURI();

    var controller = findController(uri);

    try {

      var mav = controller.execute(req, resp);
      var view = mav.getView();

      view.render(mav.getModel(), req, resp);

    } catch (Exception e) {
      throw new ServletException(e.getMessage());
    }
  }

  private Controller findController(String uri) {

    var controller = mapping.get(uri);

    if (isEmpty(controller)) {
      controller = mapping.getForward();
    }

    return controller;
  }
}
