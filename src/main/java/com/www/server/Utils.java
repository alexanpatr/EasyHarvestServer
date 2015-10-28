package com.www.server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Utils {

    /* XML utilities */
    /*
     * Description: Return from the url xml file the content of node.
     * Parameters : - .
     * Returns    : - .
     * Changelog  : 150523 - Added console output.
     */
    public static String getText(String url, String node) {
        if (Globals.DBG) {
            System.out.println("Utils.getText1: " + url + " " + node);
        }
        String text = "";
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            text = root.getChildText(node);
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.getText1: " + e.getMessage());
            }
            return text;
        }
        if (Globals.DBG) {
            System.out.println("Utils.getText1: " + text);
        }
        return text;
    }

    public static String getText(String url, String node1, String node2) {
        if (Globals.DBG) {
            System.out.println("Utils.getText2: " + url + " " + node1 + " > " + node2);
        }
        String text = "";
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            Element child = root.getChild(node1);
            text = child.getChildText(node2);
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.getText2: " + e.getMessage());
            }
            return " ";
        }
        if (Globals.DBG) {
            System.out.println("Utils.getText2: " + text);
        }
        return text;
    }

    public static String getText(String url, String node1, String node2, String node3) {
        if (Globals.DBG) {
            System.out.println("Utils.getText3: " + url + " " + node1 + " > " + node2 + " > " + node3);
        }
        String text = "";
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            Element child1 = root.getChild(node1);
            Element child2 = child1.getChild(node2);
            text = child2.getChildText(node3);
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.getText3: " + e.getMessage());
            }
            return text;
        }
        if (Globals.DBG) {
            System.out.println("Utils.getText3: " + text);
        }
        return text;
    }

    /*
     * Description: Set in url xml file the content of node to string.
     * Parameters : - .
     * Returns    : - .
     * Changelog  : 150523 - Commented out errors and added console output.
     */
    public static void setText(String url, String node, String string) {
        if (Globals.DBG) {
            System.out.println("Utils.setText1: " + url + " " + node + " > " + string);
        }
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            root.getChild(node).setText(string);
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(document, new FileWriter(url));
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.setText1: " + e.getMessage());
            }
        }
    }

    public static void setText(String url, String child1, String child2, String string) {
        if (Globals.DBG) {
            System.out.println("Utils.setText2: " + url + " " + child1 + " > " + child2 + " > " + string);
        }
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            Element node1 = root.getChild(child1);
            Element node2 = node1.getChild(child2);
            node2.setText(string);
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(document, new FileWriter(url));
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.setText2: " + e.getMessage());
            }
        }
    }

    public static void setText(String url, String child1, String child2, String child3, String string) {
        if (Globals.DBG) {
            System.out.println("Utils.setText3: " + url + " " + child1 + " > " + child2 + " > " + child3 + " > " + string);
        }
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        try {
            Document document = (Document) builder.build(xmlFile);
            Element root = document.getRootElement();
            Element node1 = root.getChild(child1);
            Element node2 = node1.getChild(child2);
            Element node3 = node2.getChild(child3);
            node3.setText(string);
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.setFormat(Format.getPrettyFormat());
            xmlOutputter.output(document, new FileWriter(url));
        } catch (JDOMException | IOException e) {
            if (Globals.DBG) {
                System.out.println("Utils.setText3: " + e.getMessage());
            }
        }
    }

    public static void addText(String url, String node1, String node2, String string) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(url);
        Document document = (Document) builder.build(xmlFile);
        Element root = document.getRootElement();
        root.getChild(node1).getChild(node2).addContent(string);
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(document, new FileWriter(url));
    }

    public static List getNodeList(String url, String element1, String element2, String label) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        File xml = new File(url);
        Document doc = (Document) builder.build(xml);
        Element root = doc.getRootElement().getChild(element1).getChild(element2);
        return root.getChildren(label);
    }

    public static void appendNode(String url, String node, String child, String label, String text) throws JDOMException, IOException {

    }
    /*  */

    /* Misc utilities */
    public static String getTime() {
        Calendar calen = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(calen.getTime());
    }

    public static String getTime(String format) {
        Calendar calen = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(format);
        return df.format(calen.getTime());
    }

    public static String getDate() {
        Calendar calen = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(calen.getTime());
    }

    public static Boolean isDateRecent(String date, int months, int days, int years) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calPrev = Calendar.getInstance();
        Date datePrev = sdf.parse(date);
        calPrev.setTime(datePrev);
        Calendar calCurr = Calendar.getInstance();
        Date dateCurr = sdf.parse(sdf.format(calCurr.getTime()));
        calCurr.setTime(dateCurr);
        calCurr.add(Calendar.MONTH, -months);
        calCurr.add(Calendar.DAY_OF_MONTH, -days);
        calCurr.add(Calendar.YEAR, -years);
        /*System.out.println("calCurr: " + calCurr.getTime());
         System.out.println("calPrev: " + calPrev.getTime());
         System.out.println("calPrev after calCurr: " + calPrev.after(calCurr));/**/
        return calPrev.after(calCurr);
    }

    public static Boolean isTimeRecent(String time, int hours, int minutes, int seconds) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar calPrev = Calendar.getInstance();
        Date datePrev = sdf.parse(time);
        calPrev.setTime(datePrev);
        Calendar calCurr = Calendar.getInstance();
        Date dateCurr = sdf.parse(sdf.format(calCurr.getTime()));
        calCurr.setTime(dateCurr);
        calCurr.add(Calendar.HOUR, -hours);
        calCurr.add(Calendar.MINUTE, -minutes);
        calCurr.add(Calendar.SECOND, -seconds);
        /*System.out.println("calCurr: " + sdf.format(calCurr.getTime()));
         System.out.println("calPrev: " + sdf.format(calPrev.getTime()));
         System.out.println("calPrev after calCurr: " + calPrev.after(calCurr));/**/
        return calPrev.after(calCurr);
    }

    public static Boolean stringIsEmpty(String string) {
        if (string == null) {
            return true;
        } else if (string.isEmpty()) {
            return true;
        } else if ("".equals(string)) {
            return true;
        } else if (string.trim().length() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Description: Checks if a device is registered.
     * Parameters : - The device id to be checked.
     * Returns    : - false/ture accordingly.
     * Changelog  : - .
     */
    public static Boolean deviceExists(String id) {
        Boolean exists = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                    Statement s = c.createStatement()) {
                s.executeQuery("SELECT * FROM devices WHERE id='" + id + "'");
                try (ResultSet rs = s.getResultSet()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
    }

    public static String getDeviceInfo(String id, String request) {
        String TAG = "Utils@getDeviceInfo: ";

        String info = "Oops!";
        String connectionURL = "jdbc:mysql://localhost:3306/server";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionURL, Globals.db_username, Globals.db_password);
            ResultSet rs;
            Statement s = connection.createStatement();
            String sql = "SELECT " + request + " FROM devices WHERE id='" + id + "'";
            rs = s.executeQuery(sql);
            if (rs.next()) {
//                System.out.println(TAG + rs.toString());
                info = rs.getString(request);
            }
            rs.close();

            s.close();
            connection.close();
        } catch (SQLException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return info;
    }

    public static void updateDeviceActivity(String id) {
        String TAG = "Utils@updateDeviceActivity: ";
        String date = Utils.getDate();
        String time = Utils.getTime();
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver);
            try (
                    Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                    Statement s = c.createStatement()) {
                s.executeUpdate("UPDATE devices SET last_date='" + date + "', last_time='" + time + "' WHERE id='" + id + "'");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        /**/

        /**/
    }

    /*  */

    /* File utilities */
    /*
     * Description: Saves a file item to a selected destination.
     * Parameters : - The file item.
     *              - The url to save the file.
     * Returns    : - The size of the file.
     * Changelog  : 150523 - Commented out errors and added console output.
     */
    public static String writeToFile(FileItem fi, String url) {
        File file = new File(url);
        try {
            fi.write(file);
        } catch (Exception ex) {
            System.out.println(Utils.class.getName() + "@writeToFile:" + ex.getMessage());
        }
        long size = file.length();
        return Long.toString(size);
    }

    public static void writeToNewFile(InputStream input, String url) throws IOException {
        OutputStream output = new FileOutputStream(new File(url));
        int read;
        byte[] buffer = new byte[1024];
        while ((read = input.read(buffer)) > 0) {
            output.write(buffer, 0, read);
        }
        close(output);
        close(input);
    }

    public static void writeToFile(InputStream input, String url) {
        File file = new File(url);
        long seek = file.length();
        RandomAccessFile output = null;
        try {
            output = new RandomAccessFile(file, "rw");
            output.seek(seek);
            int read;
            byte[] buffer = new byte[1024];
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } catch (Exception ex) {
            //Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        close(input);
        close(output);
    }

    public static File returnFileFrom(String url, String type) {
        File folder = new File(url);
        String fileName;
        File file = null;
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                fileName = listOfFile.getName();
                if (fileName.endsWith(type)) {
                    file = new File(url + "/" + fileName);
                    break;
                }
            }
        }
        return file;
    }

    /*
     * Description: Return the directory of the task id.
     * Parameters : - sensing/privacy.
     *              - The file id.
     * Returns    : - The file url.
     * Changelog  : 150529 - Rename from getFileDir to getTaskDir and remove 
     *                       throws clauses.
     *              150608 - Add privacy support.
     */
    public static String getDir(String app, String id) {
        String url = "Oops!";
        String table = "";
        if ("sensing".equals(app)) {
            table = "tasks";
        } else if ("privacy".equals(app)) {
            table = "pms";
        } else {
            return "Oops!";
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password)) {
                ResultSet rs;
                try (Statement s = c.createStatement()) {
                    rs = s.executeQuery("SELECT * FROM " + table + " WHERE id='" + id + "'");
                    if (rs.next()) {
                        url = Globals.db_dir + "/" + rs.getString("username") + "/" + app + "/" + id;
                    }
                }
                rs.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Utils.getTaskDir: " + e.getMessage());
        }
        if (Globals.DBG) {
            System.out.println("Utils.getTaskDir: " + url);
        }
        return url;
    }

    public static boolean seekFile(String url) {
        File file = new File(url);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static long getFileSize(String url) {
        File file = new File(url);
        if (file.exists()) {
            return file.length();
        } else {
            return 0;
        }
    }

    public static String compareFiles(String url, Long size) {
        String response = "Oops!";
        File file = new File(url);
        if (size == file.length()) {
            response = "equal";
        } else if (size < file.length()) {
            response = "smaller";
        } else if (size > file.length()) {
            response = "larger";
        }
        return response;
    }

    public static File returnPart(String deviceID, String url, long start) throws IOException {
        File file = new File(url);
        OutputStream output = new FileOutputStream(url + "@" + deviceID + ".tmp");
        int read;
        byte[] buffer = new byte[1024];
        RandomAccessFile input;
        input = new RandomAccessFile(file, "r");
        input.seek(start);
        while ((read = input.read(buffer)) > 0) {
            output.write(buffer, 0, read);
        }
        close(output);
        close(input);
        File part = new File(url + "@" + deviceID + ".tmp");
        return part;
    }

    public static void copyFileOLD(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        if (input.length() == length) {
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } else {
            input.seek(start);
            long toRead = length;
            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    public static void returnPart(RandomAccessFile input, OutputStream output, long start) throws IOException {
        int read;
        byte[] buffer = new byte[1024];
        input.seek(start);
        while ((read = input.read(buffer)) > 0) {
            output.write(buffer, 0, read);
        }
        close(output);
        close(input);
    }

    public static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
            }
        }
    }
    /*  */

    /* Compilation utilities */
    public static String compileFile(String dir, String fileName) throws IOException, InterruptedException, JDOMException, ClassNotFoundException, SQLException {
        String result = "";
        String javac_cmd = "\"" + Globals.javac_cmd + "\"" + " -cp " + dir + "/\\* " + dir + "/" + fileName;
        System.out.println("Utils.compileFile: " + javac_cmd);

        Process p = Runtime.getRuntime().exec(Globals.console_cmd);
        OutputStream os = p.getOutputStream();
        os.write((javac_cmd + "\n").getBytes());
        os.flush();
        os.close();
        p.waitFor();
        BufferedReader bf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line = bf.readLine();
        while (line != null) {
            result = result + line + "\n";
            line = bf.readLine();
        }
        close(bf);
        close(os);

        return result;
    }

    public static boolean findMethod(String method, File file) {
        Pattern p = Pattern.compile("\\b" + method + "\\b");
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file.getPath()));
            String line = "";
            while ((line = bf.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /*
     * Description: Compiles the source code of the sensing task.
     * Parameters : - The source code file.
     * Returns    : - The compilation log.
     * Changelog  : 150523 - Paths with spaces and console output.
     *              150607 - Remove throws clauses.
     */
    public static String compileAndroidFile(String dir, String fileName) {
        String result = "";
        try {
            String javac_cmd = "\"" + Globals.javac_cmd + "\"" + " -cp " + "\"" + Globals.lib_url + "\"" + " " + "\"" + dir + "/" + fileName + "\"";
            System.out.println("Utils.compileAndroidFile: " + javac_cmd);

            Process p = Runtime.getRuntime().exec(Globals.console_cmd);
            OutputStream os = p.getOutputStream();
            os.write((javac_cmd + "\n").getBytes());
            close(os);
            p.waitFor();
            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = bf.readLine();
            while (line != null) {
                result = result + line + "\n";
                line = bf.readLine();
            }
            close(bf);
            if (!findMethod("public void onStart", new File(dir + "/" + fileName))) {
                result += "'public void onStart(Context, ObjectInputStream)' method not found.\n";
                System.out.println("'public void onStart (Context, ObjectInputStream)' method not found.");
            }
            if (!findMethod("public void onStop", new File(dir + "/" + fileName))) {
                result += "'public void onStop()' method not found.\n";
                System.out.println("'public void onStop()' method not found.");
            }

            /*
             if (!findMethod("public ArrayList<String> getData()", new File(dir + "/" + fileName))) {
             result += "'public ArrayList<String> getData()' method not found.\n";
             System.out.println("'public ArrayList<String> getData()' method not found.");
             }
             */
            /*
             if (!findMethod("public boolean saveData", new File(dir + "/" + fileName))) {
             result += "'public boolean saveData(ObjectOutputStream)' method not found.\n";
             System.out.println("'public boolean saveData(ObjectOutputStream)' method not found.");
             }
             */
            if (!findMethod("public List<Object> getData", new File(dir + "/" + fileName))) {
                result += "'public List<Object> getData()' method not found.\n";
                System.out.println("'public List<Object> getData()' method not found.");
            }
            if (!findMethod("public boolean saveState", new File(dir + "/" + fileName))) {
                result += "'public boolean saveState(ObjectOutputStream)' method not found.\n";
                System.out.println("'public boolean saveState(ObjectOutputStream)' method not found.");
            }

            System.out.println("Utils.compileAndroidFile: " + result);
        } catch (IOException | InterruptedException ex) {
            System.out.println(Utils.class.getName() + "@compileAndroidFile: " + ex.getMessage());
        }
        return result;
    }

    /*
     * Description: Compiles the source code of the PM.
     * Parameters : - The source code file.
     * Returns    : - The compilation log.
     * Changelog  : - .
     */
    public static String compilePM(String dir, String fileName) {
        String TAG = Utils.class.getName() + "@compilePM: ";
        String result = "";
        try {
            String javac_cmd = "\"" + Globals.javac_cmd + "\"" + " -cp " + "\"" + Globals.lib_url + "\"" + " " + "\"" + dir + "/" + fileName + "\"";
            System.out.println(TAG + javac_cmd);

            Process p = Runtime.getRuntime().exec(Globals.console_cmd);
            try (OutputStream os = p.getOutputStream()) {
                os.write((javac_cmd + "\n").getBytes());
                close(os);
            }
            p.waitFor();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                String line = bf.readLine();
                while (line != null) {
                    result = result + line + "\n";
                    line = bf.readLine();
                }
            }

            if (!findMethod("public void onStart", new File(dir + "/" + fileName))) {
                result += "'public void onStart (Context, int, ObjectInputStream)' method not found.\n";
                System.out.println("'public void onStart (Context, int, ObjectInputStream)' method not found.");
            }
            if (!findMethod("public void onStop", new File(dir + "/" + fileName))) {
                result += "'public void onStop ()' method not found.\n";
                System.out.println("'public void onStop ()' method not found.");
            }
            if (!findMethod("public void onPreferenceChanged", new File(dir + "/" + fileName))) {
                result += "'public void onPreferenceChanged (int)' method not found.\n";
                System.out.println("'public void onPreferenceChanged (int)' method not found.");
            }
            if (!findMethod("public boolean saveState", new File(dir + "/" + fileName))) {
                result += "'public boolean saveState (ObjectOutputStream)' method not found.\n";
                System.out.println("'public boolean saveState (ObjectOutputStream)' method not found.");
            }
            if (!findMethod("public int processData", new File(dir + "/" + fileName))) {
                result += "'public int processData (ObjectInputStream, ObjectOutputStream)' method not found.\n";
                System.out.println("'public int processData (ObjectInputStream, ObjectOutputStream)' method not found.");
            }
            /**/
            System.out.println(TAG + result);
        } catch (IOException | InterruptedException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return result;
    }

    /*
     * Description: Compiles the java classes and creates the .dex file.
     * Parameters : - The directory of the code file.
     *              - The URL of the code file.
     * Returns    : - The compilation log.
     * Changelog  : 150523 - Paths with spaces and console output.
     *              150607 - Remove throws clauses.
     */
    public static String compileDex(String dir, String fileName) {
        String TAG = Utils.class.getName() + "@compileDex: ";
        String result = "";
        try {
            String name = FilenameUtils.removeExtension(fileName);
            String dx_cmd;
            dx_cmd = "\"" + Globals.dx_cmd + "\"" + " --dex --no-strict --output=" + "\"" + dir + "/classes.dex" + "\"" + " " + "\"" + dir + "/" + "\"" + "*" + ".class";
            System.out.println("Utils.compileDex: " + dx_cmd);
            Process p = Runtime.getRuntime().exec(Globals.console_cmd);
            try (OutputStream os = p.getOutputStream()) {
                os.write((dx_cmd + "\n").getBytes());
close(os);
            }
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                String line = bf.readLine();
                while (line != null) {
                    result = result + line + "\n";
                    line = bf.readLine();
                }
            }
            System.out.println(TAG + result);
        } catch (IOException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return result;
    }

    public static void unZip(String dir, String fileName) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(Globals.console_cmd);
        OutputStream os = p.getOutputStream();
        os.write(("cd " + dir + "\n").getBytes());
        os.write(("unzip " + dir + "/" + fileName + "\n").getBytes());
close(os);
        p.waitFor();
    }

    /*
     * Description: Generates fileId.zip from classes.dex inside dir.
     * Parameters : - The directory where the zip is saved.
     *              - The fileId.
     * Returns    : - The zip size.
     * Changelog  : 150523 - Paths with spaces and console output.
     */
    public static String zipDex(String dir, String fileId) {
        String TAG = Utils.class.getName() + "@zipDex: ";
        String result = "0";
        try {
            Process p = Runtime.getRuntime().exec(Globals.console_cmd);
            try (OutputStream os = p.getOutputStream()) {
                os.write(("cd " + "\"" + dir + "\"" + "\n").getBytes());
                os.write(("\"" + Globals.zip_cmd + "\"" + " " + Globals.zip_args + " " + fileId + ".zip " + "classes.dex" + "\n").getBytes());
close(os);
            }
            p.waitFor();
            System.out.println(TAG + dir + "/" + fileId + ".zip");
            File file = new File(dir + "/" + fileId + ".zip");
            System.out.println(TAG + Long.toString(file.length()));
            result = Long.toString(file.length());
        } catch (IOException | InterruptedException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return result;
    }

    /*
     * Description: Update the ready status.
     * Parameters : - The table.
     *              - The fileId.
     * Returns    : - The new status.
     * Changelog  : 150607 - Added PM support.
     */
    public static void updateReady(String table, String id, String status) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                    Statement s = c.createStatement()) {
                s.executeUpdate("UPDATE " + table + " SET ready='" + status + "' WHERE id='" + id + "'");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*  */
}
