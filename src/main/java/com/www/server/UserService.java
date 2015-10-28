package com.www.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/user")
public class UserService {

    String DEVICES_INFO = "jdbc:mysql://localhost:3306/devices_info";
    String USERNAME = Globals.db_username;
    String PASSWORD = Globals.db_password;

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "User service is ready.";
    }

    @GET
    @Path("register_device/{username}_{model}_{os}")
    public Response registerDevice(@PathParam("username") String username, @PathParam("model") String model, @PathParam("os") String os) throws ClassNotFoundException, SQLException {
        int id = getNewId();
        registerNewDevice(id, username, model, os);
        return Response.ok().entity(String.valueOf(id)).build();
    }

    private int getNewId() throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(DEVICES_INFO, USERNAME, PASSWORD);
        ResultSet rs;
        Statement s = conn.createStatement();
        String sql = "SELECT id FROM counter";
        rs = s.executeQuery(sql);
        rs.next();
        int id = Integer.parseInt(rs.getString("id"));
        s.executeUpdate("UPDATE counter SET id = (id + 1)");
        s.close();
        rs.close();
        conn.close();
        return id;
    }

    private void registerNewDevice(int id, String username, String model, String os) throws ClassNotFoundException, SQLException {
        System.out.println("id: " + id);
        System.out.println("username: " + username);
        System.out.println("model: " + model);
        System.out.println("os: " + os);
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(DEVICES_INFO, USERNAME, PASSWORD);
        Statement s = conn.createStatement();
        String sql = "INSERT INTO devices_info.devices (`id`, `username`, `model`, `os`) VALUES ('" + id + "', '" + username + "', '" + model + "', '" + os + "')";
        s.executeUpdate(sql);
        s.close();
        conn.close();
    }
}
