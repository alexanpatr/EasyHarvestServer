package com.www.server;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EditStatus extends HttpServlet {

    private String CONSOLE_CMD, DB_DIR, LIB_URL, JAVAC_CMD, DX_CMD, SERVER_URL, DB_SERVER, DB_USERNAME, DB_PASSWORD;
    private boolean isMultipart;
    String userName, jspName, fileId;

    @Override
    public void init() {
        SERVER_URL = Globals.server_url;
        DB_DIR = Globals.db_dir;
        CONSOLE_CMD = Globals.console_cmd;
        LIB_URL = Globals.lib_url;
        JAVAC_CMD = Globals.javac_cmd;
        DX_CMD = Globals.dx_cmd;
        DB_USERNAME = Globals.db_username;
        DB_PASSWORD = Globals.db_password;
        DB_SERVER = Globals.db_server;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        userName = (String) session.getAttribute("username");
        jspName = (String) request.getParameter("jsp_name");
        fileId = (String) request.getParameter("view_file_id");
        String fileDir = "";
        fileDir = Utils.getDir("sensing", fileId);
        String xmlUrl = fileDir + "/" + fileId + ".xml";
        String status = (String) request.getParameter("view_status_button");
        if ("START".equals(status)) {
            Utils.setText(xmlUrl, "status", "start");
        } else if ("PAUSE".equals(status)) {
            Utils.setText(xmlUrl, "status", "pause");
        } else {
            Utils.setText(xmlUrl, "status", "stop");
            Utils.updateReady("tasks", fileId, "NO");
        }
        if ("edit".equals(jspName)) {
            response.sendRedirect(response.encodeRedirectURL("./main/edit.jsp?id=" + fileId));
        } else {
            response.sendRedirect(response.encodeRedirectURL("./main/view.jsp?id=" + fileId));
        }
    }
}