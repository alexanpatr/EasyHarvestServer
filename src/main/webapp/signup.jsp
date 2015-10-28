<%@page import="com.www.server.Globals"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%String serverUrl = Globals.server_url;%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="shortcut icon" href="ppc.ico" />
        <link href="style.css" rel="stylesheet" type="text/css" />
        <title><%=Globals.logo%>: Create an account</title>
    </head>
    <body>
        <script type="text/javascript">
            var RecaptchaOptions = { theme : 'white' };
        </script>
        <div id="header" style="position: absolute">
            <a id="logo" href="<%=serverUrl%>"><%=Globals.logo%></a>
            <form id="signup_signin" action="<%=serverUrl + "/signin"%>"/>
                <input id="signup_signin_button" type="submit" value="Sign in">
            </form>
        </div>
        <div id="signup">
            <div id="signup_welcome_message">
                <h1><%=Globals.h1%></h1>
                <p><%=Globals.p%></p>
            </div>
            <form id="signup_form" action="<%=serverUrl + "/signup"%>" method="post" autocomplete="off">
                <label id="signup_form_label">Sign up</label><br/>
                <label id="signup_username_label" for="signup_username_value">Choose your username</label>
                <input id="signup_username_value" name="username" required="yes" type="text" autocomplete="on" pattern="[a-zA-Z0-9]{5,30}" title="Please use between 6 and 30 characters." />
                <% if ((String) request.getAttribute("errorMessageUsername") != null) {%>
                <error id="signup_error_username">${errorMessageUsername}</error>
                <% }%>
                <label id="signup_email_label" for="signup_email_value">Enter your email address</label>
                <input id="signup_email_value" name="email" required="yes" type="email"/>
                <% if ((String) request.getAttribute("errorMessageEmail") != null) {%>
                <error id="signup_error_email">${errorMessageEmail}</error>
                <% }%>
                <label id="signup_password_label" for="signup_password_value">Create a password</label>
                <input id="signup_password_value" name="password" required="yes" type="password" pattern=".{8,}" title="Short passwords are easy to guess. Try one with at least 8 characters."/><br/>
                <label id="signup_password_confirm_label" for="signup_password_confirm_value">Confirm your password</label>
                <input id="signup_password_confirm_value" name="password_confirm" required="yes" type="password" oninput="check(this)"/>
                <script>
                    function check(input) {
                        if (input.value != document.getElementById('signup_password_value').value) {
                            input.setCustomValidity('These passwords do not match. Try again?');
                        } else {
                            // input is valid -- reset the error message
                            input.setCustomValidity('');
                        }
                    }
                </script>
                <!--
                <label id="signup_recaptcha_label">Prove you're not a robot</label><br/>
                <value id="signup_recaptcha_value">
                <%
                    /*
                    ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LeehNgSAAAAAHnQE0GJGjLn2w8Hbqx_1450PpkX", "6LeehNgSAAAAAP2nv9YmfNprFns5YX_6_rv-svzs ", false);
                    out.print(c.createRecaptchaHtml(null, null));
                    */
                %>
                </value><br/>
                -->
                <input id="signup_button" type="submit" value="Sign up"/>
            </form>
        </div>
        <div id="footer" style="min-width: 800px">&#169; 20[0-9]{2} <a href="http://linkedin.com/in/emkatsom" target="_blank">emkatsom</a>. All Rights and Lefts reserved.</div>
    </body>
</html>