package com.www.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
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
import org.jdom.JDOMException;

public class Edit extends HttpServlet {

    private String CONSOLE_CMD, DB_DIR, SERVER_URL, LIB_URL, JAVAC_CMD, DX_CMD, DB_SERVER, DB_USERNAME, DB_PASSWORD;
    private Boolean isCompiled = true;
    private Boolean isCompiledClient = false;
    private Boolean hasServer = false;
    private Boolean isCompiledServer = false;
    private String userName, userDir,
            fileId, fileDir, xmlUrl,
            fileNameClient, fileCodeClient, fileCompileClient, dirClient, fileUrlClient, fileTimeClient, fileDateClient,
            fileNameServer, fileCodeServer, fileCompileServer, dirServer, fileUrlServer, fileTimeServer, fileDateServer;

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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        HttpSession session = request.getSession(true);
        userName = (String) session.getAttribute("username");
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                String fieldName = item.getFieldName();
                if (item.isFormField()) {
                    String fieldValue = item.getString();
                    if ("file_id".equals(fieldName)) {
                        fileId = fieldValue;
                    }
                    if ("client_name".equals(fieldName)) {
                        fileNameClient = fieldValue;
                    }
                    if ("server_name".equals(fieldName)) {
                        if (Utils.stringIsEmpty(fieldValue)) {
                            hasServer = false;
                        } else {
                            fileNameServer = fieldValue;
                            hasServer = true;
                        }
                    }
                    if ("client_code".equals(fieldName)) {
                        fileCodeClient = fieldValue;
                    }
                    if ("server_code".equals(fieldName)) {
                        fileCodeServer = fieldValue;
                    }
                    if ("client_compiled".equals(fieldName)) {
                        if ("true".equals(fieldValue)) {
                            isCompiledClient = true;
                        } else {
                            isCompiledClient = false;
                        }
                    }
                    if ("server_compiled".equals(fieldName)) {
                        if ("true".equals(fieldValue)) {
                            isCompiledServer = true;
                        } else {
                            isCompiledServer = false;
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
        System.out.println("userName: " + userName
                + "\nfileId: " + fileId
                + "\nclient_name: " + fileNameClient
                + "\nisCompiledClient: " + isCompiledClient
                + "\nserver_name: " + fileNameServer
                + "\nisCompiledServer: " + isCompiledServer);
        try {
            initFile();
            if (!isCompiledClient) {
                File file = new File(fileUrlClient);
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(fileCodeClient);
                bw.close();
                fileCompileClient = Utils.compileAndroidFile(dirClient, fileNameClient);
                fileCompileClient += "\n" + Utils.compileDex(dirClient, fileNameClient);
                fileDateClient = Utils.getDate();
                fileTimeClient = Utils.getTime();
                if (Utils.stringIsEmpty(fileCompileClient)) {
                    isCompiledClient = true;
                    isCompiled = true;
                    Utils.setText(xmlUrl, "client", "compile", "status", "true");
                    Utils.setText(fileDir + "/" + fileId + ".xml", "client", "zip_size", Utils.zipDex(dirClient, fileId));
                } else {
                    isCompiledClient = false;
                    isCompiled = false;
                }
                Utils.setText(xmlUrl, "client", "compile", "date", fileDateClient);
                Utils.setText(xmlUrl, "client", "compile", "time", fileTimeClient);
                Utils.setText(xmlUrl, "client", "compile", "output", fileCompileClient);
                
            }
            if (hasServer) {
                if (!isCompiledServer) {
                    File file = new File(fileUrlServer);
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(fileCodeServer);
                    bw.close();
                    fileCompileServer = Utils.compileFile(dirServer, fileNameServer);
                    fileDateServer = Utils.getDate();
                    fileTimeServer = Utils.getTime();
                    if (Utils.stringIsEmpty(fileCompileServer)) {
                        isCompiledServer = true;
                        Utils.setText(xmlUrl, "server", "compile", "status", "true");
                    } else {
                        isCompiledServer = false;
                        isCompiled = false;
                    }
                    Utils.setText(xmlUrl, "server", "compile", "date", fileDateServer);
                    Utils.setText(xmlUrl, "server", "compile", "time", fileTimeServer);
                    Utils.setText(xmlUrl, "server", "compile", "output", fileCompileServer);
                }
            }
            if (isCompiled) {
                Utils.updateReady("tasks", fileId, "YES");
            }
            destroyFile();
            response.sendRedirect(response.encodeRedirectURL(SERVER_URL + "/main/view.jsp?id=" + fileId));
        } catch (InterruptedException | JDOMException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initFile() throws ClassNotFoundException, SQLException {
        userDir = DB_DIR + "/" + userName;
        fileDir = userDir + "/sensing/" + fileId;
        xmlUrl = fileDir + "/" + fileId + ".xml";
        dirClient = fileDir + "/client";
        fileUrlClient = dirClient + "/" + fileNameClient;
        dirServer = fileDir + "/server";
        fileUrlServer = dirServer + "/" + fileNameServer;
    }

    private void destroyFile() {
        isCompiled = true;
        hasServer = false;
        fileNameServer = "";
        fileCodeServer = "";
        isCompiledServer = false;
        fileNameServer = "";
        fileCodeServer = "";
        isCompiledClient = false;
    }
}
