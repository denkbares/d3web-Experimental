<%
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%= Info.getTitle() %></title>
<link rel="stylesheet" type="text/css" href="style.css" />
<%!
	private static enum Access { OPEN, LOGIN, CREATE, PWD };
	private static enum WhatToDo { NULL, CLOSE, NOTHING, ENTERNEW };
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
	if (request.getParameter("login") != null)
		a = Access.LOGIN;
	else if (request.getParameter("create") != null)
		a = Access.CREATE;
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
			if (fname == null)
				errors.add("c_fname");
			
			String gname = check(request.getParameter("c_gname"));
			if (gname == null)
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
<body>
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
<div class="bwrap">
  <hr />
  <div class="hwrap">
    <p>generic text (will be added)</p>
  </div>
  <hr />
  <form method="post" action="login.jsp">
    <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="2" class="head">Login</td>
      </tr>
      <tr>
        <td class="left">E-mail address</td>
        <td><input type="text" name="l_email" /><%
		if (errors.contains("l_email")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left">Password</td>
        <td><input type="password" name="l_pwd" /><%
		if (errors.contains("l_pwd")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><input type="submit" name="login" value="login" /></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td class="small"><a href="login.jsp?pass=1">Forgot username or password?</a></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td class="small">Not registered?
<script type="text/javascript">
document.write("<a href=\"#\" onClick=\"show('hide')\">Create a new account</a>.");
</script>
<noscript>Create a new account below.</noscript>
</td>
      </tr>
      <tr class="hide">
        <td colspan="2">&nbsp;</td>
      </tr>
      <tr class="hide">
        <td colspan="2" class="head head2">Create a new account</td>
      </tr>
      <tr class="hide">
        <td class="left">E-mail address</td>
        <td><input class="m" type="text" name="c_email" /><%
		if (errors.contains("c_email")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">Family name</td>
        <td><input class="m" type="text" name="c_fname" /><%
		if (errors.contains("c_fname")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">Given name</td>
        <td><input class="m" type="text" name="c_gname" /><%
		if (errors.contains("c_gname")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">Title</td>
        <td><input type="text" name="c_title" /></td>
      </tr>
      <tr class="hide">
        <td class="left">Institution</td>
        <td><input type="text" name="c_inst" /></td>
      </tr>
      <tr class="hide">
        <td class="left">Address</td>
        <td><input class="m" type="text" name="c_addr" /><%
		if (errors.contains("c_addr")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">City</td>
        <td><input class="m" type="text" name="c_city" /><%
		if (errors.contains("c_city")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">ZIP code</td>
        <td><input class="m" type="text" name="c_zip" /><%
		if (errors.contains("c_zip")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">Country</td>
        <td><input class="m" type="text" name="c_country" /><%
		if (errors.contains("c_country")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="hide">
        <td class="left">Phone number</td>
        <td><input type="text" name="c_phone" /></td>
      </tr>
      <tr class="hide">
        <td class="left">Member of EHS</td>
        <td><input type="checkbox" name="c_member" value="1" /></td>
      </tr>
      <tr class="hide">
        <td class="left"><%
		if (errors.contains("c_eula")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
        <td class="nopad warn"><table class="d">
            <tr>
              <td><input type="checkbox" name="c_eula" value="1" /></td>
              <td>I have read  the instructions for use of the EuraHS database<br />
                and I accept the rules of the  registry.</td>
            </tr>
        </table></td>
      </tr>
      <tr class="hide">
        <td class="left">&nbsp;</td>
        <td><input type="submit" name="create" value="create account" /></td>
      </tr>
    </table>
    <div class="hide hwrap">
      <h1>Instructions and rules of the registry</h1>
      <p>Text with the user license agreement (will be added)</p>
    </div>
  </form>
<script type="text/javascript" defer>
//<!--
function show() {
	_set(document.body, "visible");
}

_set(document.body, "hidden");

function _set(e, toWhat) {
	for (var i = 0; i < e.childNodes.length; i++) {
		var obj = e.childNodes[i];
		var hidden = false;
		if (_contains(obj.className, "hide")) {
			obj.style.visibility = toWhat;
		} else {
			_set(obj, toWhat);
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
</body>
</html>
