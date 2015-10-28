<%@page import="com.www.server.Globals"%>
<%@page import="java.util.Calendar"%>
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
<%@page language="java" %>
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
            String username = (String) session.getAttribute("username");
            String app = "sensing";

            String fileId = request.getParameter("id");
            String fileNameClient = "", fileCodeClient = "", clientLog = "";
            String fileNameServer = "", fileCodeServer = "", libNameServer = "";
            Boolean isCompiled = false;
            Boolean isCompiledClient = false;
            Boolean isCompiledServer = false;
            Boolean hasMap = false;
            Boolean hasLog = false;
            Boolean hasLibServer = false;
            double sw_lat = 0, sw_lng = 0, ne_lat = 0, ne_lng = 0;
            Boolean hasServer = false;
            String compileDate = "";
            String xmlUrl = dbDir + "/" + username + "/sensing/" + fileId + "/" + fileId + ".xml";
            Utils.setText(xmlUrl, "new", "no");
            File xmlFile = new File(xmlUrl);
            fileNameClient = Utils.getText(xmlUrl, "client", "name");
            if ("on".equals(Utils.getText(xmlUrl, "client", "map", "status"))) {
                hasMap = true;
                sw_lat = Double.parseDouble(Utils.getText(xmlUrl, "client", "map", "sw_lat"));
                sw_lng = Double.parseDouble(Utils.getText(xmlUrl, "client", "map", "sw_lng"));
                ne_lat = Double.parseDouble(Utils.getText(xmlUrl, "client", "map", "ne_lat"));
                ne_lng = Double.parseDouble(Utils.getText(xmlUrl, "client", "map", "ne_lng"));
            }
            File fileUrlClient = new File(dbDir + "/" + username + "/sensing/" + fileId + "/client/" + fileNameClient);
            File fileDirServer = new File(dbDir + "/" + username + "/sensing/" + fileId + "/server");
            File fileUrlServer;
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            DataInputStream dis = null;
            if (fileDirServer.exists()) {
                hasServer = true;
                fileNameServer = Utils.getText(xmlUrl, "server", "name");
                fileUrlServer = new File(dbDir + "/" + username + "/sensing/" + fileId + "/server/" + fileNameServer);
                fis = new FileInputStream(fileUrlServer);
                bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);
                while (dis.available() != 0) {
                    fileCodeServer += (dis.readLine()) + "\n";
                }
            } else {
                hasServer = false;
            }
            fis = new FileInputStream(fileUrlClient);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            while (dis.available() != 0) {
                fileCodeClient += (dis.readLine()) + "\n";
            }
            Utils.close(dis);
            Utils.close(bis);
            Utils.close(fis);
            File dir = new File(dbDir + "/" + username + "/sensing/" + fileId);
            File[] listOfFiles = dir.listFiles();
            if ("true".equals(Utils.getText(xmlUrl, "client", "compile", "status"))) {
                isCompiledClient = true;
                isCompiled = true;
            } else {
                isCompiledClient = false;
                isCompiled = false;
            }
            if (hasServer) {
                if ("true".equals(Utils.getText(xmlUrl, "server", "compile", "status"))) {
                    isCompiledServer = true;
                } else {
                    isCompiledServer = false;
                    isCompiled = false;
                }
            }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=serverUrl + "/ppc.ico"%>" />
        <link rel="stylesheet" href="<%=serverUrl + "/style.css"%>" />
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
        <title><%=fileNameClient%> - <%=session.getAttribute("username")%> - <%=Globals.logo + ": " + app%></title>
        <script  type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                refresh();
            });
            function refresh() {
                setTimeout(function() {
                    var id = getRequestParameter('id');
                    $('#top').load('edit.jsp?id=' + id + ' #top');
                    $('#view_client_label').load('edit.jsp?id=' + id + ' #view_client_label');
                    $('#view_client_log').load('edit.jsp?id=' + id + ' #view_client_log_data');
                    refresh();
                }, 3000);
            }
            function getRequestParameter(sParam) {
                var sPageURL = window.location.search.substring(1);
                var sURLVariables = sPageURL.split('&');
                for (var i = 0; i < sURLVariables.length; i++)
                {
                    var sParameterName = sURLVariables[i].split('=');
                    if (sParameterName[0] == sParam)
                    {
                        return sParameterName[1];
                    }
                }
            }
        </script>
    </head>
    <body id="edit">
        <div id="header">
            <a id="logo" href="<%=serverUrl + "/main/home.jsp"%>"><%=Globals.logo%></a>
            <!--
            <div id="user_menu">
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
            </div>
            -->
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
            <div id="app">Sensing</div>
            <% if (isCompiled) {%>
            <form id="view_edit_status" action="<%=serverUrl + "/editstatus"%>" method="post">
                <input type="hidden" id="jsp_name" name="jsp_name" value="edit">
                <input type="hidden" id="view_file_id" name="view_file_id" value=<%=fileId%>>
                <% if ("pause".equals(Utils.getText(xmlUrl, "status"))) {%>
                <input type="submit" id="view_start_button" name="view_status_button" value="START">
                <input type="submit" id="view_stop_button" name="view_status_button" value="STOP">
                <% } else if ("start".equals(Utils.getText(xmlUrl, "status"))) {%>
                <input type="submit" id="view_pause_button" name="view_status_button" value="PAUSE">
                <input type="submit" id="view_stop_button" name="view_status_button" value="STOP">
                <% } else if ("stop".equals(Utils.getText(xmlUrl, "status"))) {%>
                <p id="top_status">Stopped.</p>
                <% } else {%>
                <p id="top_status">Pending...
                    <input type="hidden" name="view_status_button" value="STOP">
                    <a onclick="document.getElementById('view_edit_status').submit()">Cancel</a>.
                </p>
                <% }%>
            </form>
            <% }%>
            <button id="edit_cancel_button" onclick="location.href = '<%=serverUrl + "/main/view.jsp?id=" + fileId%>'">CANCEL</button>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/upload"%>">
                <input type="submit" id="view_new_button" value="NEW">
            </form>
        </div>
        <div id="view_file">
            <div id="view_file_attributes">
                <div id="view_file_details">
                    <label id="view_filename_label" name="view_filename_label">Filename</label>
                    <value id="view_filename_value" name="view_filename_value"><%=Utils.getText(xmlUrl, "name")%></value>
                    <label id="view_comment_label" name="view_comment_label">Comment</label>
                    <value id="view_comment_value" name="view_comment_value"><%=Utils.getText(xmlUrl, "comment")%></value>
                    <label id="view_size_label" name="view_size_label">Size</label>
                    <value id="view_size_value" name="view_size_value"><%=Utils.getText(xmlUrl, "size")%> </value>
                </div>
                <div id="view_file_date" name="view_date">
                    <label id="view_date_label" name="view_date_label">Date</label>
                    <value id="view_date_value" name="view_date_value"><%=Utils.getText(xmlUrl, "date")%></value>
                    <label id="view_time_label" name="view_time_label">Time</label>
                    <value id="view_time_value" name="view_time_value"><%=Utils.getText(xmlUrl, "time")%></value>
                </div>
            </div>
            <div id="view_file_code">
                <form action="../editprop" method="post">
                    <input hidden id="user_name" name="user_name" value=<%=username%>>
                    <input hidden id="file_id" name="file_id" value=<%=fileId%>>
                    <input hidden id="client_name" name="client_name" value=<%=fileNameClient%>>
                    <input hidden id="server_name" name="server_name" value=<%=fileNameServer%>>
                    <input hidden id="client_compiled" name="client_compiled" value=<%=isCompiledClient%>>
                    <input hidden id="server_compiled" name="server_compiled" value=<%=isCompiledServer%>>
                    <div id="view_file_client">
                        <div id="view_client_code">
                            <label id="view_client_label">
                                Client
                                <% if (!Utils.getText(xmlUrl, "status").isEmpty()) {%>
                                <span id="view_client_devices">downloaded by <%=Utils.getText(xmlUrl, "client", "downloaded", "counter")%> device(s)</span>
                                <% }%>
                            </label>
                            <input id="view_client_checkbox" name="view_client_checkbox" type="checkbox">
                            <p id="view_client_checkbox_label">Source</p>
                            <value id="view_client_value" name="view_client_value">
                                <textarea code readonly><%=fileCodeClient%></textarea>
                            </value>
                        </div>
                        <% if ("on".equals(Utils.getText(xmlUrl, "client", "time", "status"))) {%>
                        <div id="view_time">
                            <input type="hidden" id="view_time_on" name="view_time_on" checked>
                            <input type="checkbox" id="view_time_checkbox" name="view_time_checkbox" checked disabled>
                            <p id="view_time_checkbox_label">Time</p>
                            <div id="view_time_selection">
                                <select id="view_time_selection_from" name="view_time_selection_from">
                                    <%
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(sdf.parse("00:00"));
                                        do {
                                            if (sdf.format(cal.getTime()).equals(Utils.getText(xmlUrl, "client", "time", "from"))) {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "' selected>" + sdf.format(cal.getTime()) + "</option>");
                                            } else {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "'>" + sdf.format(cal.getTime()) + "</option>");
                                            }
                                            cal.add(Calendar.MINUTE, 30);
                                        } while (!sdf.format(cal.getTime()).equals("00:00"));
                                        /**/
                                    %>
                                </select>
                                -
                                <select id="view_time_selection_to" name="view_time_selection_to">
                                    <%
                                        do {
                                            if (sdf.format(cal.getTime()).equals(Utils.getText(xmlUrl, "client", "time", "to"))) {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "' selected>" + sdf.format(cal.getTime()) + "</option>");
                                            } else {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "'>" + sdf.format(cal.getTime()) + "</option>");
                                            }
                                            cal.add(Calendar.MINUTE, 30);
                                        } while (!sdf.format(cal.getTime()).equals("00:00"));
                                    %>
                                </select>
                            </div>
                        </div>
                        <% }%>
                        <% if ("on".equals(Utils.getText(xmlUrl, "client", "map", "status"))) {%>
                        <div id="view_map">
                            <input type="hidden" id="view_map_on" name="view_map_on" checked>
                            <input type="checkbox" id="view_map_checkbox" name="view_map_checkbox" checked disabled>
                            <p id="view_map_checkbox_label">Location</p>
                            <div id="view_map_canvas"></div>
                            <script type="text/javascript">
                                var cntr = new google.maps.LatLng(0, 0);
                                var sw = new google.maps.LatLng(<%=sw_lat%>, <%=sw_lng%>);
                                var ne = new google.maps.LatLng(<%=ne_lat%>, <%=ne_lng%>);

                                //var bounds = new google.maps.LatLngBounds();
                                //bounds.extend(sw);
                                //bounds.extend(ne);
                                var angle = <%=ne_lng%> - <%=sw_lng%>;
                                if (angle < 0) {
                                    angle += 360;
                                }
                                var zoom = Math.round(Math.log(360 * 360 / angle / 256) / Math.LN2);
                                var bounds = new google.maps.LatLngBounds(sw, ne);
                                var canvas = document.getElementById('view_map_canvas');
                                var options = {
                                    disableDefaultUI: true,
                                    navigationControl: false,
                                    mapTypeControl: false,
                                    scaleControl: false,
                                    draggable: true,
                                    mapTypeId: google.maps.MapTypeId.ROADMAP
                                };
                                var map = new google.maps.Map(canvas, options);
                                map.fitBounds(bounds);
                                var i = 0;
                                google.maps.event.addDomListener(map, 'idle', function() {
                                    if (i++ === 0) {
                                        map.setZoom(zoom);
                                    }
                                });
                                google.maps.event.addDomListener(map, 'dragend', function() {
                                    var bounds = map.getBounds();
                                    document.getElementById('view_map_sw_lat').value = bounds.getSouthWest().lat();
                                    document.getElementById('view_map_sw_lng').value = bounds.getSouthWest().lng();
                                    document.getElementById('view_map_ne_lat').value = bounds.getNorthEast().lat();
                                    document.getElementById('view_map_ne_lng').value = bounds.getNorthEast().lng();
                                });
                            </script>
                            <label hidden for="view_map_sw_lat"></label>
                            <input hidden id="view_map_sw_lat" name="view_map_sw_lat" type="text" value='<%=sw_lat%>' />
                            <label hidden for="view_map_sw_lng"></label>
                            <input hidden id="view_map_sw_lng" name="view_map_sw_lng" type="text" value='<%=sw_lng%>' />
                            <label hidden for="view_map_ne_lat"></label>
                            <input hidden id="view_map_ne_lat" name="view_map_ne_lat" type="text" value='<%=ne_lat%>' />
                            <label hidden for="view_map_ne_lng"></label>
                            <input hidden id="view_map_ne_lng" name="view_map_ne_lng" type="text" value='<%=ne_lng%>' />
                        </div>
                        <% }%>
                        <%if (isCompiled) { %>
                        <div id="view_log">
                            <input type="checkbox" id="view_client_log_checkbox" name="view_client_log_checkbox">
                            <label id="view_client_log_label" name=="view_client_log_label">Log</label>
                            <div id="view_client_log">
                                <div id="view_client_log_data">
                                    <%if (!Utils.getText(xmlUrl, "client", "log").isEmpty()) {%>
                                    <div id="view_client_log_options">
                                        : 
                                        <a id="view_client_log_download" href='<%=serverUrl + "/webresources/tasks/" + fileId + "/getdata"%>'>Download</a> | 
                                        <a id="view_client_log_delete" href='<%=serverUrl + "/webresources/tasks/" + fileId + "/deletedata"%>'>Delete</a> | 
                                        <a id="view_client_log_downdel" href='<%=serverUrl + "/webresources/tasks/" + fileId + "/getdata/delete"%>'>Download & Delete</a> 
                                    </div>
                                    <%
                                        List list = Utils.getNodeList(xmlUrl, "client", "log", "data");
                                        for (int i = 0; i < list.size(); i++) {
                                            Element element = (Element) list.get(i);
                                    %>
                                    <div id="view_client_log_element">
                                        <div id="view_client_log_value">
                                            <%="[" + element.getChildText("date") + " " + element.getChildText("time") + "]" + " @ "%>
                                            <span title='<%="Model: " + Utils.getDeviceInfo(element.getChildText("device"), "model") + "\nOS: " + Utils.getDeviceInfo(element.getChildText("device"), "os")%>'>
                                                <%=element.getChildText("device")%>
                                            </span>
                                        </div>
                                    </div>
                                    <% } %>

                                    <% } else { %>
                                    <div>Empty.</div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% if (hasServer) {%>
                    <div id="view_file_server">
                        <% if (isCompiledServer) {%>
                        <div id="view_server_code">
                            <label id="view_server_label">Server</label>
                            <input id="view_server_checkbox" name="view_server_checkbox" type="checkbox">
                            <p id="view_server_checkbox_label">Source</p>
                            <value id="view_server_value" name="view_server_code_value">
                                <% if (hasLibServer) {%>
                                <div id="view_server_lib">
                                    <label id="view_server_lib_label">Library:</label>
                                    <value id="view_server_lib_value"><%=libNameServer%></value>
                                </div>
                                <% }%>
                                <textarea code readonly><%=fileCodeServer%></textarea>
                            </value>
                        </div>
                        <% }%>

                    </div>
                    <% }%>
                    <input type="submit" id="edit_ok_button" name="edit_ok_button" value="OK" />
                </form>
                <div id="footer" style="margin-top: 0px; border: none">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
            </div>
        </div>
    </body>
</html>