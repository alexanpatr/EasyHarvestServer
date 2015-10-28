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
//            if (!Utils.stringIsEmpty(compLog)) {
//                response.sendRedirect(response.encodeRedirectURL("/Server/" + app + "/edit.jsp?id=" + pmId));
//            }
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
    <body id="view_pm">
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
                <input type="hidden" name="jsp_name" value="view">
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
            <%} else {%>
            <p id="top_status">Something went wrong...</p>
            <%}%>
            <%if (!"stop".equals(pmStatus)) {%>
            <button id="view_edit_button" onclick="location.href = '<%=serverUrl + "/" + app + "/edit.jsp?id=" + pmId%>'">EDIT</button>
            <%}%>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/" + app + "/" + "upload"%>">
                <%if (Utils.stringIsEmpty(compLog)) {%>
                <input id="view_file_on" type="checkbox" hidden checked>
                <%}%>
                <input type="submit" id="view_new_button" value="NEW">
            </form>
        </div>
        <div class="pm">
            <form action="<%=serverUrl + "/" + app + "/edit"%>" method="post" enctype="multipart/form-data">
                <input type="hidden" name="pm_id" value=<%=pmId%>>
                <div class="details">
                    <div class="info">
                        <div class="left">
                            <value class="name"><%=pmName%></value>
                            <div class="st">
                                for
                                <a href="<%=Globals.server_url + "/webresources/tasks/" + stId + "/getsrc"%>" title="View more information about the Sensing Task"><%=stName + " (" + stId + ")"%></a>
                            </div>
                            <%if (Integer.parseInt(pmVersion) == 0) {%>
                            <value class="version"><br></value>
                                <%} else {%>
                            <value class="version">version <%=pmVersion%>.0</value>
                                <%}%>
                        </div>
                        <div class="right">
                            <value class="date"><%=pmDate%></value>
                            <value class="time"><%=pmTime%></value>
                        </div>
                    </div>
                    <div class="description">
                        <value><%=pmDesc%></value>
                    </div>
                </div>
                <div class="source">
                    <textarea class="code" name="code" code readonly><%=srcCode%></textarea>
                    <%if (!Utils.stringIsEmpty(compLog)) {%>
                    <textarea class="error" readonly><%=compDate + " " + compTime + " compilation error:\n" + compLog%></textarea>
                    <%}%>
                </div>
            </form>
            <div id="footer" style="margin-top: 0px; border: none">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
        </div>
    </div>
</body>
</html>