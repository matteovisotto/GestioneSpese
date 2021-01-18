package Controller;

import Beans.User;
import DAO.UserDAO;
import Utilities.ConnectionHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        try{
            connection = ConnectionHandler.getConnection(context);
        } catch (UnavailableException e){
            e.printStackTrace();
        }
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = "login";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        templateEngine.process(path, ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username;
        String password;

        username = StringEscapeUtils.escapeJava(req.getParameter("username"));
        password = StringEscapeUtils.escapeJava(req.getParameter("password"));

        if (username == null || password == null || username.isEmpty() || password.isEmpty() ) {
            //Handle error
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credential");
            return;
        }
        UserDAO usr = new UserDAO(connection);
        User u;
        try {
            u = usr.checkCredentials(username, password);
        }
        catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to perform query: " + e.getLocalizedMessage());
            return;
        }
        catch (NoSuchElementException e){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credential");
            return;
        }

        String path = getServletContext().getContextPath();
        req.getSession().setAttribute("user", u);
        String target = "/home";
        path = path + target;
        resp.sendRedirect(path);
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException ignored) {
        }
    }
}
