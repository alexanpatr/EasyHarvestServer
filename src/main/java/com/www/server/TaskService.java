package com.www.server;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.*;
import java.util.zip.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.*;
import org.apache.commons.io.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

@Path("tasks")
public class TaskService {

    String app = "sensing";

    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "TaskService is ready.";
    }

    @GET
    @Path("test/{param}")
    @Produces(MediaType.TEXT_PLAIN)
    public String test(@PathParam("param") String id) {        
        return id;
    }

    @GET
    @Path("gettaskinfo_test/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getTaskInfo_test(@PathParam("deviceID") String deviceID) throws Exception {
        String taskID = getReadyTask();
        String taskDir = "";
        String xmlUrl = "";
        Task task = null;
        if (!taskID.isEmpty()) {
            System.out.println("TaskService/getTaskInfo: Returning task info with id " + taskID + " ...");
            taskDir = Utils.getDir(app, taskID);
            updateDownloaded(taskID);
            xmlUrl = taskDir + "/" + taskID + ".xml";
            if (Utils.getText(xmlUrl, "status").isEmpty()) {
                Utils.setText(xmlUrl, "status", "start");
            }
            task = new Task(taskDir + "/" + taskID + ".xml");
        }
        Utils.updateDeviceActivity(deviceID);
        return task;
    }

    @GET
    @Path("gettaskinfo/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getTaskInfo(@PathParam("deviceID") String deviceID) throws Exception {
        String taskID = getReadyTask();
        String taskDir = "";
        String xmlUrl = "";
        Task task = null;
        if (!taskID.isEmpty()) {
            System.out.println("TaskService/getTaskInfo: Returning task info with id " + taskID + " ...");
            taskDir = Utils.getDir(app, taskID);
            updateDownloaded(taskID);
            xmlUrl = taskDir + "/" + taskID + ".xml";
            if (Utils.getText(xmlUrl, "status").isEmpty()) {
                Utils.setText(xmlUrl, "status", "start");
            }
            task = new Task(taskDir + "/" + taskID + ".xml");
        }
        Utils.updateDeviceActivity(deviceID);
        return task;
    }

    @GET
    @Path("{taskID}/getbin/{deviceID}")
    public Response getBin(
            @PathParam("taskID") String taskID,
            @PathParam("deviceID") String deviceID) throws Exception {
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        ResponseBuilder response = Response.ok(null);
        if (clientDir != null && !clientDir.isEmpty()) {
            File bin = Utils.returnFileFrom(clientDir, ".zip");
            System.out.println("TaskService/getBin: Returning " + bin.getPath() + " ...");
            response = Response.ok((Object) bin);
        }
        Utils.updateDeviceActivity(deviceID);
        return response.build();
    }

    @GET
    @Path("{taskID}/getbin/{length}/{deviceID}")
    public Response getBin(
            @PathParam("taskID") String taskID,
            @PathParam("length") final Long length,
            @PathParam("deviceID") String deviceID) throws Exception {
        String result = "Oops!";
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        final String binUrl = clientDir + "/" + taskID + ".zip";
        Utils.updateDeviceActivity(deviceID);
        if (clientDir != null && !clientDir.isEmpty() && new File(binUrl).length() > length) {
            if (Utils.deviceExists(deviceID)) {
                StreamingOutput stream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream os) throws IOException, WebApplicationException {
                        Utils.returnPart(new RandomAccessFile(new File(binUrl), "r"), os, length);
                    }
                };
                System.out.println("TaskService/getBin: Returning part of " + binUrl + " from " + length + " to " + deviceID + "...");
                return Response.ok("OK").entity(stream).build();
            } else {
                return Response.ok("Device not found").build();
            }
        } else {
            return Response.ok(result).build();
        }
    }

    @POST
    @Path("{taskID}/putdata/{dataName}/{deviceID}")
    public Response putData(
            InputStream is,
            @PathParam("dataName") String dataName,
            @PathParam("taskID") String taskID,
            @PathParam("deviceID") String deviceID) throws Exception {
        String url = Utils.getDir(app, taskID) + "/client/" + dataName + "@" + deviceID + ".dat";
        System.out.println("TaskService/putData: Writing to " + url);
//
//        Utils.writeToFile(data, url);
//        
//        ObjectInputStream ois = new ObjectInputStream(is);
//        List<Object> data = (List) ois.readObject();
//        saveDataToFile(data, url);
//        
        Utils.writeToFile(is, url);

        Utils.updateDeviceActivity(deviceID);
        return Response.ok().entity("OK").build();
    }

    @GET
    @Path("{taskID}/checkdata/{dataName}_{dataSize}/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkData(
            @PathParam("taskID") String taskID,
            @PathParam("dataName") String dataName,
            @PathParam("dataSize") long dataSize,
            @PathParam("deviceID") String deviceID) throws JSONException {
        String TAG = getClass().getName() + "@checkData: ";

        JSONObject response = new JSONObject();

        response.append("response", "oops");

        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String serverDir = taskDir + "/server";
        String dataUrl = clientDir + "/" + taskID + ".dat";
        String newDataUrl = clientDir + "/" + dataName + "@" + deviceID + ".dat";

        System.out.println(TAG + "Checking data " + newDataUrl + " | " + new File(newDataUrl).length() + "/" + dataSize);

        if (Utils.seekFile(newDataUrl)) {
            String compareResult = Utils.compareFiles(newDataUrl, dataSize);
            if ("equal".equals(compareResult)) {
                File file = Utils.returnFileFrom(taskDir, ".xml");
                String fileName = FilenameUtils.removeExtension(file.getName());
                String xmlUrl = taskDir + "/" + fileName + ".xml";
                Utils.setText(xmlUrl, "new", "yes");
                Utils.setText(xmlUrl, "counter", String.valueOf(Integer.valueOf(Utils.getText(xmlUrl, "counter")) + 1));
                addLog(xmlUrl, dataName, deviceID);
//
//                mergeData(dataUrl, newDataUrl);
//                

                try {
                    FileInputStream fis = new FileInputStream(newDataUrl);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    List<Object> newData = (List) ois.readObject();
                    saveDataToFile(newData, dataUrl);

                    close(ois);
                    close(fis);
                    System.gc();

                    File newDataFile = new File(newDataUrl);
                    if (newDataFile.delete()) {
                        System.out.println(TAG + "Deleted " + newDataUrl);
                    } else {
                        System.out.println(TAG + "Error deleting " + newDataUrl);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println(TAG + ex.getMessage());
                }

                /*if (Utils.getText(xmlUrl, "server") != null) {
                 FileInputStream fis;
                 ObjectInputStream in;
                 fis = new FileInputStream(newDataUrl);
                 in = new ObjectInputStream(fis);
                 Object object = (Object) in.readObject();
                 String serverFileName = Utils.getText(xmlUrl, "server", "name");
                 String serverClassName = serverFileName.substring(0, serverFileName.indexOf("."));
                 loadClass(serverDir, serverClassName, object);
                 }/**/
                response.put("response", "OK");
            } else if ("larger".equals(compareResult)) {
                response.put("response", "put");
                response.append("checked", String.valueOf(Utils.getFileSize(newDataUrl)));
            } else {
                new File(newDataUrl).delete();
            }
        }
        Utils.updateDeviceActivity(deviceID);

        System.out.println(TAG + response.toString());

        return Response.ok().entity(response.toString()).build();
    }

    @GET
    @Path("{taskID}/getprop/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getProp(
            @PathParam("taskID") String taskID,
            @PathParam("deviceID") String deviceID) throws Exception {
        String taskDir = Utils.getDir(app, taskID);
        String xmlUrl = taskDir + "/" + taskID + ".xml";
        Task task = null;
        //System.out.println("TaskService/getProp: Returning task prop with id " + taskID + " ...");
        taskDir = Utils.getDir(app, taskID);
        xmlUrl = taskDir + "/" + taskID + ".xml";
        task = new Task(xmlUrl);
        Utils.updateDeviceActivity(deviceID);
        return task;
    }

    /*
     * Description: Return the source code of a task.
     * Parameters : - The task ID.
     * Returns    : - The task source code.
     * Changelog  : - .
     */
    @GET
    @Path("{taskID}/getsrc/")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getSrc(@PathParam("taskID") String taskID) {
        String TAG = TaskService.class.getName() + "@getSrc: ";
        String taskDir = Utils.getDir(app, taskID);
        String xmlUrl = taskDir + "/" + taskID + ".xml";
        String taskName = Utils.getText(xmlUrl, "name");
        String srcUrl = taskDir + "/client/" + taskName;
        System.out.println(TAG + "Returning task source with id " + taskID + " from " + taskName + "...");
        FileInputStream fis = null;
        String src = "/*\n";
        src += " * " + Utils.getText(xmlUrl, "name") + "\n";
        src += " * " + Utils.getText(xmlUrl, "comment") + "\n";
        src += " * " + "assigned id " + Utils.getText(xmlUrl, "id") + "\n";
        src += " * " + "submitted by " + Utils.getText(xmlUrl, "username") + "\n";
        src += " * " + "on " + Utils.getText(xmlUrl, "date") + " at " + Utils.getText(xmlUrl, "time") + "\n";
        src += " */\n\n";
        try {
            fis = new FileInputStream(srcUrl);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            while (dis.available() != 0) {
                src += (dis.readLine()) + "\n";
            }
            close(dis);
            close(bis);
            close(fis);

        } catch (IOException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return src;
    }

    /*
     * Description: Return the source code of a task.
     * Parameters : - The task ID.
     * Returns    : - The task source code.
     * Changelog  : - .
     */
    @GET
    @Path("getlist/")
    @Produces(MediaType.TEXT_HTML)
    public InputStream getList() {
        String TAG = TaskService.class.getName() + "@getList: ";
        String html = "<h1>List of submitted Sensing Tasks</h1>";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password)) {
                ResultSet rs;
                try (Statement s = c.createStatement()) {
                    rs = s.executeQuery("SELECT * FROM tasks WHERE ready='YES'");
                    html += "<ol type=\"1\">";
                    while (rs.next()) {
                        String id = rs.getString("id");
                        String dir = Utils.getDir(app, id);
                        String xml = dir + "/" + id + ".xml";
                        html += "<li value =\"" + Utils.getText(xml, "id") + "\"><a href=" + Globals.server_url + "/webresources/tasks/" + id + "/getsrc>"
                                + Utils.getText(xml, "name") + ""
                                + ": " + Utils.getText(xml, "comment") + ""
                                + " submitted by " + Utils.getText(xml, "username") + ""
                                + " on " + Utils.getText(xml, "date") + " at " + Utils.getText(xml, "time")
                                + "</a></li>";
                    }
                    html += "</ol>";
                }
                rs.close();
                c.close();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TaskService.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream is = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        return is;
    }

    /* 
     * Data 
     */
    @GET
    @Path("{taskID}/getdata")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Object getData(@PathParam("taskID") String taskID) throws Exception {
        String TAG = getClass().getName() + "@getData: ";

        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String dataUrl = clientDir + "/" + taskID + ".dat";
        File data = new File(dataUrl);
//        File data = new File(Globals.db_dir + "/data");
        if (data.exists()) {
            System.out.println(TAG + "Returning data " + dataUrl);
            //data.delete();
            return Response.ok("OK").header("Content-Disposition", "attachment; filename=\"" + taskID + ".dat\"").entity(data).build();
        } else {
            System.out.println(TAG + "No data found " + dataUrl);
            return Response.ok("OK").header("Content-Disposition", "attachment; filename=\"" + taskID + ".dat\"").entity(null).build();
        }
    }

    @GET
    @Path("{taskID}/getdataOLD")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Object getDataOLD(@PathParam("taskID") String taskID) throws Exception {
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String dataUrl = clientDir + "/" + taskID + ".dat";
        final File data = new File(dataUrl);
        if (data.exists()) {
            String response = FileUtils.readFileToString(data);
            //data.delete();
            return Response.ok("OK").header("Content-Disposition", "attachment; filename=\"" + taskID + ".dat\"").entity(response).build();
        } else {
            return "Empty.";
        }
    }

    @GET
    @Path("{taskID}/deletedata")
    public void deleteData(@PathParam("taskID") String taskID) throws Exception {
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String dataUrl = clientDir + "/" + taskID + ".dat";
        String xmlUrl = taskDir + "/" + taskID + ".xml";
        System.out.println("TaskService/deleteData: " + "Deleting data " + dataUrl);
        File data = new File(dataUrl);
        if (data.exists()) {
            File dir = new File(clientDir);
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".dat")) {
                    f.delete();
                }
            }

            rmvLog(xmlUrl);
        }
    }

    @GET
    @Path("{taskID}/getdata/delete")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Object downdelData(@PathParam("taskID") String taskID) throws Exception {
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String dataUrl = clientDir + "/" + taskID + ".dat";
        String xmlUrl = taskDir + "/" + taskID + ".xml";
        final File data = new File(dataUrl);
        if (data.exists()) {
            String response = FileUtils.readFileToString(data);
            data.delete();
            rmvLog(xmlUrl);
            return Response.ok("OK").header("Content-Disposition", "attachment; filename=\"" + taskID + ".dat\"").entity(response).build();
        } else {
            return Response.serverError().build();
        }
    }

    /*
    
     */
    @GET
    @Path("{taskID}/getdata/{dataName}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Object getData(
            @PathParam("taskID") String taskID,
            @PathParam("dataName") String dataName) throws Exception {
        ResponseBuilder response = Response.ok("ERROR");
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String serverDir = taskDir + "/server";
        String dataUrl = clientDir + "/" + dataName;
        System.out.println("TaskService/getdata: " + "Returning data " + dataUrl);
        File data = new File(dataUrl);
        if (data.exists()) {
            return data;
        } else {
            return null;
        }
    }

    @GET
    @Path("{taskID}/getdata/dat.zip")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Object getDataZip(@PathParam("taskID") String taskID) throws Exception {
        ResponseBuilder response = Response.ok("ERROR");
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        System.out.println("TaskService/getdata: " + "Returning all data from " + clientDir);

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(clientDir + "/" + "dat.zip");
        ZipOutputStream zos = new ZipOutputStream(fos);
        File[] list = new File(clientDir).listFiles();
        for (File file : list) {
            if ("dat".equals(FilenameUtils.getExtension(file.getName()))) {
                String name = file.getAbsoluteFile().toString().substring(clientDir.length() + 1, file.getAbsoluteFile().toString().length());
                ZipEntry ze = new ZipEntry(name);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(clientDir + File.separator + name);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                close(in);
            }
        }
        close(zos);
        close(fos);
        return new File(clientDir + "/" + "dat.zip");
    }

    @GET
    @Path("{taskID}/deletedata/{dataName}")
    public void deleteData(
            @PathParam("taskID") String taskID,
            @PathParam("dataName") String dataName) throws Exception {
        //ResponseBuilder response = Response.ok("ERROR");
        String taskDir = Utils.getDir(app, taskID);
        String clientDir = taskDir + "/client";
        String serverDir = taskDir + "/server";
        String dataUrl = clientDir + "/" + dataName;
        System.out.println("TaskService/deletedata: " + "Deleting data " + dataUrl);
        rmvLog(taskDir + "/" + taskID + ".xml", dataName.substring(0, dataName.indexOf('@')));
        File data = new File(dataUrl);
        if (data.exists()) {
            data.delete();
        }
    }

    private void mergeData(String dataUrl, String newDataUrl) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(dataUrl), true);
            fw.write(FileUtils.readFileToString(new File(newDataUrl)));
        } catch (IOException ex) {
            Logger.getLogger(TaskService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.close(fw);
        }
    }

    /*
     * Log
     */
    private void addLog(String url, String dataName, String deviceID) {
        String TAG = getClass().getName() + "@addLog: ";
        SAXBuilder builder = new SAXBuilder();
        File xml = new File(url);
        try {
            Document doc = (Document) builder.build(xml);
            Element root = doc.getRootElement().getChild("client").getChild("log");
            Element data = new Element("data");
            root.addContent(data);

            data.setAttribute("id", dataName);
            data.addContent(new Element("date").setText(Utils.getDate()));
            data.addContent(new Element("time").setText(Utils.getTime()));
            data.addContent(new Element("device").setText(deviceID));

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(doc, new FileWriter(url));

        } catch (JDOMException | IOException ex) {
            System.out.println(TAG + ex.getMessage());
        }

    }

    private void rmvLog(String url, String dataName) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        File xml = new File(url);
        Document doc = (Document) builder.build(xml);
        Element root = doc.getRootElement().getChild("client").getChild("log");
        List list = root.getChildren("data");
        for (int i = 0; i < list.size(); i++) {
            Element element = (Element) list.get(i);
            if (dataName.equals(element.getAttributeValue("id").toString())) {
                element.getParent().removeContent(element);
            }
        }
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(doc, new FileWriter(url));
    }

    private void rmvLog(String url) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        File xml = new File(url);
        Document doc = (Document) builder.build(xml);
        Element root = doc.getRootElement().getChild("client").getChild("log");
        root.removeContent();
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(doc, new FileWriter(url));
    }

    /*
     * Data
     */
    boolean saveDataToFile(List data, String url) {
        String TAG = getClass().getName() + "@saveData: ";

        try {
            System.out.println(TAG + "Saving data to " + url);
            List<Object> oldData = getDataFromFile(url);
            if (!oldData.isEmpty()) {
                oldData.addAll(data);
                data = oldData;
            }
            FileOutputStream fos = new FileOutputStream(url);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            close(oos);
            close(fos);
            System.gc();
        } catch (Exception ex) {
            System.out.println(TAG + ex.getMessage());
            return false;
        }
        System.out.println(TAG + "OK");
        return true;
    }

    List getDataFromFile(String url) {
        String TAG = getClass().getName() + "@getDataFromFile: ";

        FileInputStream fis;
        ObjectInputStream ois;

        List<Object> data = new ArrayList<>();

        try {
            System.out.println(TAG + "Returning data from " + url);
            if (new File(url).exists()) {
                fis = new FileInputStream(url);
                ois = new ObjectInputStream(fis);
                data = (List) ois.readObject();
                close(ois);
                close(fis);
            } else {
                System.out.println(TAG + "File does not exist");
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        System.out.println(TAG + "OK");
        return data;
    }

    /*
     * Misc
     */
    private void loadClass(String dir, String className, Object object) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoClassDefFoundError, IllegalArgumentException, InvocationTargetException {
        URL url = new File(dir).toURI().toURL();
        ClassLoader cl = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        Class clss = cl.loadClass(className);
        Object instance = clss.newInstance();
        Method method = clss.getMethod("main", new Class[]{Object.class
        }
        );
        method.invoke(instance, new Object[]{object});
    }

    private String getReadyTask() throws ClassNotFoundException, SQLException {
        String id = "";
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
        Statement s = c.createStatement();
        /*rs = s.executeQuery("SELECT * FROM tasks WHERE compiled='YES' AND downloaded=0");
         if (rs.next()) {
         id = rs.getString("id");
         }/**/
        ResultSet rs = s.executeQuery("SELECT * FROM tasks WHERE ready='YES'");
        int min;
        if (rs.next()) {
            min = rs.getInt("downloaded");
            id = rs.getString("id");
            while (rs.next()) {
                if (rs.getInt("downloaded") < min) {
                    min = rs.getInt("downloaded");
                    id = rs.getString("id");
                }
            }
        }
        rs.close();
        s.close();
        c.close();
        return id;
    }

    private void updateDownloaded(String id) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
        Statement s = c.createStatement();
        s.executeUpdate("UPDATE tasks SET downloaded=downloaded+1 WHERE id='" + id + "'");
        s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM tasks WHERE id='" + id + "'");
        rs.next();
        Utils.setText(Utils.getDir(app, id) + "/" + id + ".xml", "client", "downloaded", "counter", rs.getString("downloaded"));
        rs.close();
        s.close();
        c.close();

    }

    public static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
            }
        }
    }
}
