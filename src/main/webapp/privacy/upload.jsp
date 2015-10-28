<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.www.server.Globals"%>
<%@page import="com.www.server.Utils"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            String serverUrl = Globals.server_url;
            String app = "privacy";
            Boolean error = false;
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage == null) {
                error = false;
            } else {
                error = true;
            }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=serverUrl + "/ppc.ico"%>" />
        <link rel="stylesheet" href="<%=serverUrl + "/style.css"%>" />
        <title>Upload - <%=session.getAttribute("username")%> - <%=Globals.logo + ": " + app%></title>
    </head>
    <body>
        <div id="header">
            <a id="logo" href="<%=serverUrl + "/privacy/home.jsp"%>"><%=Globals.logo%></a>
            <div id="user_menu">
                <ul>
                    <li class="top" title="User menu"><%=session.getAttribute("username")%></li>
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
            <div id="app">Privacy</div>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/" + app + "/upload"%>">
                <input type="submit" id="upload_new_button" value="NEW">
            </form>
        </div>
        <div id="upload_pm">
            <form class="form" action="<%=serverUrl + "/" + app + "/upload"%>" method="post" enctype="multipart/form-data">
                <% if (error) {%>
                <div id="upload_message">
                    <value id="upload_message_value"><%=errorMessage%></value>
                </div>
                <% }%>
                <input class="name" name="name" type="text" placeholder="Name" title="Enter a name" required>
                <textarea class="description" name="description" placeholder="Description" title="Enter a description" required></textarea>
                <div class="sensing_task">
                    <select class="id" name="sensing_task" title="Select the target Sensing Task" onchange="select(this)" required>
                        <option value="" disabled selected>Select the target Sensing Task</option>
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
                                            String xml = dir + "/" + id + ".xml";
                                            out.println(
                                                    "<option value=\"" + Utils.getText(xml, "id") + "\">"
                                                    + Utils.getText(xml, "id") + ". "
                                                    + Utils.getText(xml, "name") + ": "
                                                    + Utils.getText(xml, "comment") + ""
                                                    + " submitted by " + Utils.getText(xml, "username") + ""
                                                    + " on " + Utils.getText(xml, "date") + " at " + Utils.getText(xml, "time")
                                                    + "</option>"
                                            );
                                        }
                                    }
                                    rs.close();
                                }
                            } catch (ClassNotFoundException | SQLException ex) {
                                System.out.println(app + "/upload: " + ex.getMessage());
                            }
                        %>
                    </select>
                    <script>
                        function select(sensing_task) {
                            //                        alert(sensing_task.options[sensing_task.selectedIndex].value);
                            document.getElementById("sensing_task_info").href = '<%=Globals.server_url%>' + "/webresources/tasks/" + sensing_task.options[sensing_task.selectedIndex].value + "/getsrc";
                        }
                    </script>
                    <a id="sensing_task_info" href="<%=Globals.server_url + "/webresources/tasks/getlist"%>" title="View more information about the Sensing Tasks">More info</a>
                </div>
                <input class="code" name="code" type="file" accept=".java" title="Choose the source code file"required/>
                <input id="upload_ok_button" type="submit" value="OK"/>
            </form>
            <div id="footer" style="border: none; margin: 0px;">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
            <form action="<%=serverUrl + "/privacy/home.jsp"%>">
                <input id="upload_cancel_button" type="submit" value="CANCEL"/>
            </form>
        </div>
    </body>
</html>
