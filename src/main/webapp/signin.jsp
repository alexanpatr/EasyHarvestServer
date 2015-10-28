<%@page import="com.www.server.Globals"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%String serverUrl = Globals.server_url;%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="shortcut icon" href="ppc.ico" />
        <link href="style.css" rel="stylesheet" type="text/css" />
        <title><%=Globals.logo%>: Welcome!</title>
    </head>
    <body>
        <div id="header" style="position: absolute">
            <a id="logo" href="<%=serverUrl%>"><%=Globals.logo%></a>
            <form id="signin_signup" action="<%=serverUrl + "/signup"%>">
                <div id="signin_signup_label">New to <%=Globals.logo%>?</div>
                <input id="signin_signup_button" type="submit" value="CREATE AN ACCOUNT">
            </form>
        </div>
        <div id="signin">
            <div id="signin_welcome_message">
                <h1><%=Globals.h1%></h1>
                <p><%=Globals.p%></p>
            </div>
            <form id="signin_form" action="<%=serverUrl + "/signin"%>" autocomplete="on" method="post">
                <label id="signin_form_label">Sign in</label>
                <label id="signin_username_label" for="signin_username_value">Username</label>
                <input id="signin_username_value" name="username" required="required" type="text"/>
                <label id="signin_password_label" for="signin_password_value">Password</label>
                <input id="signin_password_value" name="password" required="required" type="password"/>
                <% if ((String) request.getAttribute("errorMessage") != null) {%>
                <error id="signin_error">${errorMessage}</error>
                <% }%>
                <input id="signin_button"type="submit" value="Sign in"/>
            </form>
        </div>
        <div id="footer" style="min-width: 800px">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
    </body>
</html>