package Controller;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.servlet.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@WebServlet("/sso/post")
public class SSOAcs extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Auth auth = null;
        try {
            auth = new Auth(req, resp);
            auth.processResponse();
        } catch (Exception e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (!auth.isAuthenticated()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        List<String> errors = auth.getErrors();
        if (!errors.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } else {
            HttpSession session = req.getSession();
            Map<String, List<String>> attributes = auth.getAttributes();
            String nameId = auth.getNameId();
            String nameIdFormat = auth.getNameIdFormat();
            String sessionIndex = auth.getSessionIndex();
            String nameidNameQualifier = auth.getNameIdNameQualifier();
            String nameidSPNameQualifier = auth.getNameIdSPNameQualifier();
            session.setAttribute("attributes", attributes);
            session.setAttribute("nameId", nameId);
            session.setAttribute("nameIdFormat", nameIdFormat);
            session.setAttribute("sessionIndex", sessionIndex);
            session.setAttribute("nameidNameQualifier", nameidNameQualifier);
            session.setAttribute("nameidSPNameQualifier", nameidSPNameQualifier);

            String relayState = req.getParameter("RelayState");
            if (relayState != null && !relayState.isEmpty() && !relayState.equals(ServletUtils.getSelfRoutedURLNoQuery(req)) &&
                    !relayState.contains("/sso/login")) { // We don't want to be redirected to login.jsp neither
                resp.sendRedirect(req.getParameter("RelayState"));
            } else {

                if (attributes.isEmpty()) {
		            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                } else {
                    Collection<String> keys = attributes.keySet();
                    for(String name :keys){
                        resp.getWriter().println("<tr><td>" + name + "</td><td>");
                        List<String> values = attributes.get(name);
                        for(String value :values) {
                            resp.getWriter().println("<li>" + value + "</li>");
                        }
                    }
                }
            }
        }
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
