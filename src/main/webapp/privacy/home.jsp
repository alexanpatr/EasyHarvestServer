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
            String app = "privacy";
            /*
             * Get the sorted list of privacy mechanisms.
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
    </head>
    <body>
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
            <div id="app">Privacy</div>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/" + app + "/upload"%>">
                <input type="submit" id="home_new_button" value="NEW">
            </form>
        </div>
        <div id="home_pm">
            <div class="content">
                <%
//                    listOfFiles = null;
                    if (listOfFiles != null && listOfFiles.length > 0) {
                        for (File file : listOfFiles) {
                            if (file.isDirectory()) {
                                String id = file.getName();
                                String xmlFile = appDir + "/" + id + "/" + id + ".xml";
                                String pmName = Utils.getText(xmlFile, "name");
                                String version = Utils.getText(xmlFile, "version");
                                String description = Utils.getText(xmlFile, "description");
//                                int n = 40;
//                                if (description.length() > n) {
//                                    description = description.substring(0, n) + "...";
//                                }
                                String date;
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                if ((Utils.getText(xmlFile, "date")).equals(dateFormat.format(new Date()))) {
                                    date = Utils.getText(xmlFile, "time");
                                } else {
                                    date = Utils.getText(xmlFile, "date");
                                }
                                String stId = Utils.getText(xmlFile, "sensing");
                                String stName = Utils.getText(Utils.getDir("sensing", stId) + "/" + stId + ".xml", "name");
                                String name = pmName + " v" + version + ".0" + " for " + stName + " (" + stId + ")";
                %>
                <a class="item" href="<%=serverUrl + "/" + app + "/view.jsp?id=" + id%>" title="<%=name%>">
                    <div class="name"><%=name%></div>
                    <div class="description"><%=description%></div>
                    <div class="date"><%=date%></div>
                </a>
                <%}%>
                <%}%>
                <%} else {%>
                <div id="home_empty">No mechanisms to view. Click on <a href="<%=serverUrl + "/" + app + "/upload"%>">NEW</a> to begin...</div>
                <%}%>
                <div id="footer">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
            </div>
        </div>
    </body>
</html>
