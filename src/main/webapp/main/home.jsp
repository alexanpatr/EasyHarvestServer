<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.www.server.Globals"%>
<%@page import="com.www.server.Utils"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.jdom.input.SAXBuilder"%>
<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.Document"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.io.File"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            String serverUrl = Globals.server_url;
            String dbDir = Globals.db_dir;
            String username = (String) session.getAttribute("username");
            String app = "sensing";
            /*
             * Get the sorted list of sensing tasks.
             */
            File appDir = new File(dbDir + "/" + username + "/" + app);
            File[] listOfFiles = null;
            if (appDir.exists()) {
                listOfFiles = appDir.listFiles();
                Arrays.sort(listOfFiles, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Integer.valueOf(f1.getName()).compareTo(Integer.valueOf(f2.getName()));
                    }
                });
            }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=serverUrl + "/ppc.ico"%>" />
        <link rel="stylesheet" href="<%=serverUrl + "/style.css"%>" />
        <title><%="Home - " + username + " - " + Globals.logo + ": " + app%></title>
        <script  type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
        <% if (!Globals.DBG) {%>
        <!-- Refresh -->
        <script type="text/javascript">
            $(document).ready(function () {
                refresh();
            });
            function refresh() {
                setTimeout(function () {
                    $('#home').load('home.jsp #home_content');
                    refresh();
                }, 3000);
            }
        </script>
        <% }%>
    </head>
    <body>
        <div id="header">
            <a id="logo" href="<%=serverUrl + "/main/home.jsp"%>"><%=Globals.logo%></a>
            <div id="user_menu">
                <ul>
                    <li class="top" title="User menu"><%=username%></li>
                    <li class="item"><a href="<%=serverUrl + "/signout"%>">Sign out</a></li>
                </ul>
                <!--
                <div id="user_menu_username" name="user_menu_username">
                    <a>
                        <value><%=username%></value>
                    </a>
                </div>
                <div id="user_menu_signout" name="user_menu_signout">
                    <a href="<%=serverUrl + "/signout"%>">
                        <value>Sign out</value>
                    </a>
                </div>
                -->
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
            <div id="app">Sensing</div>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/upload"%>">
                <input type="submit" id="home_new_button" value="NEW">
            </form>
        </div>
        <div id="home">
            <div id="home_content">
                <%
                    if (listOfFiles != null && listOfFiles.length > 0) {
                        for (File file : listOfFiles) {
                            if (file.isDirectory()) {
                                String id = file.getName();
                                String xmlFile = appDir + "/" + id + "/" + id + ".xml";
                                String name = Utils.getText(xmlFile, "name");
                                String hasNew = Utils.getText(xmlFile, "new");
                                String counter = Utils.getText(xmlFile, "counter");
                                String comment = Utils.getText(xmlFile, "comment");
                                String date;
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                if ((Utils.getText(xmlFile, "date")).equals(dateFormat.format(new Date()))) {
                                    date = Utils.getText(xmlFile, "time");
                                } else {
                                    date = Utils.getText(xmlFile, "date");
                                }
                %>
                <% if ("yes".equals(hasNew)) {%>
                <a id="home_task" style="background-color: white;" href="<%=serverUrl + "/main/view.jsp?id=" + id%>">
                    <div id="home_task_name">
                        <%
                            if (Integer.valueOf(counter) > 0) {
                                out.print(name + "(" + counter + ")");
                            } else {
                                out.print(name);
                            }
                        %>
                    </div>
                    <div id="home_task_comment"><%=comment%></div>
                    <div id="home_task_date"><%=date%></div>
                </a>
                <% } else {%>
                <a id="home_task" href="<%=serverUrl + "/main/view.jsp?id=" + id%>">
                    <div id="home_task_name">
                        <%
                            if (Integer.valueOf(counter) > 0) {
                                out.print(name + "(" + counter + ")");
                            } else {
                                out.print(name);
                            }
                        %>
                    </div>
                    <div id="home_task_comment"><%=comment%></div>
                    <div id="home_task_date"><%=date%></div>
                </a>
                <%}
                        }
                    }
                } else {%>
                <div id="home_empty">No tasks to view. Click on <a href="<%=serverUrl + "/upload"%>">NEW</a> to begin...</div>
                <%}%>
                <div id="footer" style="border: none; margin: 0px;">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
            </div>
        </div>
    </body>
</html>
