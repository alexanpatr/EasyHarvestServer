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

            try {

                if (fileDirServer.exists()) {
                    hasServer = true;
                    fileNameServer = Utils.getText(xmlUrl, "server", "name");
                    fileUrlServer = new File(dbDir + "/" + username + "/sensing/" + fileId + "/server/" + fileNameServer);
                    fis = new FileInputStream(fileUrlServer);
                    bis = new BufferedInputStream(fis);
                    dis = new DataInputStream(bis);
//                dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fileUrlServer)));
                    while (dis.available() != 0) {
                        fileCodeServer += (dis.readLine()) + "\n";
                    }
                    Utils.close(dis);
                    Utils.close(bis);
                    Utils.close(fis);
                } else {
                    hasServer = false;
                }
            } catch (IOException e) {

            }

            try {

                fis = new FileInputStream(fileUrlClient);
                bis = new BufferedInputStream(fis);
                dis = new DataInputStream(bis);
//            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fileUrlClient)));
                while (dis.available() != 0) {
                    fileCodeClient += (dis.readLine()) + "\n";
                }
                Utils.close(dis);
                Utils.close(bis);
                Utils.close(fis);
            } catch (IOException e) {

            }

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
        <% if (!Globals.DBG) {%>
        <script type="text/javascript">
            $(document).ready(function () {
                refresh();
            });
            function refresh() {
                setTimeout(function () {
                    var id = getRequestParameter('id');
                    $('#top').load('view.jsp?id=' + id + ' #top');
                    $('#view_client_label').load('view.jsp?id=' + id + ' #view_client_label');
                    $('#view_client_log').load('view.jsp?id=' + id + ' #view_client_log_data');
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
        <% }%>
    </head>
    <body id="view">
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
                <input type="hidden" id="jsp_name" name="jsp_name" value="view">
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
                <p id="top_status">Sending to device(s)...
                    <input type="hidden" name="view_status_button" value="STOP">
                    <a onclick="document.getElementById('view_edit_status').submit()">Cancel</a>.
                </p>
                <% }%>
            </form>
            <%if (!"stop".equals(Utils.getText(xmlUrl, "status"))) {%>
            <button id="view_edit_button" onclick="location.href = '<%=serverUrl + "/main/edit.jsp?id=" + fileId%>'">EDIT</button>
            <% }%>
            <% }%>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/upload"%>">
                <% if (isCompiled) {%>
                <input id="view_file_on" type="checkbox" hidden checked>
                <% }%>
                <input type="submit" id="view_new_button" value="NEW">
            </form>
        </div>
        <div id="view_file">
            <div id="view_file_attributes">
                <div id="view_file_details">
                    <label id="view_filename_label" name="view_filename_label">Filename</label>
                    <a href="<%=serverUrl + "/webresources/tasks/" + fileId + "/getsrc"%>" title="Get task source code">
                        <value id="view_filename_value" name="view_filename_value">
                            <%=Utils.getText(xmlUrl, "name")%>
                        </value>
                    </a>
                    <label id="view_comment_label" name="view_comment_label">Comment</label>
                    <value id="view_comment_value" name="view_comment_value"><%=Utils.getText(xmlUrl, "comment")%></value>
                    <label id="view_size_label" name="view_size_label">Size</label>
                    <value id="view_size_value" name="view_size_value"><%=Utils.getText(xmlUrl, "client", "size")%> </value>
                </div>
                <div id="view_file_date" name="view_date">
                    <label id="view_date_label" name="view_date_label">Date</label>
                    <value id="view_date_value" name="view_date_value"><%=Utils.getText(xmlUrl, "date")%></value>
                    <label id="view_time_label" name="view_time_label">Time</label>
                    <value id="view_time_value" name="view_time_value"><%=Utils.getText(xmlUrl, "time")%></value>
                </div>
            </div>
            <div id="view_file_code">
                <form action="../edit" method="post" enctype="multipart/form-data">
                    <input id="file_id" name="file_id" hidden value=<%=fileId%>>
                    <input id="client_name" name="client_name" hidden value=<%=fileNameClient%>>
                    <input id="server_name" name="server_name" hidden value=<%=fileNameServer%>>
                    <input id="client_compiled" name="client_compiled" hidden value=<%=isCompiledClient%>>
                    <input id="server_compiled" name="server_compiled" hidden value=<%=isCompiledServer%>>
                    <div id="view_file_client">
                        <% if (isCompiledClient) {%>
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
                            <input type="checkbox" id="view_time_checkbox" name="view_time_checkbox">
                            <p id="view_time_checkbox_label">Time</p>    
                            <div id="view_time_selection"><%=Utils.getText(xmlUrl, "client", "time", "from")%> - <%=Utils.getText(xmlUrl, "client", "time", "to")%></div>
                        </div>
                        <% }%>
                        <% if (hasMap) {%>
                        <div id="view_map">
                            <input type="hidden" id="view_map_on" name="view_map_on" checked>
                            <input type="checkbox" id="view_map_checkbox" name="view_map_checkbox">
                            <p id="view_map_checkbox_label">Location</p>
                            <div id="view_map_canvas"></div>
                            <script type="text/javascript">
                                var cntr = new google.maps.LatLng(0, 0);
                                var sw = new google.maps.LatLng(<%=sw_lat%>, <%=sw_lng%>);
                                var ne = new google.maps.LatLng(<%=ne_lat%>, <%=ne_lng%>);
                                var angle = <%=ne_lng%> - <%=sw_lng%>;
                                if (angle < 0) {
                                    angle += 360;
                                }
                                var zoom = Math.round(Math.log(360 * 360 / angle / 256) / Math.LN2);
                                var bounds = new google.maps.LatLngBounds(sw, ne);
                                var options = {
                                    disableDefaultUI: true,
                                    disableDoubleClickZoom: true,
                                    scrollwheel: false,
                                    navigationControl: false,
                                    mapTypeControl: false,
                                    scaleControl: false,
                                    draggable: false,
                                    mapTypeId: google.maps.MapTypeId.ROADMAP
                                };
                                var canvas = document.getElementById('view_map_canvas');
                                var map = new google.maps.Map(canvas, options);
                                map.fitBounds(bounds);
                                google.maps.event.addDomListener(map, 'idle', function () {
                                    map.setZoom(zoom);
                                });
                                document.getElementById("view_map_checkbox").onclick = function () {
                                    if (this.checked) {
                                        cntr = map.getCenter();
                                        google.maps.event.trigger(map, 'resize');
                                        map.setCenter(cntr);
                                    }
                                };
                            </script>
                        </div>
                        <% }%>
                        <% } else {%>
                        <div id="view_client_code">
                            <label id="view_client_label">Client</label>
                            <input id="view_client_checkbox" name="view_client_checkbox" type="checkbox" hidden checked>
                            <value id="view_client_value" name="view_client_value">
                                <textarea id="client_code" name="client_code" code><%=fileCodeClient%></textarea>
                            </value>
                        </div>
                        <div id="view_client_compile" name="view_client_compile">
                            <label id="view_client_compile_label" name="view_compile_label">Compilation error:</label>
                            <value id="view_client_compile_value" name="view_compile_value">
                                <textarea readonly><%=Utils.getText(xmlUrl, "client", "compile", "output")%></textarea>
                            </value>
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
                                        try {
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
                                    <%
                                            }
                                        } catch (Exception e) {
                                        }
                                    %>

                                    <% } else { %>
                                    <div>Empty.</div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
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
                <% } else {%>
                <div id="view_server_code">
                    <label id="view_server_label">Server</label>
                    <input id="view_server_checkbox" name="view_server_checkbox" type="checkbox" hidden checked>
                    <% if (hasLibServer) {%>
                    <div id="view_server_lib">
                        <label id="view_server_lib_label">Library:</label>
                        <value id="view_server_lib_value"><%=libNameServer%></value>
                    </div>
                    <% }%>
                    <value id="view_server_value" name="view_server_value">
                        <textarea id="server_code" name="server_code" code><%=fileCodeServer%></textarea>
                    </value>
                </div>
                <div id="view_server_compile" name="view_compile">
                    <label id="view_server_compile_label" name="view_server_compile_label">Compilation error:</label>
                    <value id="view_server_compile_value" name="view_server_compile_value">
                        <textarea readonly><%=Utils.getText(xmlUrl, "server", "compile", "output")%></textarea>
                    </value>
                </div>
                <% }%>
            </div>
            <% }%>
            <% if (!isCompiled) {%>
            <input type="submit" id="view_ok_button" name="view_ok_button" value="OK" />
            <% }%>
        </form>
        <% if (!isCompiled) {%>
        <form action="<%=serverUrl + "/main/home.jsp"%>">
            <input id="view_cancel_button" type="submit" value="CANCEL" />
        </form>
        <% }%>
        <div id="footer" style="margin-top: 0px; border: none">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
    </div>
</div>
</body>
</html>