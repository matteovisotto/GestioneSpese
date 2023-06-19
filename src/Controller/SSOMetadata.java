package Controller;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.List;

@WebServlet("/sso/metadata")
public class SSOMetadata extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Auth auth = null;
        try {
            auth = new Auth();
        } catch (SettingsException e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Saml2Settings settings = auth.getSettings();
        settings.setSPValidationOnly(true);
        List<String> errors = settings.checkSettings();
        if (errors.isEmpty()) {
            String metadata = null;
            try {
                metadata = settings.getSPMetadata();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
            resp.getWriter().println(metadata);
        } else {
            resp.setContentType("text/html; charset=UTF-8");
            for (String error : errors) {
                resp.getWriter().println("<p>"+error+"</p>");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }


}
