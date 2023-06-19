package Controller;

import Beans.User;
import DAO.UserDAO;
import Utilities.ConnectionHandler;
import Utilities.RandomString;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import sun.net.www.http.HttpClient;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;

@WebServlet("/sso/login")
public class SSOLoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Auth auth = null;
        try {
            auth = new Auth(req, resp);
            auth.login();
        } catch (SettingsException | Error e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    /*
                        String path = getServletContext().getContextPath();
                        req.getSession().setAttribute("user", user);
                        req.getSession().setAttribute("isSSO", true);
                        String target = "/home";
                        path = path + target;
                        resp.sendRedirect(path);
     */
    }


}
