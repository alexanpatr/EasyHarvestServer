package com.www.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jdom.JDOMException;

public class EditProp extends HttpServlet {

    private String CONSOLE_CMD, DB_DIR, LIB_URL, JAVAC_CMD, DX_CMD, SERVER_URL, DB_SERVER, DB_USERNAME, DB_PASSWORD;
    private boolean isMultipart;
    String userName, jspName, fileId;
    String mapCheckBox, sw_lat, sw_lng, ne_lat, ne_lng;
    String timeCheckBox, timeFrom, timeTo;

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
        fileId = (String) request.getParameter("file_id");
        String fileDir = "";
        String app = "privacy";
        fileDir = Utils.getDir(app, fileId);
        String xmlUrl = fileDir + "/" + fileId + ".xml";
        if ("on".equals(Utils.getText(xmlUrl, "client", "map", "status"))) {
            Utils.setText(xmlUrl, "client", "map", "sw_lat", (String) request.getParameter("view_map_sw_lat"));
            Utils.setText(xmlUrl, "client", "map", "sw_lng", (String) request.getParameter("view_map_sw_lng"));
            Utils.setText(xmlUrl, "client", "map", "ne_lat", (String) request.getParameter("view_map_ne_lat"));
            Utils.setText(xmlUrl, "client", "map", "ne_lng", (String) request.getParameter("view_map_ne_lng"));
        }
        if ("on".equals(Utils.getText(xmlUrl, "client", "time", "status"))) {
            Utils.setText(xmlUrl, "client", "time", "from", (String) request.getParameter("view_time_selection_from"));
            Utils.setText(xmlUrl, "client", "time", "to", (String) request.getParameter("view_time_selection_to"));
        }
        
        response.sendRedirect(response.encodeRedirectURL("./main/view.jsp?id=" + fileId));
    }
}
