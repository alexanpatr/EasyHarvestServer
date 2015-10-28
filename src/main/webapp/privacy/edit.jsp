<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page language="java" %>
<%@page import="com.www.server.Globals"%>
<%@page import="com.www.server.Utils"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="org.w3c.dom.*, javax.xml.parsers.*" %>
<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.jdom.Document" %>
<%@page import="org.jdom.Element" %>
<%@page import="org.jdom.JDOMException" %>
<%@page import="org.jdom.input.SAXBuilder" %>
<%@page import="java.io.IOException" %>
<%@page import="java.lang.*" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            String serverUrl = Globals.server_url;
            String dbDir = Globals.db_dir;
            String app = "privacy";
            String username = (String) session.getAttribute("username");

            Boolean error = false;
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage == null) {
                error = false;
            } else {
                error = true;
            }

            String pmId = request.getParameter("id");
            String xmlUrl = dbDir + "/" + username + "/" + app + "/" + pmId + "/" + pmId + ".xml";
            String pmName = Utils.getText(xmlUrl, "name");
            String pmVersion = Utils.getText(xmlUrl, "version");
            String pmDate = Utils.getText(xmlUrl, "date");
            String pmTime = Utils.getText(xmlUrl, "time");
            String pmDesc = Utils.getText(xmlUrl, "description");
            String stId = Utils.getText(xmlUrl, "sensing");
            String stName = Utils.getText(Utils.getDir("sensing", stId) + "/" + stId + ".xml", "name");
            String pmStatus = Utils.getText(xmlUrl, "status");
            String srcName = Utils.getText(xmlUrl, "source", "name");
            String compDate = Utils.getText(xmlUrl, "source", "compile", "date");
            String compTime = Utils.getText(xmlUrl, "source", "compile", "time");
            String compLog = Utils.getText(xmlUrl, "source", "compile", "log");
            /*
             * Get the source code.
             */
            File srcUrl = new File(dbDir + "/" + username + "/" + app + "/" + pmId + "/" + srcName);
            FileInputStream fis = new FileInputStream(srcUrl);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            String srcCode = "";
            while (dis.available() != 0) {
                srcCode += (dis.readLine()) + "\n";
            }
            Utils.close(dis);
            Utils.close(bis);
            Utils.close(fis);
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=serverUrl + "/ppc.ico"%>" />
        <link rel="stylesheet" href="<%=serverUrl + "/style.css"%>" />
        <title><%=pmName%> - <%=username%> - <%=Globals.logo + ": " + app%></title>
    </head>
    <body id="view">
        <div id="header">
            <a id="logo" href="<%=serverUrl + "/" + app + "/home.jsp"%>"><%=Globals.logo%></a>
            <div id="user_menu">
                <ul>
                    <li class="top" title="User menu"><%=username%></li>
                    <li class="item"><a href="<%=serverUrl + "/signout"%>">Sign out</a></li>
                </ul>
            </div>
            <div id="user_apps">
                <ul>
                    <li class="top" title="Apps"><img src="../img/apps.png"></li>
                    <li class="item"><a href="<%=serverUrl + "/main/home.jsp"%>">Sensing</a></li>
                    <li class="item"><a href="<%=serverUrl + "/privacy/home.jsp"%>">Privacy</a></li>
                </ul>
            </div>
        </div>
        <div id="top">
            <div id="app"><%=app%></div>
            <%if (Utils.stringIsEmpty(compLog)) {%>
            <form id="view_edit_status" action="<%=serverUrl + "/" + app + "/" + "editstatus"%>" method="post">
                <input type="hidden" name="jsp_name" value="edit">
                <input type="hidden" name="pm_id" value=<%=pmId%>>
                <%if ("pause".equals(pmStatus)) {%>
                <input type="submit" id="view_start_button" name="view_status_button" value="START">
                <input type="submit" id="view_stop_button" name="view_status_button" value="STOP">
                <%} else if ("start".equals(pmStatus)) {%>
                <input type="submit" id="view_pause_button" name="view_status_button" value="PAUSE">
                <input type="submit" id="view_stop_button" name="view_status_button" value="STOP">
                <%} else if ("stop".equals(pmStatus)) {%>
                <p id="top_status">Stopped.</p>
                <%} else {%>
                <p id="top_status">Something went wrong...</p>
                <%}%>
            </form>
            <%if (!"stop".equals(pmStatus)) {%>
            <button id="view_edit_button" onclick="location.href = '<%=serverUrl + "/" + app + "/edit.jsp?id=" + pmId%>'">EDIT</button>
            <%}%>
            <%} else {%>
            <p id="top_status">Something went wrong...</p>
            <%}%>
            <button id="edit_cancel_button" onclick="location.href = '<%=serverUrl + "/" + app + "/view.jsp?id=" + pmId%>'">CANCEL</button>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/" + app + "/" + "upload"%>">
                <%if (Utils.stringIsEmpty(compLog)) {%>
                <input id="view_file_on" type="checkbox" hidden checked>
                <%}%>
                <input type="submit" id="view_new_button" value="NEW">
            </form>
        </div>
        <div id="edit_pm">
            <form action="<%=serverUrl + "/" + app + "/edit"%>" method="post" enctype="multipart/form-data">
                <input type="hidden" name="pm_id" value=<%=pmId%>>

                <% if (error) {%>
                <div id="upload_message">
                    <value id="upload_message_value"><%=errorMessage%></value>
                </div>
                <% }%>
                <input class="name" name="name" type="text" placeholder="Name" required value="<%=pmName%>">
                <textarea class="description" name="description" required><%=pmDesc%></textarea>
                <div class="sensing_task">
                    <select class="id" name="sensing_task" title="Select the target Sensing Task" onchange="select(this)" required>
                        <option value="" disabled>Select the target Sensing Task</option>
                        <%
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                try (Connection c = DriverManager.getConnection(Globals.db_server, Globals.db_username, Globals.db_password)) {
                                    ResultSet rs;
                                    try (Statement s = c.createStatement()) {
                                        rs = s.executeQuery("SELECT * FROM tasks WHERE ready='YES'");
                                        while (rs.next()) {
                                            String id = rs.getString("id");
                                            String dir = Utils.getDir("sensing", id);
                                            String stXml = dir + "/" + id + ".xml";
                                            String option = "<option value=\"" + Utils.getText(stXml, "id") + "\"";
                                            if (stId.equals(id)) {
                                                option += " selected";
                                            }
                                            option += ">" + Utils.getText(stXml, "id") + ". "
                                                    + Utils.getText(stXml, "name") + ": "
                                                    + Utils.getText(stXml, "comment") + ""
                                                    + " submitted by " + Utils.getText(stXml, "username") + ""
                                                    + " on " + Utils.getText(stXml, "date") + " at " + Utils.getText(stXml, "time")
                                                    + "</option>";
                                            System.out.println(option);
                                            out.println(option);
                                        }
                                    }
                                    rs.close();
                                }
                            } catch (ClassNotFoundException | SQLException ex) {
                                System.out.println(app + "/edit: " + ex.getMessage());
                            }
                        %>
                    </select>
                    <script>
                        function select(sensing_task) {
                            //                        alert(sensing_task.options[sensing_task.selectedIndex].value);
                            document.getElementById("sensing_task_info").href = '<%=Globals.server_url%>' + "/webresources/tasks/" + sensing_task.options[sensing_task.selectedIndex].value + "/getsrc";
                        }
                    </script>
                    <a id="sensing_task_info" href="<%=Globals.server_url + "/webresources/tasks/" + stId + "/getsrc"%>" title="View more information about the Sensing Tasks">More info</a>
                </div>
                <div class="source">
                    <textarea class="code" name="code" code><%=srcCode%></textarea>
                    <%if (!Utils.stringIsEmpty(compLog)) {%>
                    <textarea class="error" readonly><%=compDate + " " + compTime + " compilation error:\n" + compLog%></textarea>
                    <%}%>
                </div>
                <input type="submit" id="edit_ok_button" name="edit_ok_button" value="OK" />
            </form>
            <div id="footer" style="margin-top: 0px; border: none">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
        </div>
    </div>
</body>
</html>