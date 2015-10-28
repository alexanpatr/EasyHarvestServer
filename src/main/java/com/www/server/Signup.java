package com.www.server;

import java.io.*;
import java.io.File;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Signup extends HttpServlet {

    private String DB_DIR;
    private String DB_SERVER;
    private String DB_USERNAME;
    private String DB_PASSWORD;

    @Override
    public void init() {
        DB_DIR = Globals.db_dir;
        DB_SERVER = Globals.db_server;
        DB_USERNAME = Globals.db_username;
        DB_PASSWORD = Globals.db_password;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session;
        ResultSet rs;
        Statement s;
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        response.setContentType("text/html");
        String remoteAddr = "";
        Boolean error = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(DB_SERVER, DB_USERNAME, DB_PASSWORD);
            s = connection.createStatement();
            s.executeQuery("SELECT * FROM users WHERE email='" + email + "'");
            rs = s.getResultSet();
            if (rs.next()) {
                String message = "Email \"" + email + "\" is already in use.";
                request.setAttribute("errorMessageEmail", message);
                System.out.println("Signup: " + message);
                error = true;
            }
            s = connection.createStatement();
            s.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
            rs = s.getResultSet();
            if (rs.next()) {
                String message = "Username \"" + username + "\" is already in use.";
                request.setAttribute("errorMessageUsername", message);
                System.out.println("Signup: " + message);
                error = true;
            }
            remoteAddr = request.getRemoteAddr();
            //ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
            //reCaptcha.setPrivateKey("6LezstoSAAAAAEE9lfB6TR2kEX81_peDt4n03K4l");
            //String challenge = request.getParameter("recaptcha_challenge_field");
            //String uresponse = request.getParameter("recaptcha_response_field");
            //ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
            /*if (!reCaptchaResponse.isValid()) { 
             print_wrong_once(error); 
             out.print("<h2 align=\"center\">Validation code is wrong.</h2>"); 
             error = 1; 
             }*/
            if (error) {
                rs.close();
                s.close();
                connection.close();
                request.setCharacterEncoding("UTF-8");
                RequestDispatcher rd = request.getRequestDispatcher(response.encodeURL("signup.jsp"));
                rd.forward(request, response);
            } else {
                s.executeUpdate("INSERT INTO users (`username`, `password`, `email`) VALUES ('" + username + "', '" + password + "', '" + email + "')");
                rs.close();
                s.close();
                connection.close();
                // User directory
                File dir = new File(DB_DIR + "/" + username);
                dir.mkdir();
                System.out.println("Signup: " + "Created directory at " + dir.getPath());
                
                // Sensing tasks directory
                dir = new File(DB_DIR + "/" + username + "/sensing");
                dir.mkdir();
                System.out.println("Signup: " + "Created directory at " + dir.getPath());
                
                // Privacy mechanisms directory
                dir = new File(DB_DIR + "/" + username + "/privacy");
                dir.mkdir();
                System.out.println("Signup: " + "Created directory at " + dir.getPath());
                
                session = request.getSession(true);
                session.setAttribute("username", request.getParameter("username"));
                response.sendRedirect(response.encodeRedirectURL("./main/home.jsp"));
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL("signup.jsp"));
    }
}
