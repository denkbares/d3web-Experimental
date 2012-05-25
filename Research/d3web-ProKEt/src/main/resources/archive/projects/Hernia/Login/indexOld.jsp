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
<body>
<div class="bwrap">
<%	if (go) { %>
 <p>Ok, proceeding to login ... if you are not redirected automatically, please follow
 	<a href="login.jsp">this link</a>.</p>
<%	} else { %>
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
        <td class="left">Invitation Password</td>
        <td><input type="password" name="g_pwd" /><%
	if (errors.contains("g_pwd")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left d">Captcha<br /><img style="cursor: pointer;" src="gfx/information.png" title="The c
aptcha is to prevent automated form processing." /></td>
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

<div id='LOGININFO' style='width: 800px; margin-left: auto; margin-right: auto; margin-top: 20px;'>
	<div style='font-size:1.2em;'>-> Please type in the <b>Invitation Password</b> here. </div>
	<div style='font-size:1em; margin-top: 10px'>This is the password you received by the person who invited
	you to use the registry. It is NOT the <i>personal password</i> you may have received via email after
	registering as a user for the EuraHS registry!</div> <br />
	<div style='font-size:1.2em;'>-> Afterwards, please enter the captcha (follow on-screen instructions) and press <b>login</b>.</div>
	
	<div style='font-size:1em; margin-top: 10px'>The EuraHS page is protected against automated internet login-misuse. The first time you s
uccessful complete 
		the login with the captcha, a cookie will be created, that prevents your own computer to access again this login. 
		If needed, you can delete the cookie via the desktop. 
	</div>		
	
	<div style='font-size:1.2em; margin-top: 20px'>-> If you have successfully logged in with the invitation password and the captcha, you 
are redirected 
		to the personal login site of the registry. There, you can either login using your personal login information you have received
		after registering to EuraHS, or you can register yourself as a new user.</div>
</div>

<div id=\"BROWSERINFO\" style='width: 800px; margin-left:auto; margin-right:auto; margin-top:50px;'>
	<hr>
	<div style='font-size:1.2em'>
	Should there be any further problems with logging in to the registry or any further browser-related issues, please contact us:
	</div>
		<ul>
			<li>
				<a href="mailto:freiberg@informatik.uni-wuerzburg.de">freibergATinformatik.uni-wuerzburg.de (Martina Freiberg, 
Uni W&uuml;rzburg programming/development team)</a></a>
			</li>
			<li>
				<a href="mailto:dietz_u@chirurgie.uni-wuerzburg.de">dietz_uATchirurgie.uni-wuerzburg.de   (Uli Dietz, Uni W&uum
l;rzburg - clinic)</a></a>
			</li>
		</ul>
		<br /><br />
	<b>Important:</b> In the current test-phase we are searching for system-problems, stability of the database, improvement of navigation 
and data analysis. If you have ANY suggestions, problems or questions, please contact us (see above) - we will do our best to optimize the Regi
ster until the launch next June. 
	We ask for your understanding, that also minor system improvements may take one or two days to be implemented.
	
		<div style='font-size:1.2em; color: darkred; margin-top:20px'>		
		Due to security and expense reasons this site does only support the following browsers/versions:</div>
		<ul>
			<li>
				<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox (vs. 10 or higher)</a>
			</li>
			<li>
				<a href='http://www.google.com/chrome/'>Google Chrome (vs. 17 or higher)</a>
			</li>
			<li>
				<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer (vs. 9 
or higher)</a>
			</li>
		</ul>
</div>



<script type="text/javascript" defer>
//<!--

//--------------------------------BROWSER CHECKING STUFF

/**
 * BROWSER DETECTION SCRIPT
 * Origin from http://www.quirksmode.org/js/detect.html
 * Needed for correctly detecting various browsers, including several IE versions
 *
 * NOTE: needs to be adapted from time to time
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera",
			versionSearch: "Version"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();	

/* when site has loaded, immediately perform browser handling */
window.onload = handleUnsupportedBrowsers();

/*
 * Browser detection and handling: if browser is IE and NOT version 9, display
 * a static message-page to the user so he can't go on
 */
function handleUnsupportedBrowsers() {

    var bro = BrowserDetect.browser;
    var ver = BrowserDetect.version;

	if(bro == "Explorer" && ver != "9"){

		var message = "<div id=\"BROWSERINFO\" style='width: 700px; margin-left:auto; margin-right:auto; margin-top:50px; font-size:1.5em; color: red'>";
		message = message + "You are currently using <b>" + bro + " " + ver ;
		message = message + ".</b><br /><br />This website does NOT fully support this browser/version! <br /><br />";
		message = message + "Please use instead one of the following suggestions:";
        message = message + "<ul>";
        message = message + "<li>";
        message = message + "<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://www.google.com/chrome/'>Google Chrome</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer <b>9.0</b></a>";
        message = message + "</li>";
        message = message + "</ul>";
        message = message + "</div>";
		document.body.innerHTML = message;
	}
}
//-->
</script>
</body>
</html>
root@KnowWE:/var/lib/tomcat6/webapps/EuraHS-Login# more index.jsp
<%@page import="de.ai_wuerzburg.genericregistration.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-trans
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
<body>
<div class="bwrap">
<%	if (go) { %>
 <p>Ok, proceeding to login ... if you are not redirected automatically, please follow
 	<a href="login.jsp">this link</a>.</p>
<%	} else { %>
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
        <td class="left">Invitation Password</td>
        <td><input type="password" name="g_pwd" /><%
	if (errors.contains("g_pwd")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left d">Captcha<br /><img style="cursor: pointer;" src="gfx/information.png" title="The c
aptcha is to prevent automated form processing." /></td>
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

<div id='LOGININFO' style='width: 800px; margin-left: auto; margin-right: auto; margin-top: 20px;'>
	<div style='font-size:1.2em;'>-> Please type in the <b>Invitation Password</b> here. </div>
	<div style='font-size:1em; margin-top: 10px'>This is the password you received by the person who invited
	you to use the registry. It is NOT the <i>personal password</i> you may have received via email after
	registering as a user for the EuraHS registry!</div> <br />
	<div style='font-size:1.2em;'>-> Afterwards, please enter the captcha (follow on-screen instructions) and press <b>login</b>.</div>
	
	<div style='font-size:1em; margin-top: 10px'>The EuraHS page is protected against automated internet login-misuse. The first time you s
uccessful complete 
		the login with the captcha, a cookie will be created, that prevents your own computer to access again this login. 
		If needed, you can delete the cookie via the desktop. 
	</div>		
	
	<div style='font-size:1.2em; margin-top: 20px'>-> If you have successfully logged in with the invitation password and the captcha, you 
are redirected 
		to the personal login site of the registry. There, you can either login using your personal login information you have received
		after registering to EuraHS, or you can register yourself as a new user.</div>
</div>

<div id=\"BROWSERINFO\" style='width: 800px; margin-left:auto; margin-right:auto; margin-top:50px;'>
	<hr>
	<div style='font-size:1.2em'>
	Should there be any further problems with logging in to the registry or any further browser-related issues, please contact us:
	</div>
		<ul>
			<li>
				<a href="mailto:freiberg@informatik.uni-wuerzburg.de">freibergATinformatik.uni-wuerzburg.de (Martina Freiberg, 
Uni W&uuml;rzburg programming/development team)</a></a>
			</li>
			<li>
				<a href="mailto:dietz_u@chirurgie.uni-wuerzburg.de">dietz_uATchirurgie.uni-wuerzburg.de   (Uli Dietz, Uni W&uum
l;rzburg - clinic)</a></a>
			</li>
		</ul>
		<br /><br />
	<b>Important:</b> In the current test-phase we are searching for system-problems, stability of the database, improvement of navigation 
and data analysis. If you have ANY suggestions, problems or questions, please contact us (see above) - we will do our best to optimize the Regi
ster until the launch next June. 
	We ask for your understanding, that also minor system improvements may take one or two days to be implemented.
	
		<div style='font-size:1.2em; color: darkred; margin-top:20px'>		
		Due to security and expense reasons this site does only support the following browsers/versions:</div>
		<ul>
			<li>
				<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox (vs. 10 or higher)</a>
			</li>
			<li>
				<a href='http://www.google.com/chrome/'>Google Chrome (vs. 17 or higher)</a>
			</li>
			<li>
				<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer (vs. 9 
or higher)</a>
			</li>
		</ul>
</div>



<script type="text/javascript" defer>
//<!--

//--------------------------------BROWSER CHECKING STUFF

/**
 * BROWSER DETECTION SCRIPT
 * Origin from http://www.quirksmode.org/js/detect.html
 * Needed for correctly detecting various browsers, including several IE versions
 *
 * NOTE: needs to be adapted from time to time
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera",
			versionSearch: "Version"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();	

/* when site has loaded, immediately perform browser handling */
window.onload = handleUnsupportedBrowsers();

/*
 * Browser detection and handling: if browser is IE and NOT version 9, display
 * a static message-page to the user so he can't go on
 */
function handleUnsupportedBrowsers() {

    var bro = BrowserDetect.browser;
    var ver = BrowserDetect.version;

	if(bro == "Explorer" && ver != "9"){

		var message = "<div id=\"BROWSERINFO\" style='width: 700px; margin-left:auto; margin-right:auto; margin-top:50px; font-size:1.5em; color: red'>";
		message = message + "You are currently using <b>" + bro + " " + ver ;
		message = message + ".</b><br /><br />This website does NOT fully support this browser/version! <br /><br />";
		message = message + "Please use instead one of the following suggestions:";
        message = message + "<ul>";
        message = message + "<li>";
        message = message + "<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://www.google.com/chrome/'>Google Chrome</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer <b>9.0</b></a>";
        message = message + "</li>";
        message = message + "</ul>";
        message = message + "</div>";
		document.body.innerHTML = message;
	}
}
//-->
</script>
</body>
</html>