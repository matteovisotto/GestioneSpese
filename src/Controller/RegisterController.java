package Controller;

import DAO.UserDAO;

import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import Utilities.ConnectionHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    @Override
    public void init() throws ServletException {
        try{
            connection = ConnectionHandler.getConnection(getServletContext());
        } catch (UnavailableException e) {
            throw new UnavailableException("Can't load database driver");
        }

        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = "register";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        templateEngine.process(path, webContext, resp.getWriter());
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = StringEscapeUtils.escapeJava(req.getParameter("name"));
        String surname = StringEscapeUtils.escapeJava(req.getParameter("surname"));
        String username = StringEscapeUtils.escapeJava(req.getParameter("username"));
        String password = StringEscapeUtils.escapeJava(req.getParameter("password"));
        String checkPassword = StringEscapeUtils.escapeJava(req.getParameter("password_cnf"));

        if(name == null || name.isEmpty() || surname == null || surname.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty() || checkPassword == null || checkPassword.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid params");
        }

        if(!password.equals(checkPassword)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password do not match");
            return;
        }
        UserDAO userDAO = new UserDAO(connection);
        try {
            if(!userDAO.isUsernameFree(username)){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unavailable username");
                return;
            }
            userDAO.addUser(name, surname,username, password);
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to save user");
            return;
        }
        resp.sendRedirect(getServletContext().getContextPath()+"/login");
    }
    @Override
    public void destroy() {
        super.destroy();
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
