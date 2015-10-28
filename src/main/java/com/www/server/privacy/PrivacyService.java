package com.www.server.privacy;

import com.www.server.Globals;
import com.www.server.Utils;
import java.io.*;
import java.lang.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jettison.json.*;

@Path("pms")
public class PrivacyService {

    String app = "privacy";

    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        String TAG = PrivacyService.class.getName() + "@respondAsReady: ";
        return TAG + "OK!";
    }

    /*
     * Description: Used for testing purposes only.
     * Parameters : - .
     * Returns    : - .
     * Changelog  : - .
     */
    @GET
    @Path("demo_filter/{param}")
    public String demoFilter(
            @PathParam("param") String param
    ) {
        String TAG = getClass().getName() + "@test: ";

        int pref = Integer.valueOf(param);

        String url = Globals.db_dir + "/emkatsom/sensing/5/client/5.dat";

        List<Object> list = getData(url);

        System.out.println("1111111111");
//
        printData(list);

//        edit each entry of the input
        for (Object o : list) {

            Map data = (Map) o;

            data.put("device", data.get("device"));
            data.put("task", data.get("task"));
            data.put("sensor", data.get("sensor"));
            data.put("timestamp", (Long) data.get("timestamp") + (pref / 10) * 3600000000000L);
            data.put("values", data.get("values"));

        }

        System.out.println("2222222222");

        printData(list);

        return "OK";
    }

    boolean checkDataKeys(List data) {
        String TAG = getClass().getName() + "@checkDataKeys: ";

        for (Object o : data) {
            Map m = (Map) o;
            for (Object key : m.keySet()) {
                try {
                    if ("sensor".equals((String) key)
                            || "timestamp".equals((String) key)
                            || "values".equals((String) key)) {
                        System.out.println(TAG + key + " key" + " OK");
                    } else {
                        System.out.println(TAG + key + " key" + " ERROR");
                        return false;
                    }
                } catch (Exception e) {
                    System.out.println(TAG + key + " key " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    boolean checkDataValues(List data) {
        String TAG = getClass().getName() + "@checkDataValues: ";

        for (Object o : data) {
            Map m = (Map) o;
            for (Object key : m.keySet()) {
                if ("sensor".equals(key)) {
                    try {
                        int i = (int) m.get(key);
                        System.out.println(TAG + key + " value " + "OK");
                    } catch (Exception e) {
                        System.out.println(TAG + key + " value " + e.getMessage());
                        return false;
                    }
                } else if ("timestamp".equals(key)) {
                    try {
                        long l = (long) m.get(key);
                        System.out.println(TAG + key + " value " + "OK");
                    } catch (Exception e) {
                        System.out.println(TAG + key + " value " + e.getMessage());
                        return false;
                    }
                } else if ("values".equals(key)) {
                    try {
                        double[] d = (double[]) m.get(key);
                        System.out.println(TAG + key + " value " + "OK");
                    } catch (Exception e) {
                        System.out.println(TAG + key + " value " + e.getMessage());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    boolean saveData(List data, String url) {
        String TAG = getClass().getName() + "@saveData: ";

        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            System.out.println(TAG + "Saving data to " + url);
            List<Object> oldData = getData(url);
            if (!oldData.isEmpty()) {
                oldData.addAll(data);
                data = oldData;
            }
            fos = new FileOutputStream(url);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            close(oos);
            close(fos);
        } catch (Exception ex) {
            System.out.println(TAG + ex.getMessage());
            return false;
        }
        System.out.println(TAG + "OK");
        return true;
    }

    void printData(List data) {
        String html = "";
        for (Object o : data) {
            Map m = (Map) o;
            html += ""
                    + new Date((Long) m.get("timestamp") / 1000000L).toString() + ""
                    + " task[" + m.get("task") + "]"
                    + "@" + m.get("device") + ":"
                    + " " + m.get("sensor") + ""
                    + "" + Arrays.toString((double[]) m.get("values")) + ""
                    + "\n";
        }
        System.out.println(html);
    }

    List getData(String url) {
        String TAG = getClass().getName() + "@getData: ";

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
        } catch (Exception ex) {
            System.out.println(TAG + ex.getMessage());
        }
        System.out.println(TAG + "OK");
        return data;
    }

    /*
     * Description: Return the list of available privacy mechanisms.
     * Parameters : - The device id.
     * Returns    : - The list of pms.
     * Changelog  : - .
     */
    @GET
    @Path("getlist/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getList(@PathParam("deviceID") String deviceID) {
        String TAG = PrivacyService.class.getName() + "@get_list: ";
        List<String> l = new ArrayList<>();

        if (Utils.deviceExists(deviceID)) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                Statement s = c.createStatement();
                s.executeQuery("SELECT * FROM pms WHERE ready='YES'");
                ResultSet rs = s.getResultSet();
                while (rs.next()) {
                    l.add(getInfo(rs.getString("id")));
                }
                rs.close();
                s.close();
                c.close();
            } catch (ClassNotFoundException | SQLException ex) {
                System.out.println(TAG + ex.getMessage());
            }
        } else {
            try {
                JSONObject o = new JSONObject();
                o.append("Error", "Device not found");
                l.add(o.toString());
                System.out.println(TAG + o.toString());
            } catch (JSONException ex) {
                System.out.println(TAG + ex.getMessage());
            }
        }
        return l;
    }

    /*
     * Description: Return the list of available privacy mechanisms for a 
     *              specific Sensing Task.
     * Parameters : - The Sensing Task id.
     *              - The device id.
     * Returns    : - The list of pms.
     * Changelog  : - .
     */
    @GET
    @Path("getlist/{stID}/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getList(
            @PathParam("stID") String stID,
            @PathParam("deviceID") String deviceID) {
        String TAG = PrivacyService.class
                .getName() + "@get_list: ";
        List<String> l = new ArrayList<>();

        if (Utils.deviceExists(deviceID)) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                Statement s = c.createStatement();
                s.executeQuery("SELECT * FROM pms WHERE sensing='" + stID + "' AND ready='YES'");
                ResultSet rs = s.getResultSet();
                while (rs.next()) {
                    l.add(getInfo(rs.getString("id")));
                }
                rs.close();
                s.close();
                c.close();
            } catch (ClassNotFoundException | SQLException ex) {
                System.out.println(TAG + ex.getMessage());
            }
        } else {
            try {
                JSONObject o = new JSONObject();
                o.append("Error", "Device not found");
                l.add(o.toString());
                System.out.println(TAG + o.toString());
            } catch (JSONException ex) {
                System.out.println(TAG + ex.getMessage());
            }
        }

        JSONArray json = new JSONArray(l);
//        System.out.println(TAG + json.toString());

        try {
            JSONArray jsonArray = new JSONArray(json.toString());
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            JSONObject o = new JSONObject(list.get(0));
            System.out.println(TAG + o.getJSONArray("name").getString(0));
        } catch (Exception ex) {
            System.out.println(TAG + ex.getMessage());
        }

        return l;
    }

    /*
     * Description: Returns information for a particular mechanism.
     * Parameters : - The pm id.
     *              - The device id.
     * Returns    : - The info.
     * Changelog  : - .
     */
    @GET
    @Path("{pmID}/getinfo/{deviceID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfo(
            @PathParam("pmID") String pmID,
            @PathParam("deviceID") String deviceID) {
        String TAG = PrivacyService.class
                .getName() + "@get_info: ";
        String info = "Oops!";

        if (Utils.deviceExists(deviceID)) {
            info = getInfo(pmID);
        } else {
            info = "Device not found.";
        }

        System.out.println(TAG
                + info);
        return info;
    }

    /*
     * Description: Returns the binary file of a particular mechanism.
     * Parameters : - The pm id.
     *              - The device id.
     * Returns    : - The pm binary file.
     * Changelog  : - .
     */
    @GET
    @Path("{pmID}/getbin/{deviceID}")
    public Response getBin(
            @PathParam("pmID") String pmID,
            @PathParam("deviceID") String deviceID) {
        String TAG = PrivacyService.class
                .getName() + "@get_bin: ";
        Response.ResponseBuilder response;

        if (Utils.deviceExists(deviceID)) {
            String dir = Utils.getDir(app, pmID);
            if (dir != null && !"Oops!".equals(dir)) {
                File bin = Utils.returnFileFrom(dir, ".zip");
                response = Response.ok((Object) bin);
            } else {
                response = Response.ok("Invalid privacy mechanism ID.");
            }
        } else {
            response = Response.ok("Device not found.");
        }

        System.out.println(TAG
                + response.toString());
        return response.build();
    }

    /*
     * Description: Returns information for a particular mechanism.
     * Parameters : - The device id.
     * Returns    : - The info.
     * Changelog  : - .
     */
    private String
            getInfo(String id) {
        String TAG = PrivacyService.class
                .getName() + "@getInfo: ";
        String info = "Oops!";

        try {
            String dir = Utils.getDir(app, id);
            if (!"Oops!".equals(dir)) {
                String xmlUrl = dir + "/" + id + ".xml";
                JSONObject o = new JSONObject();
                o.append("id", Utils.getText(xmlUrl, "id"));
                o.append("name", Utils.getText(xmlUrl, "name"));
//              class name
                o.append("class", Utils.getText(xmlUrl, "source", "class"));
//                
                o.append("version", Utils.getText(xmlUrl, "version"));
                o.append("description", Utils.getText(xmlUrl, "description"));
                o.append("user", Utils.getText(xmlUrl, "user"));
                o.append("date", Utils.getText(xmlUrl, "date"));
                o.append("time", Utils.getText(xmlUrl, "time"));
                o.append("size", Utils.getText(xmlUrl, "size"));

                info = o.toString();
            } else {
                info = "Invalid privacy mechanism ID.";
            }
        } catch (JSONException ex) {
            System.out.println(TAG + ex.getMessage());
        }
        return info;
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
