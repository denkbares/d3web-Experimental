<%
        if (request.getCookies() != null)
                for (Cookie c : request.getCookies())
                        if (c.getName().equals("go")
                                && c.getValue().equals("1"))
                        {
                                session.setAttribute("group", new Object());
                        }

        if (session.getAttribute("group") == null) { %>
<jsp:forward page="index.jsp" />
<%	} %>
<%@page import="de.ai_wuerzburg.genericregistration.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-
    transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/javascript; charset=utf-8" />
        <title><%= Info.getTitle() %></title>
        <link rel="stylesheet" type="text/css" href="style.css" />
        <%!
                private static enum Access { OPEN, LOGIN, CREATE, PWD };
                private static enum WhatToDo { NULL, CLOSE, NOTHING, ENTERNEW };
                private static enum AccountType { INSTITUTION, PERSON };
        %>
        <%
                String supportLink =
                        "<a href=\"mailto:" + Info.getSupport() + "\">" + Info.getSupport() + "</a>"
                        ;

                String resultText = null;
                WhatToDo resultWhat = WhatToDo.NULL;
	
                Set<String> errors = new HashSet<String>();
	
                String login = null;
	
                Access a;
                AccountType t = AccountType.PERSON;
                if (request.getParameter("login") != null)
                        a = Access.LOGIN;
                else if (request.getParameter("create") != null) {
                        a = Access.CREATE;	
                        if (request.getParameter("a_type").equals("institution")) 
                                t = AccountType.INSTITUTION;
                }
                else if (request.getParameter("pass") != null)
                        a = Access.PWD;
                else
                        a = Access.OPEN;
	
                if (errors.isEmpty()) {
		
                        if (a == Access.PWD) {
			
                                resultText =
                                        "<h2>Sorry, but this is not yet available.</h2>" +
                                        "<p>Please contact our support:<br>" + supportLink + "</p>";
                                resultWhat = WhatToDo.ENTERNEW;
			
                        } else  if (a == Access.LOGIN) {
			
                                String email = request.getParameter("l_email");
                                String pwd = request.getParameter("l_pwd");
			
                                User u = User.getUser(email);
                                if (u == null)
                                        errors.add("l_email");
                                else if (!u.checkPassword(pwd))
                                        errors.add("l_pwd");
                                else
                                        login = email;
			
                        } else if (a == Access.CREATE) {
			
                                String fname = check(request.getParameter("c_fname"));
                                if (t == AccountType.PERSON && a == null)
                                        errors.add("c_fname");
			
                                String gname = check(request.getParameter("c_gname"));
                                if (t == AccountType.PERSON && gname == null)
                                        errors.add("c_gname");
			
                                String title = check(request.getParameter("c_title"));
                                String inst = check(request.getParameter("c_inst"));
			
                                String addr = check(request.getParameter("c_addr"));
                                if (addr == null)
                                        errors.add("c_addr");
	
                                String city = check(request.getParameter("c_city"));
                                if (city == null)
                                        errors.add("c_city");
	
                                String zipS = check(request.getParameter("c_zip"));
                                Integer zip = null;
                                try {
                                        zip = Integer.parseInt(zipS);
                                } catch (Exception ex) { /* duh */ }
                                if (zip == null)
                                        errors.add("c_zip");
	
                                String country = check(request.getParameter("c_country"));
                                if (country == null)
                                        errors.add("c_country");
			
                                String phone = check(request.getParameter("c_phone"));
	
                                String email = check(request.getParameter("c_email"));
                                if (email == null)
                                        errors.add("c_email");
	
                                boolean member = "1".equals(request.getParameter("c_member"));
			
                                boolean eula = "1".equals(request.getParameter("c_eula"));
                                if (!eula)
                                        errors.add("c_eula");
			
                                if (errors.isEmpty()) {
                                        try {
                                                boolean ok =
                                                        User.addUser(
                                                                email, fname, gname, title, inst,
                                                                addr, city, zip, country,
                                                                phone, member);
                                                if (ok) {
                                                        resultText =
                                                                "<h2>Your account has been created, but it is not activated yet.</h2>" +
                                                                "<p>An e-mail with further information is on its way, so please" +
                                                                " check your inbox and spam folder (maybe it takes a few minutes)." +
                                                                " If you don't" +
                                                                " receive an e-mail then please contact our support:<br>" + supportLink +
                                                                "</p>"
                                                        ;
                                                        resultWhat = WhatToDo.CLOSE;
                                                } else {
                                                        resultText =
                                                                "<h2>Oops - something went wrong!</h2><p>Your account could not be created due" +
                                                                " to some internal error :-(</p>" +
                                                                "<p>Please contact our support:<br>" + supportLink + "</p>"
                                                        ;
                                                        resultWhat = WhatToDo.NOTHING;
                                                }
                                        } catch (User.MailFailureException ex) {
                                                resultText =
                                                        "<h2>Oops - something went wrong!</h2><p>Your account was created but the e-mail" +
                                                        " with further information" +
                                                        " could not be sent :-(</p><p>You can either try to register with another" +
                                                        " e-mail adress or wait for 12 hours (until the activation link for" +
                                                        " the current registration is expired) to register again" +
                                                        " or contact our support:<br>" + supportLink + "</p>"
                                                ;
                                                resultWhat = WhatToDo.NOTHING;
                                        } catch (User.ExistingUserException ex) {
                                                resultText =
                                                        "<h2>Your account could not be created as someone has already created" +
                                                        " an account with the given e-mail.</h2><p>If you think this is an error," +
                                                        " then please contact our support:<br>" + supportLink + "</p>"
                                                ;
                                                resultWhat = WhatToDo.ENTERNEW;
                                        }
                                }
			
                        }
                }
        %>
        <%!
                private static String check(String s) {
                        String res = s;
                        if (res == null)
                                return null;

                        res = res.replaceAll("\\s", " ");
                        res = res.trim();
                        if (res.equals(""))
                                return null;
		
                        res = res.replaceAll(";", ",");
                        res = res.replaceAll("\"", "'");
                        res = res.replaceAll("<", "[");
                        res = res.replaceAll(">", "]");
                        res = res.replaceAll("&", "&amp;");
                        return res;
                }
        %>
        <%	String gotoUrl =
                        a == Access.LOGIN && errors.isEmpty()
                        ? Goto.getGoto(login)
                        : null;
                if (gotoUrl != null) { %>
        <meta http-equiv="refresh" content="0;url=<%= gotoUrl %>" />
        <%	} %>
    </head>
    <body style="padding:0px; margin:0px;">
		 <div id="head" style="z-index: 1000; background: none repeat scroll 0 0 #EFEFEF; box-shadow: 0 0 5px black; width:100%;">
	            <table style="width:100%; margin: 0px; padding: 5px 5px 0px 5px;">
	                <tbody>
	                    <tr>
	                        <td width="15%" />
	                        <td width="*">
	                            <div style="color: black; font-size: 2.5em; font-variant: small-caps;
	                                 font-weight: bold; text-align: center; font-family:arial;color: #984806"> 
	                                European Registry of Abdominal wall HerniaS 
	                            </div>
	                        </td>
	                        <td width="15%">
	                            <div style="float:right;">
	                                <img style="width:180px;" alt="logo" src="../EuraHS-Dialog/img/eurahslogo.png" />
	                            </div>
	                        </td>
	                    </tr>
	                </tbody>
	            </table>
	        </div>
        <%	if (gotoUrl != null) { %>
        <div class="bwrap">
            <p>Login successful, proceeding to application ... if you are not redirected automatically, please follow
                <a href="<%= gotoUrl %>">this link</a>.</p>
        </div>
        <%	} else { %>
        <%		if (resultWhat != WhatToDo.NULL) { %>
        <div class="fwrap">
            <div class="floater"><div>
                    <%= resultText %>
                    <%			if (resultWhat == WhatToDo.CLOSE) { %>
                    <p>You may now close this window.</p>
                    <%			} else if (resultWhat == WhatToDo.ENTERNEW) { %>
                    <p><a href="login.jsp">OK</a></p>
                    <%			} else if (resultWhat == WhatToDo.NOTHING) { %>
                    <%			} %>
                </div></div>
        </div>
        <%		} %>
        <div style="margin: 25px auto; width: 800px;">
            <form method="post" action="login.jsp">
                <table border="0" cellspacing="0" cellpadding="0" style="width: 615px;">
                    <tr class='login'>
                        <td colspan="2" class="login"
                            style="font-size:1.4em;font-weight:bold;padding: 20px 0 20px;text-align:left;">
							EuraHS <b>MEMBER LOGIN</b></td>
                    </tr>
                    <tr>
                        <td style="text-align: left; padding: 5px 0 5px; float: left; font-size:1.4em;" class="login">REGISTERED EMAIL</td>
                        <td><input type="text" name="l_email" style="float: right;" class='login' /><%
                                if (errors.contains("l_email")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
                    <tr class='login'>
                        <td style="text-align: left; padding: 5px 0 5px; float: left; font-size:1.4em;">MEMBER PASSWORD</td>
                        <td><input type="password" name="l_pwd" style="float: right;" class='login' /><%
                                if (errors.contains("l_pwd")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
                    <tr class='login'>
                        <td>&nbsp;</td>
                        <td><input style="width: 125px; height:25px; float: right;" class ='login' type="submit" name="login" value="MEMBER LOGIN" /></td>
                    </tr>
					<tr>
                        <td colspan="2"> 
							<div id='LOGININFO' class="login" style='margin-left: auto; margin-right: auto; margin-top: 25px;'>

			                    <div style='font-size:1.1em; margin-top: 60px;'>1. Please enter your <b>REGISTERED EMAIL</b> and the <b>MEMBER PASSWORD</b>. </div>
			                    <div style='font-size:1.1em; float: left; margin-left:20px;'>This is the email and password you received after creating a new member account at the EuraHS registry.</div> <br />
			                    <div style='font-size:1.1em; margin-top: 40px;'>2. Please press <b>MEMBER LOGIN</b>.</div><br />
			                    <div style='font-size:1.1em;'>
			                        <div style="float: left; margin-top: 10px;">
			                            You don't have a member account yet?
			                        </div>
			                    </div>
			                </div>
						</td>
                    </tr>
                    
					<tr>
                        <td colspan="2" style="font-size:1.4em;font-weight:bold;padding: 20px 0 10px;text-align:left; ">
                            <div style="color: black;">
                                <script type="text/javascript">
                                    document.write("<a style='color:black; text-decoration:none' href=\"#\" onClick=\"show('hide');show('person');hide('login')\">CREATE NEW EuraHS MEMBER ACCOUNT</b></a>");
                                </script>
                            </div>
                        </td>
                    </tr>

                    <tr class="hide">
                        <td class="left" style="text-align: left; padding: 5px 0 5px; float: left; width: 240px; font-weight:bold; font-size:1.1em;">Type of account</td>
                        <td><label onclick="changeToPerson()">
                               	<input class="m" type="radio" name="a_type" value="person" checked="checked" style="float: left;"/>
                                <div style="float: left;">Person independent from institutions</div>
                            </label></td>
                    </tr>
                    <tr class="hide">
                        <td class="left"></td>
                        <td><label onclick="changeToInstitution()" >
                                 <input class="m" type="radio"  name="a_type" value="institution" style="float: left;"/>
	                             <div style="float: left;">Institution with multiple persons</div>
                            </label></td>
                    </tr>
                    <tr class="hide">
                        <td class="left" style="text-align: left; padding: 5px 0 5px; float: left; width: 240px; font-weight:bold; font-size:1.1em;">E-mail address</td>
                        <td><input class="m" type="text" name="c_email" style="float: right; width: 365px;" /><%
                                if (errors.contains("c_email")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
              		<tr class="person">
                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Family name</td>
                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_fname"  /><%
                                if (errors.contains("c_fname")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
                    <tr class="person">
                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Given name</td>
                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_gname" /><%
                                if (errors.contains("c_gname")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
                    <tr class="person">
                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Title</td>
                        <td><input style="float: right; width: 365px;" type="text" name="c_title" /></td>
                    </tr>
                    <tr class="institution">
                        <td style="text-align: left; padding: 5px 0 5px; float: left; width: 240px; font-weight:bold; font-size:1.1em;">Institution</td>
                        <td><input style="float: right; width: 365px;" type="text" name="c_inst" /></td>
                    </tr>
                    <tr class="hide">
                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Address</td>
                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_addr" /><%
                                if (errors.contains("c_addr")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                    </tr>
                        <tr class="hide">
	                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">City</td>
	                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_city" /><%
	                                if (errors.contains("c_city")) {
	                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
	                    </tr>
	                    <tr class="hide">
	                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">ZIP code</td>
	                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_zip" /><%
	                                if (errors.contains("c_zip")) {
	                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
	                    </tr>
	                    <tr class="hide">
	                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Country</td>
	                        <td><input class="m" style="float: right; width: 365px;" type="text" name="c_country" /><%
	                                if (errors.contains("c_country")) {
	                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
	                    </tr>
                   		<tr class="hide">
	                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Phone number</td>
	                        <td><input style="float: right; width: 365px;" type="text" name="c_phone" /></td>
	                    </tr>
	                    <tr class="hide">
	                        <td style="text-align: left; padding: 5px 0 5px; float: left;width: 240px; font-weight:bold; font-size:1.1em;">Member of EHS</td>
	                        <td><input style="float: left;" type="checkbox" name="c_member" value="1" /></td>
	                    </tr>
                    <tr class="hide">
                        <td class="left"><%
                                if (errors.contains("c_eula")) {
                            %><img class="icon" src="gfx/cross.png" /><% } %></td>
                        <td class="nopad warn"><table class="d">
                                <tr>
                                    <td><input type="checkbox" name="c_eula" value="1" /></td>
                                    <td>I have read the EuraHS usage instructions and I accept the rules of the registry.</td>
                                </tr>
                            </table></td>
                    </tr>
                    <tr class="hide">
                       	<td class="left">&nbsp;</td>
                        <td><input style="width: 180px; height:25px; float: right; padding: 2px 0px 0px 0px;" type="submit" name="create" value="CREATE MEMBER ACCOUNT" /></td>
                    </tr>
                	<tr>
                        <td colspan="2">
                            <div class="hide" style="float: left; font-size:10pt;padding: 20px 0px 0px 0px;text-align:right; margin-top: 20px;">
                                <div style="font-size: 13pt; font-weight: bold;"> Instructions and rules of the registry

                                </div>
                        </td>
                    </tr>
					</table>
                
            </form>
            <script type="text/javascript" defer>
                //<!--


                function changeToInstitution() {
                    hide("person");
                    show("institution");
                }

                function changeToPerson() {
                    hide("institution");
                    show("person");
                }

                function show(what) {
                    _set(document.body, what, null);
                }

                function hide(what) {
                    _set(document.body, what, "none");
                }

                _set(document.body, "hide", "none");

                _set(document.body, "institution", "none");

                _set(document.body, "person", "none");

                function _set(e, what, toWhat) {
                    for (var i = 0; i < e.childNodes.length; i++) {
                        var obj = e.childNodes[i];
                        var hidden = false;
                        if (_contains(obj.className, what)) {
                            obj.style.display = toWhat;
                        } else {
                            _set(obj, what, toWhat);
                        }
                    }
                }

                function _contains(s, toFind) {
                    if (s == null)
                        return false;
                    var ss = s.split(" ");
                    for (var i = 0; i < ss.length; i++)
                        if (ss[i] == toFind)
                            return true;
                    return false;
                }
                //-->
            </script>
            <%	} %>
        </div>

		<div id='BROWSERINFO' style='width: 615px; margin-left:auto; margin-right:auto; margin-top:40px;'>
            <div style='font-size:1.1em;'>
                <div style="float: left; margin-top: 10px;">
                    Problems or Questions? Please contact us
                </div>
                <div style="width: 50px; float: left; margin-top: -10px; margin-left: 80px;">
                    <a href="mailto:iris.kyle-leinhase@eurahs.eu">
                        <img src="../EuraHS-Dialog/img/mailto.png" alt="Mail" style="width:50px;" />
                    </a>
                </div>
            </div>
            <div style="width: 800px; margin-left: auto; margin-right: auto; float: left;">
                <div style=" font-size: 1.1em; text-align: left;">   
                    Please note! EuraHS is optimized for the following browsers:
                </div>
            </div>
            <div id="browsers" style="margin-left: auto; margin-right: auto; width: 615px; margin-top: 20px; float: left;">
                <div style="float:left; width: 100px; margin-left:35px; margin-right:25px;">
                    <a href="http://www.mozilla.org/en-US/firefox/all.html" target="_blank"> 
                        <img src="../EuraHS-Dialog/img/FF.png" alt="Firefox 10 (or higher)" style="width:60px;margin-left:10px;" />
                        <!--<img src="FF.png" alt="Firefox 20 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">10 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="https://www.google.com/chrome" target="_blank">
                        <img src="../EuraHS-Dialog/img/GC.png" alt="Google Chrome 17 (or higher)" style="width:60px;margin-left:10px;" />
                        <!--<img src="GC.png" alt="Google Chrome 17 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">17 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="http://www.apple.com/de/safari/download/" target="_blank">
                        <img src="../EuraHS-Dialog/img/SA.png" alt ="Safari 5 (or higher)" style="width:60px;margin-left:10px;" />
                        <!--<img src="SA.png" alt ="Safari 5 (or higher)" style="width:100px;" />--> 
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">5 (or higher)</div>
                </div>
                <div style="float:left; width: 100px; margin-left:25px; margin-right:25px;">
                    <a href="http://windows.microsoft.com/de-DE/internet-explorer/products/ie/home" target="_blank">
                        <img src="../EuraHS-Dialog/img/IE.png" alt="Internet Explorer 8 (or higher)" style="width:60px;margin-left:10px;" />
                        <!--<img src="IE.png" alt="Internet Explorer 8 (or higher)" style="width:100px;" />-->
                    </a>
                    <div style="margin-left: auto; margin-right: auto; width: 100px; font-size: 0.75em; font-weight:  bold; color: #555555;">9 (or higher)</div>
                </div>
            </div>

        </div>

    </body>
</html>