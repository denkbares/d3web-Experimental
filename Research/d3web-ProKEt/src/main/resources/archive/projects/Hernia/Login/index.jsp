<%@page import="de.ai_wuerzburg.genericregistration.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.or
    g/TR/xhtml1/DTD/xhtml1-trans
    itional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title><%= Info.getTitle() %></title>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <%
                if (request.getCookies() != null)
                        for (Cookie c : request.getCookies())
                if (c.getName().equals("go")
                        && c.getValue().equals("1"))
                {
                        session.setAttribute("group", new Object());
                }

                boolean go = session.getAttribute("group") != null;
                Set<String> errors = new HashSet<String>();
                if (!go && request.getParameter("login") != null) {
	
                        String theCaptcha = (String) session.getAttribute(nl.captcha.Constants.SESSION_KEY);
                        if (theCaptcha == null) {
                errors.add("c");
                        } else {
                int whatCaptcha = 0;
                try {
                        whatCaptcha = Integer.parseInt(request.getParameter("cn"));
                } catch (Exception ex) { /* duh */ }
                String[] captchas = theCaptcha.split("\\s+");
                theCaptcha = captchas[whatCaptcha];
                String reqC = request.getParameter("c");
                if (!theCaptcha.equals(reqC))
                        errors.add("c");
                        }
	
                        if (errors.isEmpty()) {
		
                String pwd = request.getParameter("g_pwd");
	
                boolean ok = false;
                for (CaptchaGroup g : CaptchaGroup.getGroups()) {
                        ok = g.getPass().equals(pwd);
                }
	
                if (!ok) {
                        errors.add("g_pwd");
                } else {
                        session.setAttribute("group", new Object());
                        Cookie c = new Cookie("go", "1");
                        c.setMaxAge(365 * 24 * 60 * 60); // one year
                        response.addCookie(c);
                        go = true;
                }
                        }
                }
                if (go) {
        %>
        <meta http-equiv="refresh" content="0;url=login.jsp" />
        <%	} %>	
    </head>
    
    <body style="padding:0px; margin:0px;">

        <div id="head" style="z-index: 1000; background: none repeat scroll 0 0 #EFEFEF; box-shadow: 0 0 5px black; width:100%;">
            <table style="width:100%; margin: 0px; padding: 5px 5px 0px 5px;">
                <tbody>
                    <tr>
                        <td width="35%" />
                        <td width="*">
                            <div style="color: black; font-size: 1.4em; font-variant: small-caps;
                                 font-weight: bold; text-align: center; font-family:arial"> 
                                European Registry of Abdominal wall HerniaS 
                            </div>
                        </td>
                        <td width="35%">
                            <div style="float:right;">
                                <img style="width:100px;" alt="logo" src="../EuraHS-Dialog/img/eurahslogo.png" />
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        
        
        <div style="margin: 25px auto; width: 800px;">

            <!-- if GO, then goto login -->

            <%	if (go) { %>
            <p>Ok, proceeding to login ... if you are not redirected automatically, please follow
                <a href="login.jsp">this link</a>.</p>

            <!-- OTJERWISE if no GO, show ENTER Site-->
            <%	} else { %>
            <form method="post" action="index.jsp">
                <%
                        int r = new Random().nextInt(2);
                        String num = r == 0 ? "first" : "second";
                %>
                <input style="width: 300px;" type="hidden" name="cn" value="<%= r %>" />
                <table border="0" cellspacing="0" cellpadding="0" width="560px;">
                    <tr>
                        <td colspan="2" 
                            style="font-size:13pt;font-weight:bold;padding: 20px 0 20px;text-align:right;">
                            <b>Computer registration</b> to enter EuraHS
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: left; padding: 5px 0 5px;">Invitation Password:</td>
                        <td style="padding: 5px 0 5px; float: right;">
                            <input type="password" name="g_pwd" />
                            <% if (errors.contains("g_pwd")) {%>
                            <img class="icon" src="gfx/cross.png" />
                            <% } %>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 5px 0 5px;">
                            <div style="text-align: left; float: left;"> CAPTCHA: </div>
                            <img style="cursor: pointer; text-align: left; float: left; margin-left: 10px;" 
                                 src="gfx/information.png" 
                                 title="The captcha is to prevent automated form processing." />
                        </td>
                        <td style="padding: 5px 0 5px; float: right;">
                            <img style="width: 304px;" src="captcha.jpg" />
                        </td>    
                    </tr>
                    <tr>
                        <td style="padding: 5px 0 5px;">
                            Enter <b><%= num %></b> CAPTCHA:
                        </td>
                        <td style="padding: 5px 0 5px;">
                            <input style="float: right;" type="text" name="c"/>
                            <% if (errors.contains("c")) {%>
                            <img class="icon" src="gfx/cross.png" />
                            <% } %>
                        </td>
                    </tr>

                    <tr >
                        <td style="padding: 5px 0 5px;">&nbsp;</td>
                        <td style="padding: 5px 0 5px;">
                            <div style="float: right;">
                                <input style="width: 75px; height:25px;" type="submit" name="login" value="ENTER" />
                            </div>
                        </td>
                    </tr>
                </table>
            </form>
            <%	} %>
        </div>

        <div id='LOGININFO' style='width: 560px; margin-left: auto; margin-right: auto; margin-top: 20px;'>
            <div style='font-size:1.2em;'>- To register your computer, please enter the <b>Invitation Password</b>. </div>
            <div style='font-size:1em; float: right;'>This is the password you received by the person who invited
                you to the registry.</div> <br />
            <div style='font-size:1.2em; margin-top:10px;'>- Please enter only the <b>first</b> OR <b> the second CAPTCHA</b> 
                (see above).</div><br />
            <div style='font-size:1.2em;'>- Please press <b>ENTER</b>.</div><br />
            <div style='font-size:1.2em; font-weight: bold;'>
                <div style="float: left; padding-top: 15px;">
                    - Problems or Questions? Please contact us:
                </div>
                <div style="width: 50px; float: right;">
                    <a href="mailto:iris.kyle-leinhase@eurahs.eu">
                        <img src="../EuraHS-Dialog/img/mailto.png" alt="Mail" style="width:50px;" />
                    </a>
                </div>
            </div>
        </div>

        <div id='BROWSERINFO' style='width: 560px; margin-left:auto; margin-right:auto; margin-top:100px;'>
            <div style="width: 560px; margin-left: auto; margin-right: auto;">
                <div style=" font-size: 1.2em; font-weight:  bold; text-align: center;">   
                   Please note! EuraHS is optimized for the following browsers:
                </div>
            </div>
            <div id="browsers" style="margin-left: auto; margin-right: auto; width: 600px; margin-top: 20px">
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="http://www.mozilla.org/en-US/firefox/all.html" target="_blank"> 
                        <img src="../EuraHS-Dialog/img/FF.png" alt="Firefox 10 (or higher)" style="width:75px;" />
                        <!--<img src="FF.png" alt="Firefox 20 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">10 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="https://www.google.com/chrome" target="_blank">
                        <img src="../EuraHS-Dialog/img/GC.png" alt="Google Chrome 17 (or higher)" style="width:75px;" />
                        <!--<img src="GC.png" alt="Google Chrome 17 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">17 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="http://www.apple.com/de/safari/download/" target="_blank">
                        <img src="../EuraHS-Dialog/img/SA.png" alt ="Safari 5 (or higher)" style="width:75px;" />
                        <!--<img src="SA.png" alt ="Safari 5 (or higher)" style="width:100px;" />--> 
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">5 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="http://windows.microsoft.com/de-DE/internet-explorer/products/ie/home" target="_blank">
                        <img src="../EuraHS-Dialog/img/IE.png" alt="Internet Explorer 8 (or higher)" style="width:75px;" />
                        <!--<img src="IE.png" alt="Internet Explorer 8 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">9 (or higher)</div>
                </div>
            </div>

        </div>
    </body>
</html>
