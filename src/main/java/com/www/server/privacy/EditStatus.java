package com.www.server.privacy;

import com.www.server.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "EditStatusPM", urlPatterns = {"/privacy/editstatus"})
public class EditStatus extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String jspName = (String) request.getParameter("jsp_name");
        String fileId = (String) request.getParameter("pm_id");
        String app = "privacy";
        String fileDir = "";
        fileDir = Utils.getDir(app, fileId);
        String xmlUrl = fileDir + "/" + fileId + ".xml";
        String status = (String) request.getParameter("view_status_button");
        if ("START".equals(status)) {
            Utils.setText(xmlUrl, "status", "start");
        } else if ("PAUSE".equals(status)) {
            Utils.setText(xmlUrl, "status", "pause");
        } else {
            Utils.setText(xmlUrl, "status", "stop");
            Utils.updateReady("pms", fileId, "NO");
        }
        if ("edit".equals(jspName)) {
            response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/edit.jsp?id=" + fileId));
        } else {
            response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/view.jsp?id=" + fileId));
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
