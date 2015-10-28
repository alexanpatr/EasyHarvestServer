package com.www.server.privacy;

import com.www.server.Globals;
import com.www.server.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class Edit extends HttpServlet {

    String app = "privacy";

    /*
     @Override
     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     }
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String TAG = Edit.class.getName() + "@doPost: ";
        HttpSession session = request.getSession(true);
        String userName = (String) session.getAttribute("username");
        String userDir = Globals.db_dir + "/" + userName;
        String date = Utils.getDate();
        String time = Utils.getTime();
        System.out.println(TAG + "User\t\t: " + userName);
        String pmId = "0";
        String pmName = "";
        String pmDesc = "";
        String sensing = "";
        String pmSource = "";

        try {
            /*
             * Get PM details.
             */
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                String fieldName = item.getFieldName();
                if (item.isFormField()) {
                    /* 
                     * Get title and description.
                     */
                    if ("pm_id".equals(fieldName)) {
                        pmId = item.getString();
                        System.out.println(TAG + "Id\t\t: " + pmId);
                    }
                    if ("name".equals(fieldName)) {
                        pmName = item.getString();
                        System.out.println(TAG + "Name\t\t: " + pmName);
                    }
                    if ("description".equals(fieldName)) {
                        pmDesc = item.getString();
                        System.out.println(TAG + "Description\t: " + pmDesc);
                    }
                    if ("sensing_task".equals(fieldName)) {
                        sensing = item.getString();
                        System.out.println(TAG + "Sensing\t: " + sensing);
                    }
                    if ("code".equals(fieldName)) {
                        pmSource = item.getString();
                        System.out.println(TAG + "Source\t\t: " + pmSource);
                    }
                }
            }
        } catch (FileUploadException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        String fileDir = userDir + "/" + app + "/" + pmId;
        String xmlUrl = fileDir + "/" + pmId + ".xml";
        String fileName = Utils.getText(xmlUrl, "source", "name");
        String fileUrl = fileDir + "/" + fileName;
        File file = new File(fileUrl);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(pmSource);
        }
        String fileSize = Long.toString((new File(fileUrl)).length());
        Utils.setText(xmlUrl, "source", "size", fileSize);
        String compLog = Utils.compilePM(fileDir, fileName);
        Utils.setText(xmlUrl, "source", "compile", "date", Utils.getDate());
        Utils.setText(xmlUrl, "source", "compile", "time", Utils.getTime());
        if (Utils.stringIsEmpty(compLog)) {
            compLog += "\n" + Utils.compileDex(fileDir, fileName);
            if (Utils.stringIsEmpty(compLog)) {
                String binSize = Utils.zipDex(fileDir, pmId);
                Utils.setText(xmlUrl, "size", binSize);
                Utils.updateReady("pms", pmId, "YES");
                String v = Utils.getText(xmlUrl, "version");
                Utils.setText(xmlUrl, "version", Integer.toString(Integer.parseInt(v) + 1));
                Utils.setText(xmlUrl, "status", "start");
                Utils.setText(xmlUrl, "date", Utils.getDate());
                Utils.setText(xmlUrl, "time", Utils.getTime());
            }
        }
        Utils.setText(xmlUrl, "source", "compile", "log", compLog);
        if (!Utils.stringIsEmpty(pmName)) {
            Utils.setText(xmlUrl, "name", pmName);
        }
        if (!Utils.stringIsEmpty(pmDesc)) {
            Utils.setText(xmlUrl, "description", pmDesc);
        }
        Utils.setText(xmlUrl, "sensing", sensing);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                    Statement s = c.createStatement()) {
                s.executeUpdate("UPDATE " + "pms" + " SET sensing='" + sensing + "' WHERE id='" + pmId + "'");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(TAG + ex.getMessage());
        }

//            request.getRequestDispatcher("/Server/" + app + "/home.jsp").forward(request, response);
        response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/view.jsp?id=" + pmId));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
