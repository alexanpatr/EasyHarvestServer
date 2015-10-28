package com.www.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Upload extends HttpServlet {

    private String CONSOLE_CMD, DB_DIR, SERVER_URL, LIB_URL, JAVAC_CMD, DX_CMD, DB_SERVER, DB_USERNAME, DB_PASSWORD;
    private String userName;
    private String errorMessage = "";
    private boolean hasFileClient = false;
    private boolean isCompiledClient = false;
    private boolean hasFileServer = false;
    private boolean hasLibServer = false;
    private boolean isCompiledServer = false;
    Boolean fileCompiled = false;
    private String fileId, fileComment, fileDate, fileTime, userDir, fileDir,
            dirClient, fileUrlClient, fileNameClient, fileSizeClient, fileSizeZipClient, fileCompileClient, fileTimeClient, fileDateClient,
            dirServer, fileUrlServer, fileNameServer, fileSizeServer, fileCompileServer, fileTimeServer, fileDateServer,
            zipFileNameServer, libUrlServer, libNameServer, libSizeServer;
    private FileItem fileContentClient, fileContentServer, libContentServer;
    private String timeCheckbox, timeFrom, timeTo;
    private String mapCheckbox, sw_lat, sw_lng, ne_lat, ne_lng;
    private String libCheckboxServer;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileNotFoundException {
        HttpSession session = request.getSession(true);
        userName = (String) session.getAttribute("username");
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                String fieldName = item.getFieldName();
                if (item.isFormField()) {
                    String fieldValue = item.getString();
                    if ("upload_comment_value".equals(fieldName)) {
                        fileComment = "(no comment)";
                        if (!Utils.stringIsEmpty(fieldValue)) {
                            fileComment = item.getString();
                        }
                    }
                    if ("upload_time_checkbox".equals(fieldName)) {
                        timeCheckbox = fieldValue;
                    }
                    if ("on".equals(timeCheckbox)) {
                        if ("upload_time_selection_from".equals(fieldName)) {
                            timeFrom = fieldValue;
                        }
                        if ("upload_time_selection_to".equals(fieldName)) {
                            timeTo = fieldValue;
                        }
                    }
                    if ("upload_map_checkbox".equals(fieldName)) {
                        mapCheckbox = fieldValue;
                    }
                    if ("on".equals(mapCheckbox)) {
                        if ("upload_map_sw_lat".equals(fieldName)) {
                            sw_lat = fieldValue;
                        }
                        if ("upload_map_sw_lng".equals(fieldName)) {
                            sw_lng = fieldValue;
                        }
                        if ("upload_map_ne_lat".equals(fieldName)) {
                            ne_lat = fieldValue;
                        }
                        if ("upload_map_ne_lng".equals(fieldName)) {
                            ne_lng = fieldValue;
                        }
                    }
                    //if ("upload_lib_server_checkbox".equals(fieldName)) {
                    //libCheckboxServer = fieldValue;
                    //}
                } else {
                    String fileName = FilenameUtils.getName(item.getName());
                    FileItem file = item;
                    if ("upload_code_client_value".equals(fieldName)) {
                        if (Utils.stringIsEmpty(fileName)) {
                            errorMessage += "No client part file chosen<br/>";
                        } else {
                            if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("java")) {
                                hasFileClient = true;
                                fileNameClient = fileName;
                                fileContentClient = file;
                            } else {
                                errorMessage += "Client part must be a .java file<br/>";
                            }
                        }
                    } else if ("upload_code_server_value".equals(fieldName) && !Utils.stringIsEmpty(fileName)) {
                        if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("zip")) {
                            hasFileServer = true;
                            zipFileNameServer = fileName;
                            fileContentServer = file;
                        } else {
                            errorMessage += "Server part must be a .zip file<br/>";
                        }
                    }
                    //if ("upload_lib_server_value".equals(fieldName) && fileServer && "on".equals(libCheckboxServer)) {
                    //if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("jar")) {
                    //libServer = true;
                    //libNameServer = fileName;
                    //libContentServer = file;
                    //} else {
                    //errorMessage += "Server library must be a .jar file<br/>";
                    //}
                    //}
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }/**/

        if (!Utils.stringIsEmpty(errorMessage)) {
            request.setAttribute("errorMessage", errorMessage);
            errorMessage = "";
            destroyFile();
            request.getRequestDispatcher("main/upload.jsp").forward(request, response);
        } else {
            try {
                fileId = String.valueOf(getNewTaskId());
                insertTask(fileId, fileNameClient, userName);
                initFile();

                fileSizeClient = Utils.writeToFile(fileContentClient, fileUrlClient);
                fileCompileClient = Utils.compileAndroidFile(dirClient, fileNameClient);
                fileCompileClient += "\n" + Utils.compileDex(dirClient, fileNameClient);
                fileDateClient = Utils.getDate();
                fileTimeClient = Utils.getTime();

                if (hasFileServer) {
                    Utils.writeToFile(fileContentServer, dirServer + "/" + zipFileNameServer);
                    fileDateServer = Utils.getDate();
                    fileTimeServer = Utils.getTime();
                    //if (libServer) {
                    //libSizeServer = writeToFile(libContentServer, libUrlServer);
                    //fileCompileServer = compileFile(dirServer, fileNameServer, libNameServer);
                    //} else {
                    Utils.unZip(dirServer, zipFileNameServer);
                    fileNameServer = Utils.returnFileFrom(dirServer, ".java").getName();
                    if (Utils.returnFileFrom(dirServer, ".jar") != null) {
                        hasLibServer = true;
                    }
                    fileCompileServer = Utils.compileFile(dirServer, fileNameServer);
                }

                composeXml();

                if (isCompiledClient) {
                    fileSizeZipClient = Utils.zipDex(dirClient, fileId);
                    Utils.setText(fileDir + "/" + fileId + ".xml", "client", "zip_size", fileSizeZipClient);
                }
                if (fileCompiled) {
                    Utils.updateReady("tasks", fileId, "YES");
                }

            } catch (ClassNotFoundException | SQLException | IOException | InterruptedException | JDOMException ex) {
                Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
            }
            systemOut();
            destroyFile();
            response.sendRedirect(response.encodeRedirectURL("/Server/main/view.jsp?id=" + fileId));
        }/**/

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        response.sendRedirect(response.encodeRedirectURL("/Server/main/upload.jsp"));
    }

    private void systemOut() {
        System.out.println(
                "\n/********** Upload **********/\n"
                + "\nUser: " + userName
                + "\nComment: " + fileComment
                + "\nId: " + fileId
                + "\nDate: " + fileDate
                + "\nTime: " + fileTime
        );
        if (hasFileClient) {
            System.out.println(
                    "\nClient: " + fileNameClient
                    + "\n Size: " + fileSizeClient
                    + "\n Time: " + timeCheckbox
            );
            if ("on".equals(timeCheckbox)) {
                System.out.println(
                        "  From: " + timeFrom
                        + "\n  To: " + timeTo
                );
            }
            System.out.println(" Location: " + mapCheckbox);
            if ("on".equals(mapCheckbox)) {
                System.out.println(
                        "  sw_lat: " + sw_lat
                        + "\n  sw_lng: " + sw_lng
                        + "\n  ne_lat: " + ne_lat
                        + "\n  ne_lng: " + ne_lng
                );
            }
        }
        if (hasFileServer) {
            System.out.println(
                    "\nServer: " + fileNameServer
                    + "\n Size: " + fileSizeServer
            );
            if (hasLibServer) {
                System.out.println(" Library: " + libNameServer);
            }
        }
        System.out.println("\n/****************************/\n");
    }

    private void insertTask(String fileId, String fileName, String userName) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(DB_SERVER, DB_USERNAME, DB_PASSWORD);
        Statement s = c.createStatement();
        String sql = "INSERT INTO tasks (`id`, `filename`, `username`, `ready`, `downloaded`) VALUES ('"
                + fileId + "', '" + fileName + "', '" + userName + "', '" + "NO" + "', '" + "0" + "')";
        s.executeUpdate(sql);
        s.close();
        c.close();
    }

    private int getNewTaskId() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(DB_SERVER, DB_USERNAME, DB_PASSWORD);
        Statement s;
        s = c.createStatement();
        s.executeQuery("SELECT COUNT(*) FROM tasks");
        ResultSet rs = s.getResultSet();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        s.close();
        c.close();
        return ++id;
    }

    private void initFile() throws ClassNotFoundException, SQLException {
        fileDate = Utils.getDate();
        fileTime = Utils.getTime();
        userDir = DB_DIR + "/" + userName;
        fileDir = userDir + "/sensing/" + fileId;
        new File(fileDir).mkdir();
        dirClient = fileDir + "/client";
        fileUrlClient = dirClient + "/" + fileNameClient;
        new File(dirClient).mkdir();
        if (hasFileServer) {
            dirServer = fileDir + "/server";
            new File(dirServer).mkdir();
            //fileUrlServer = dirServer + "/" + fileNameServer;
            //if (libServer) {
            //libUrlServer = dirServer + "/" + libNameServer;
            //}
        }
    }

    private void destroyFile() {
        hasFileServer = false;
        fileNameServer = "";
        fileContentServer = null;

        hasLibServer = false;
        libNameServer = "";
        libContentServer = null;

        isCompiledServer = false;

        hasFileClient = false;
        fileNameClient = "";
        fileContentClient = null;
        isCompiledClient = false;

        fileCompiled = false;
    }

    private void composeXml() {
        try {
            Element root = new Element("file");
            Document doc = new Document(root);
            doc.setRootElement(root);
            root.addContent(new Element("username").setText(userName));
            root.addContent(new Element("name").setText(fileNameClient));
            root.addContent(new Element("id").setText(fileId));
            root.addContent(new Element("comment").setText(fileComment));
            root.addContent(new Element("date").setText(fileDate));
            root.addContent(new Element("time").setText(fileTime));
            root.addContent(new Element("status").setText(""));
            root.addContent(new Element("new").setText("no"));
            root.addContent(new Element("counter").setText("0"));
            
            Element client = new Element("client");
            root.addContent(client);
            client.addContent(new Element("name").setText(fileNameClient));
            client.addContent(new Element("size").setText(fileSizeClient));
            client.addContent(new Element("zip_size").setText("0"));
            
            Element downloaded = new Element("downloaded");
            client.addContent(downloaded);
            downloaded.addContent(new Element("counter").setText("0"));
            downloaded.addContent(new Element("devices").setText(""));
            
            Element time = new Element("time");
            client.addContent(time);
            time.addContent(new Element("status").setText(timeCheckbox));
            if ("on".equals(timeCheckbox)) {
                time.addContent(new Element("from").setText(timeFrom));
                time.addContent(new Element("to").setText(timeTo));
            }
            
            Element map = new Element("map");
            client.addContent(map);
            map.addContent(new Element("status").setText(mapCheckbox));
            if ("on".equals(mapCheckbox)) {
                map.addContent(new Element("sw_lat").setText(sw_lat));
                map.addContent(new Element("sw_lng").setText(sw_lng));
                map.addContent(new Element("ne_lat").setText(ne_lat));
                map.addContent(new Element("ne_lng").setText(ne_lng));
            }
            Element compileClient = new Element("compile");
            client.addContent(compileClient);
            if (Utils.stringIsEmpty(fileCompileClient)) {
                compileClient.addContent(new Element("status").setText("true"));
                isCompiledClient = true;
                fileCompiled = true;
            } else {
                compileClient.addContent(new Element("status").setText("false"));
                isCompiledClient = false;
                fileCompiled = false;
            }
            compileClient.addContent(new Element("date").setText(fileDateClient));
            compileClient.addContent(new Element("time").setText(fileTimeClient));
            compileClient.addContent(new Element("output").setText(fileCompileClient));
            client.addContent(new Element("log"));
            
            if (hasFileServer) {
                Element server = new Element("server");
                root.addContent(server);
                server.addContent(new Element("name").setText(fileNameServer));
                server.addContent(new Element("size").setText(fileSizeServer));
                if (hasLibServer) {
                    Element libs = new Element("libraries");
                    server.addContent(libs);
                    
                    File folder = new File(dirServer);
                    String fileName;
                    File file = null;
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            fileName = listOfFiles[i].getName();
                            if (fileName.endsWith(".jar")) {
                                file = new File(dirServer + "/" + fileName);
                                Element lib = new Element("library");
                                libs.addContent(lib);
                                lib.addContent(new Element("name").setText(file.getName()));
                                lib.addContent(new Element("size").setText(Long.toString(file.length())));
                            }
                        }
                    }
                    
                }
                Element compileServer = new Element("compile");
                server.addContent(compileServer);
                if (Utils.stringIsEmpty(fileCompileServer)) {
                    compileServer.addContent(new Element("status").setText("true"));
                    isCompiledServer = true;
                } else {
                    compileServer.addContent(new Element("status").setText("false"));
                    isCompiledServer = false;
                    fileCompiled = false;
                }
                compileServer.addContent(new Element("date").setText(fileDateServer));
                compileServer.addContent(new Element("time").setText(fileTimeServer));
                compileServer.addContent(new Element("output").setText(fileCompileServer));
            }
            
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(doc, new FileWriter(fileDir + "/" + fileId + ".xml"));
        } catch (IOException ex) {
            Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
