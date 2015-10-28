package com.www.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("devs")
public class DeviceService {

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "DeviceService is ready.";
    }

    @GET
    @Path("register_test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response register_test() throws Exception {
        String username = "emkatsom";
        String model = "nexus";
        String os = "android";

//        String id = getNewId();
        String id = "0";

        String date = Utils.getDate();
        String time = Utils.getTime();
        Class.forName("com.mysql.jdbc.Driver");
        ResultSet rs;
        try (
                Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                Statement s = c.createStatement()) {
            String sql = "INSERT INTO devices (`username`, `model`, `os`, `reg_date`, `reg_time`, `last_date`, `last_time`) "
                    + "VALUES ('" + username + "', '" + model + "', '" + os + "', '" + date + "', '" + time + "', '" + date + "', '" + time + "')";
            s.executeUpdate(sql);
            s.executeQuery("SELECT * FROM devices ORDER BY id DESC LIMIT 1");
            rs = s.getResultSet();
            if (rs.next()) {
                id = rs.getString("id");
            }
        }
        System.out.println("DeviceService/register: Device (" + username + "|" + model + "|" + os + ") with id " + id + " successfully registered.");

        deviceUnregister();
        return Response.ok().entity(id).build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(MultivaluedMap<String, String> deviceParams) throws Exception {
        String username = deviceParams.getFirst("username");
        String model = deviceParams.getFirst("model");
        String os = deviceParams.getFirst("os");

//        String id = getNewId();
        String id = "0";

        String date = Utils.getDate();
        String time = Utils.getTime();
        Class.forName("com.mysql.jdbc.Driver");
        ResultSet rs;
        try (
                Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
                Statement s = c.createStatement()) {
            String sql = "INSERT INTO devices (`username`, `model`, `os`, `reg_date`, `reg_time`, `last_date`, `last_time`) "
                    + "VALUES ('" + username + "', '" + model + "', '" + os + "', '" + date + "', '" + time + "', '" + date + "', '" + time + "')";
            s.executeUpdate(sql);
            s.executeQuery("SELECT * FROM devices ORDER BY id DESC LIMIT 1");
            rs = s.getResultSet();
            if (rs.next()) {
                id = rs.getString("id");
            }
        }
        System.out.println("DeviceService/register: Device (" + username + "|" + model + "|" + os + ") with id " + id + " successfully registered.");

        deviceUnregister();
        return Response.ok().entity(id).build();
    }

    @GET
    @Path("{id}/unregister")
    public Response unregister(@PathParam("id") String id) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
        Statement s = c.createStatement();
        s.executeQuery("SELECT * FROM devices WHERE id='" + id + "'");
        ResultSet rs = s.getResultSet();
        String result;
        if (rs.next()) {
            s.executeUpdate("DELETE FROM devices WHERE id='" + id + "'");
            System.out.println("DeviceService.unregister: Device with id " + id + " successfully unregistered.");
            result = "OK";
        } else {
            System.out.println("DeviceService.unregister: Device with id " + id + " not found.");
            result = "Not registered";
        }
        rs.close();
        s.close();
        c.close();/**/

        deviceUnregister();
        return Response.ok().entity(result).build();
    }

    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(MultivaluedMap<String, String> deviceParams) throws ClassNotFoundException, SQLException, ParseException {
        System.out.println("test");

        System.out.println(deviceParams.toString());

        String username = deviceParams.getFirst("username");
        String model = deviceParams.getFirst("model");
        String os = deviceParams.getFirst("os");

        System.out.println(username + "|" + model + "|" + os);

        return Response.ok().entity("OK").build();
    }

    private String getNewId() throws ClassNotFoundException, SQLException {
        String id = "";
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
        Statement s = null;
        ResultSet rs;
        do {
            id = UUID.randomUUID().toString().replaceAll("-", "");
            s = c.createStatement();
            s.executeQuery("SELECT * FROM devices WHERE id='" + id + "'");
            rs = s.getResultSet();
        } while (rs.next());
        s.close();
        rs.close();
        c.close();
        return id;
    }

    /*
     Description:
     Called periodically to delete inactive devices.
    
     Changelog:
     */
    public void deviceUnregister() throws SQLException, ClassNotFoundException, ParseException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password);
        Statement s = c.createStatement();
        s.executeQuery("SELECT * FROM devices");
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            String id = rs.getString("id");
            String date = rs.getString("last_date");
            String time = rs.getString("last_time");
            //if (!Utils.isTimeRecent(time, 0, 1, 0)) {
            if (!Utils.isDateRecent(date, 1, 0, 0)) {
                System.out.println("DeviceService/DeviceUnregister: Unregistering device with id " + id + "...");
                s = c.createStatement();
                s.executeUpdate("DELETE FROM devices WHERE id='" + id + "'");
            }
        }
        s.close();
        rs.close();
        c.close();
    }

}
