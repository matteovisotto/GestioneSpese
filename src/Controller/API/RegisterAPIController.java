package Controller.API;

import DAO.UserDAO;
import Utilities.ConnectionHandler;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/api/json/register")
public class RegisterAPIController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().println("Invalid action for method GET and path login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            connection = ConnectionHandler.getConnection(getServletContext());
        } catch (UnavailableException e){
            e.printStackTrace();
        }

        String username;
        String password;
        String password_cnf;
        String name;
        String surname;
        username = StringEscapeUtils.escapeJava(req.getParameter("username"));
        password = StringEscapeUtils.escapeJava(req.getParameter("password"));
        password_cnf = StringEscapeUtils.escapeJava(req.getParameter("password_cnf"));
        name = StringEscapeUtils.escapeJava(req.getParameter("name"));
        surname = StringEscapeUtils.escapeJava(req.getParameter("surname"));

        if (username == null || password == null || password_cnf == null || username.isEmpty() || password.isEmpty() || password_cnf.isEmpty() || name == null || name.isEmpty() || surname == null || surname.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid username");
            return;
        } else if(!password.equals(password_cnf)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Password not match");
            return;
        }


        UserDAO userDao = new UserDAO(connection);

        try {
            if(!userDao.isUsernameFree(username)){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("Selected username is already in use");
                return;
            }

            userDao.addUser(name, surname, username, password);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error, retry later");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println("Account successfully created");
    }

    @Override
    public void destroy() {
        try{
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
