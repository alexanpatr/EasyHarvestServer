<%@page import="com.www.server.Globals"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <% 
            String serverUrl = Globals.server_url;
            String app = "sensing";
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=serverUrl + "/ppc.ico"%>" />
        <link rel="stylesheet" href="<%=serverUrl + "/style.css"%>" />
        <title>Upload - <%=session.getAttribute("username")%> - <%=Globals.logo + ": " + app%></title>
        <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js"></script>
        <%
            Boolean error = false;
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage == null) {
                error = false;
            } else {
                error = true;
            }
        %>
    </head>
    <body>
        <div id="header">
            <a id="logo" href="<%=serverUrl + "/main/home.jsp"%>"><%=Globals.logo%></a>
            <!--
            <div id="user_menu">
                <div id="user_menu_username" name="user_menu_username">
                    <a>
                        <value><%=session.getAttribute("username")%></value>
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
            <div id="app">Sensing</div>
        </div>
        <div id="left">
            <form action="<%=serverUrl + "/upload"%>">
                <input type="submit" id="upload_new_button" value="NEW">
            </form>
        </div>
        <div id="upload_file">
            <form id="upload_file_form" action="<%=serverUrl + "/upload"%>" method="post" enctype="multipart/form-data">
                <% if (error) {%>
                <div id="upload_message">
                    <value id="upload_message_value"><%=errorMessage%></value>
                </div>
                <% }%>
                <div id="upload_comment" >
                    <label id="upload_comment_label">Comment</label>
                    <input type="text" id="upload_comment_value" name="upload_comment_value" placeholder="Comment">
                </div>
                <div id="upload_code">
                    <div id="upload_client">
                        <div id="upload_code_client" name="upload_code_client">
                            <label id="upload_code_client_label" name="upload_code_client_label">Client</label>
                            <input type="file" accept=".java" id="upload_code_client_value" name="upload_code_client_value" required/>
                            <div id="upload_time">
                                <input type="hidden" id="upload_time_checkbox_off" name="upload_time_checkbox" value="off">
                                <input type="checkbox" id="upload_time_checkbox" name="upload_time_checkbox" label="Time">
                                <p id="upload_time_checkbox_label">Time</p>
                                <div id="upload_time_selection">
                                    <select id="upload_time_selection_from" name="upload_time_selection_from">
                                        <%
                                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(sdf.parse("00:00"));
                                            do {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "'>" + sdf.format(cal.getTime()) + "</option>");
                                                cal.add(Calendar.MINUTE, 30);
                                            } while (!sdf.format(cal.getTime()).equals("00:00"));
                                            /**/
                                        %>
                                    </select>
                                    -
                                    <select id="upload_time_selection_to" name="upload_time_selection_to">
                                        <%
                                            do {
                                                out.println("<option value='" + sdf.format(cal.getTime()) + "'>" + sdf.format(cal.getTime()) + "</option>");
                                                cal.add(Calendar.MINUTE, 30);
                                            } while (!sdf.format(cal.getTime()).equals("00:00"));
                                            /**/
                                        %>
                                    </select>
                                </div>
                            </div>
                            <div id="upload_map">
                                <input type="hidden" id="upload_map_checkbox_off" name="upload_map_checkbox" value="off">
                                <input type="checkbox" id="upload_map_checkbox" name="upload_map_checkbox" label="Map">
                                <p id="upload_map_checkbox_label">Location</p>
                                <div id="upload_map_canvas"></div>
                                <br />
                                <div id ="upload_map_details" name="map_details">
                                    <!--
                                    Erase "hidden" to debug the coordinates
                                    -->
                                    <label type="hidden" for="upload_latitude"></label>
                                    <input type="hidden" id="upload_map_latitude" type="text" value="" />
                                    <label type="hidden" for="longitude"></label>
                                    <input type="hidden" id="upload_map_longitude" type="text" value="" />
                                    <label type="hidden" for="upload_map_sw_lat"></label>
                                    <input type="hidden" id="upload_map_sw_lat" name="upload_map_sw_lat" type="text" value="" />
                                    <label type="hidden" for="upload_map_sw_lng"></label>
                                    <input type="hidden" id="upload_map_sw_lng" name="upload_map_sw_lng" type="text" value="" />
                                    <label type="hidden" for="upload_map_ne_lat"></label>
                                    <input type="hidden" id="upload_map_ne_lat" name="upload_map_ne_lat" type="text" value="" />
                                    <label type="hidden" for="upload_map_ne_lng"></label>
                                    <input type="hidden" id="upload_map_ne_lng" name="upload_map_ne_lng" type="text" value="" />
                                    <script type="text/javascript">
                                        var latlng = new google.maps.LatLng(0, 0);
                                        var options = {
                                            center: latlng,
                                            disableDefaultUI: true,
                                            draggable: true,
                                            mapTypeId: google.maps.MapTypeId.ROADMAP,
                                            //minZoom: 1,
                                            //panControl: true,
                                            //rotateControl: true,
                                            zoom: 1
                                             //zoomControl: true
                                        };
                                        var canvas = document.getElementById('upload_map_canvas');
                                        var map = new google.maps.Map(canvas, options);
                                        google.maps.event.addDomListener(map, 'idle', function() {
                                            var bounds = map.getBounds();
                                            document.getElementById('upload_map_sw_lat').value = bounds.getSouthWest().lat();
                                            document.getElementById('upload_map_sw_lng').value = bounds.getSouthWest().lng();
                                            document.getElementById('upload_map_ne_lat').value = bounds.getNorthEast().lat();
                                            document.getElementById('upload_map_ne_lng').value = bounds.getNorthEast().lng();
                                        });
                                        document.getElementById("upload_map_checkbox").onclick = function() {
                                            if (this.checked) {
                                                latlng = map.getCenter();
                                                google.maps.event.trigger(map, 'resize');
                                                map.setCenter(latlng);
                                            }
                                        };
                                        var marker = new google.maps.Marker({
                                            position: new google.maps.LatLng(0, 0),
                                            draggable: true
                                        });
                                        google.maps.event.addListener(marker, 'dragend', function(evt) {
                                            document.getElementById('upload_map_latitude').value = evt.latLng.lat().toFixed(6);
                                            document.getElementById('upload_map_longitude').value = evt.latLng.lng().toFixed(6);
                                        });
                                        //marker.setMap(map);
                                    </script>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Deprecated -->
                    <div id="upload_server" type="hidden">
                        <div id="upload_code_server"> 
                            <label id="upload_code_server_label" name="upload_code_server_label">Server</label>
                            <input type="file" accept=".zip" id="upload_code_server_value" name="upload_code_server_value"/>
                        </div>
                    </div>
                    <!-- Deprecated -->
                    
                </div>
                <input id="upload_ok_button" type="submit" value="OK"/>
            </form>
            <div id="footer" style="border: none; margin: 0px;">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
            <form action="<%=serverUrl + "/main/home.jsp"%>">
                <input id="upload_cancel_button" type="submit" value="CANCEL"/>
            </form>
        </div>
    </body>
</html>
