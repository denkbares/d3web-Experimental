<%@page import="de.ai_wuerzburg.genericregistration.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
		
			String name = request.getParameter("g_name");
			String pwd = request.getParameter("g_pwd");
			
			if (name != null) {
				name = name.trim();
				if ("".equals(name))
					name = null;
			}
			
			boolean ok = false;
			boolean gnamefound = false;
			for (Group g : Group.getGroups()) {
				boolean nameok =
					name == null && g.getName() == null
					|| name != null && name.equals(g.getName());
				gnamefound |= nameok;
				ok |= g.getPass().equals(pwd) && nameok;
			}
			
			if (!ok) {
				if (gnamefound)
					errors.add("g_pwd");
				else
					errors.add("g_name");
			} else {
				session.setAttribute("group", new Object());
				Cookie c = new Cookie("go", "1");
				c.setMaxAge(365 * 24 * 60 * 60); // one year
				response.addCookie(c);
				go = true;
			}
		}
	}
	if (go) { %>
<meta http-equiv="refresh" content="0;url=login.jsp" />
<%	} %>	
</head>
<body>
<div class="bwrap">
<%	if (go) { %>
 <p>Ok, proceeding to login ... if you are not redirected automatically, please follow
 	<a href="login.jsp">this link</a>.</p>
<%	} else { %>
  <hr />
  <div class="hwrap">
    <p>generic text (will be added)</p>
  </div>
  <hr />
  <form method="post" action="index.jsp">
<%
	int r = new Random().nextInt(2);
	String num = r == 0 ? "first" : "second";
%>
<input type="hidden" name="cn" value="<%= r %>" />
    <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="2" class="head">Enter site</td>
      </tr>
      <tr>
        <td class="left">Group name (optional)</td>
        <td><input type="text" name="g_name" /><%
	if (errors.contains("g_name")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left">Password</td>
        <td><input type="password" name="g_pwd" /><%
	if (errors.contains("g_pwd")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left d">Captcha<br /><img style="cursor: pointer;" src="gfx/information.png" title="The captcha is to prevent automated form processing." /></td>
        <td><span class="inputhint">You see two "words" with five characters each.</span>
<br /><img src="captcha.jpg" />
<br /><span class="inputhint">Please enter only the <b><%= num %></b> one.</span>
<br /><input type="text" name="c"/><%
	if (errors.contains("c")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><input type="submit" name="login" value="login" /></td>
      </tr>
    </table>
  </form>
<%	} %>
</div>
</body>
</html>
