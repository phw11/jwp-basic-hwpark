package core.nmvc;

import com.google.common.collect.Lists;
import core.mvc.Controller;
import core.mvc.LegacyRequestMapping;
import core.mvc.view.ModelAndView;
import core.mvc.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final List<HandlerMapping> mappings = Lists.newArrayList();

  @Override
  public void init() throws ServletException {
    LegacyRequestMapping lhm = new LegacyRequestMapping();
    lhm.initMapping();

    AnnotationHandlerMapping ahm = new AnnotationHandlerMapping("next.controller");
    ahm.initialize();

    mappings.add(lhm);
    mappings.add(ahm);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    Object handler = getHandler(req);

    if (isEmpty(handler)) {
      throw new IllegalArgumentException("Not exist uri.");
    }

    try {
      ModelAndView mav = execute(handler, req, resp);

      View view = mav.getView();

      view.render(mav.getModel(), req, resp);
    } catch (Throwable e) {
      logger.error("Exception : {}", e.getMessage(), e);
      throw new ServletException(e.getMessage());
    }
  }

  private Object getHandler(HttpServletRequest req) {
    for (HandlerMapping handlerMapping : mappings) {
      Object handler = handlerMapping.getHandler(req);

      if (isNotEmpty(handler)) {
        return handler;
      }
    }

    return null;
  }

  private ModelAndView execute(Object handler, HttpServletRequest req, HttpServletResponse reps)
      throws Exception {
    if (handler instanceof Controller) {
      return ((Controller) handler).execute(req, reps);
    }

    return ((HandlerExecution) handler).handle(req, reps);
  }
}