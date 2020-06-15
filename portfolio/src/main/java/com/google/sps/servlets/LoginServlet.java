import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.lang.UnsupportedOperationException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logins")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Not implemented yet
      throw new UnsupportedOperationException("doGet is not implemented yet");
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL("/logins");

      response.getWriter().println("<p>Hello " + userEmail + "!</p>");
      response.getWriter().println("<p>You are already logged in.<p>");
      response.getWriter().println("<p>Please Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
      response.sendRedirect("/data");
    } else {
      String loginUrl = userService.createLoginURL("/logins");
      
      response.getWriter().println("<p>Hello!</p>");
      response.getWriter().println("<p>Please Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }
}
