package Controller;

import Beans.Spesa;
import Beans.User;
import DAO.SpeseDAO;
import Utilities.ConnectionHandler;
import Utilities.GeneratePDF;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/home/reportGenerator")
public class ReportGeneratorController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        try{
            connection = ConnectionHandler.getConnection(getServletContext());
        } catch (UnavailableException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String reportType;

        reportType = StringEscapeUtils.escapeJava(req.getParameter("type"));

        if(reportType == null || reportType.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameter");
            return;
        }

        SpeseDAO speseDAO = new SpeseDAO(connection);
        ArrayList<Spesa> spese;
        User user = (User) req.getSession().getAttribute("user");
        try {
            if(reportType.equals("storico")){
                spese = speseDAO.getStorico(user.getId());
            } else if(reportType.equals("spese")){
                spese = speseDAO.getSpese(user.getId());
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid argument");
                return;
            }
        }catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
            return;
        }

        resp.setContentType("application/pdf;charset=UTF-8");
        resp.addHeader("Content-Disposition", "inline; filename=" + "report_GestioneSpese.pdf");
        ServletOutputStream out = resp.getOutputStream();




        ByteArrayOutputStream baos = GeneratePDF.getPdfFile(spese);
        baos.writeTo(out);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
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
