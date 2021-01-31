package Controller;

import Beans.User;
import DAO.UserDAO;
import Utilities.ConnectionHandler;
import Utilities.RandomString;

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

@WebServlet("/sso/login")
public class SSOLoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private String serviceId;

    @Override
    public void init() throws ServletException {
        serviceId = getServletContext().getInitParameter("ssoServiceId");
        try{
            connection = ConnectionHandler.getConnection(getServletContext());
        } catch (UnavailableException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serviceToken = new RandomString(24).nextString();
        req.getSession().setAttribute("serviceToken", serviceToken);
        String aunicaUrl = "https://aunica.matmacsystem.it/index.php?serviceId="+serviceId+"&serviceToken="+serviceToken;
        resp.sendRedirect(aunicaUrl);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serviceToken = (String) req.getSession().getAttribute("serviceToken");
        String reqToken = req.getParameter("serviceToken");
        if(!verifyParam(reqToken)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to find secure token");
            return;
        } else {
            if(reqToken.equals(serviceToken)){
                req.getSession().removeAttribute("serviceToken");
                String ssoUserId = req.getParameter("userId");
                UserDAO userDao = new UserDAO(connection);
                User user;
                try {
                    user = userDao.ssoLogin(ssoUserId);
                } catch (SQLException e){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to perform query");
                    return;
                } catch (NoSuchElementException e){
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user");
                    return;
                }
                String path = getServletContext().getContextPath();
                req.getSession().setAttribute("user", user);
                req.getSession().setAttribute("isSSO", true);
                String target = "/home";
                path = path + target;
                resp.sendRedirect(path);
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request secure token");
                return;
            }
        }
    }

    private boolean verifyParam(String param){
        boolean result = param==null || param.isEmpty();
        return !result;
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
