package com.www.server.privacy;

import com.www.server.Globals;
import com.www.server.Utils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Upload extends HttpServlet {

    String app = "privacy",
            userName,
            userDir,
            date,
            time,
            pmName,
            pmDesc = "",
            sensing,
            fileName = "",
            fileDir,
            fileUrl,
            fileSize,
            className,
            compLog = "",
            errorMsg = "",
            binSize = "0";
    FileItem pmFile = null;
    Integer pmId;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(response.encodeRedirectURL("/Server/privacy/upload.jsp"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String TAG = Upload.class.getName() + "@doPost: ";
        HttpSession session = request.getSession(true);
        userName = (String) session.getAttribute("username");
        userDir = Globals.db_dir + "/" + userName;
        date = Utils.getDate();
        time = Utils.getTime();
        System.out.println(TAG + "User\t\t: " + userName);

        try {
            /*
             * Get PM details.
             */
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                String fieldName = item.getFieldName();
                if (item.isFormField()) {
                    /* 
                     * Get the title.
                     */
                    if ("name".equals(fieldName)) {
                        pmName = item.getString();
                        if (!Utils.stringIsEmpty(pmName)) {
                            System.out.println(TAG + "Name\t\t: " + pmName);
                        } else {
                            errorMsg += "Please enter a name<br/>";
                        }
                    }
                    /* 
                     * Get the description.
                     */
                    if ("description".equals(fieldName)) {
                        pmDesc = item.getString();
                        if (!Utils.stringIsEmpty(pmDesc)) {
                            System.out.println(TAG + "Dscription\t: " + pmDesc);
                        } else {
                            errorMsg += "Please enter a description<br/>";
                        }
                    }
                    /* 
                     * Get the sensing task.
                     */
                    if ("sensing_task".equals(fieldName)) {
                        sensing = item.getString();
                        if (!Utils.stringIsEmpty(sensing)) {
                            System.out.println(TAG + "Sensing\t: " + sensing);
                        } else {
                            errorMsg += "Please select a target Sensing Task<br/>";
                        }
                    }
                } else {
                    /* 
                     * Get the code.
                     */
                    fileName = FilenameUtils.getName(item.getName());
//                  class name
                    className = FilenameUtils.removeExtension(fileName);
//                    
                    FileItem fileItem = item;
                    if ("code".equals(fieldName)) {
                        if (fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("java")) {
                            pmFile = fileItem;
                            System.out.println(TAG + "Filename\t\t: " + fileName);
                        } else {
                            errorMsg += "Privacy mechanism must be a .java file<br/>";
                        }
                    }
                }
            }
        } catch (FileUploadException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        if (!Utils.stringIsEmpty(errorMsg)) {
            /* 
             * Something's wrong, re-upload.
             */
            System.out.println(Upload.class.getName() + "@doPost: " + errorMsg);
            request.setAttribute("errorMessage", errorMsg);
            request.getRequestDispatcher("/" + app + "/upload.jsp").forward(request, response);
        } else {
            /* 
             * Everything's OK, save the PM.
             */
            pmId = addNew(fileName, userName, sensing);
            fileDir = userDir + "/" + app + "/" + pmId;
            new File(fileDir).mkdir();
            fileUrl = fileDir + "/" + fileName;
            fileSize = Utils.writeToFile(pmFile, fileUrl);
            compLog = Utils.compilePM(fileDir, fileName);
            if (Utils.stringIsEmpty(compLog)) {
                compLog += "\n" + Utils.compileDex(fileDir, fileName);
                if (Utils.stringIsEmpty(compLog)) {
                    binSize = Utils.zipDex(fileDir, pmId.toString());
                    Utils.updateReady("pms", pmId.toString(), "YES");
                }
            }
            composeXml();
//            request.getRequestDispatcher("/privacy/home.jsp").forward(request, response);
            if (Utils.stringIsEmpty(compLog)) {
                response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/view.jsp?id=" + pmId));
            } else {
                response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/edit.jsp?id=" + pmId));
            }
        }
    }

    /*
     * Description: Inserts a new PM to the table.
     * Parameters : - The file name.
     *              - The user name.
     * Returns    : - The new PM id.
     * ChangeLog  : - .
     */
    private int addNew(String fName, String uName, String sName) {
        int id = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                    Statement s = c.createStatement()) {
                s.executeQuery("SELECT COUNT(*) FROM pms");
                try (ResultSet rs = s.getResultSet()) {
                    rs.next();
                    id = rs.getInt(1) + 1;
                }
                s.executeUpdate("INSERT INTO pms (`id`, `filename`, `username`, `sensing`, `ready`) VALUES ('"
                        + id + "', '" + fName + "', '" + uName + "', '" + sName + "', '" + "NO" + "')");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(Upload.class.getName() + "@addNew: " + ex.getMessage());
        }
        if (Globals.DBG) {
            System.out.println(Upload.class.getName() + "@addNew: " + "Id\t\t: " + id);
        }
        return id;
    }

    /*
     * Description: Composes the XML file.
     * Parameters : - .
     * Returns    : - .
     * ChangeLog  : - .
     */
    private void composeXml() {
        String TAG = Utils.class.getName() + "@composeXml: ";
        try {
            Element root = new Element("pm");
            Document doc = new Document(root);
            doc.setRootElement(root);
            root.addContent(new Element("user").setText(userName));
            root.addContent(new Element("name").setText(pmName));
            root.addContent(new Element("version").setText("0"));
            root.addContent(new Element("status").setText(""));
            root.addContent(new Element("id").setText(pmId.toString()));
            root.addContent(new Element("size").setText(binSize));
            root.addContent(new Element("date").setText(date));
            root.addContent(new Element("time").setText(time));
            root.addContent(new Element("description").setText(pmDesc));
            root.addContent(new Element("sensing").setText(sensing));

            Element file = new Element("source");
            root.addContent(file);
            file.addContent(new Element("name").setText(fileName));
            file.addContent(new Element("size").setText(fileSize));
//          class name  
            file.addContent(new Element("class").setText(className));
//            
            Element compile = new Element("compile");
            file.addContent(compile);
            if (Utils.stringIsEmpty(compLog)) {
                root.getChild("version").setText("1");
                root.getChild("status").setText("start");
            }
            compile.addContent(new Element("date").setText(date));
            compile.addContent(new Element("time").setText(time));
            compile.addContent(new Element("log").setText(compLog));

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(doc, new FileWriter(fileDir + "/" + pmId + ".xml"));
        } catch (IOException ex) {
            System.out.println(TAG + ex.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Upload a new privacy mechanism";
    }

}
