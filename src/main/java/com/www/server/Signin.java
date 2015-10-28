package com.www.server;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Signin extends HttpServlet {

    //private ServletConfig config;
    private String DB_USERNAME;
    private String DB_PASSWORD;
    private String DB_SERVER;

    @Override
    public void init() throws ServletException {
        DB_USERNAME = Globals.db_username;
        DB_PASSWORD = Globals.db_password;
        DB_SERVER = Globals.db_server;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session;
        PrintWriter out = response.getWriter();
        Connection connection;
        ResultSet rs;
        Statement s;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String message = "";
        Boolean error = true;
        response.setContentType("text/html");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_SERVER, DB_USERNAME, DB_PASSWORD);
            String sql = "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'";
            s = connection.createStatement();
            s.executeQuery(sql);
            rs = s.getResultSet();
            if (rs.next()) {
                error = false;
            }
            rs.close();
            s.close();
            connection.close();
            if (!error) {
                session = request.getSession(true);
                session.setAttribute("username", username);
                response.sendRedirect(response.encodeRedirectURL("./main/home.jsp"));
            } else {
                message = "The username or password you entered is incorrect.";
                request.setAttribute("errorMessage", message);
                RequestDispatcher view = request.getRequestDispatcher("/signin.jsp");
                view.forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL("signin.jsp"));
    }
}
