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
  <form method="post" action="login.jsp">
    <table border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="2" class="head">EuraHS - Login</td>
      </tr>
      <tr>
        <td class="left">Email address</td>
        <td><input type="text" name="l_email" /><%
		if (errors.contains("l_email")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr>
        <td class="left">Registration Confirmation Password</td>
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
document.write("<a href=\"#\" onClick=\"show('hide');show('person')\">Create a new account</a>.");
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
        <td class="left">Type of account</td>
        <td><label onclick="changeToPerson()">
        <input class="m" type="radio" name="a_type" value="person" checked="checked"/>
        Person independent from institutions
        </label></td>
      </tr>
      <tr class="hide">
        <td class="left"></td>
        <td><label onclick="changeToInstitution()" >
        <input class="m" type="radio"  name="a_type" value="institution" />
        Institution with multiple persons
        </label></td>
      </tr>
      <tr class="hide">
        <td class="left">E-mail address</td>
        <td><input class="m" type="text" name="c_email" /><%
		if (errors.contains("c_email")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="person">
        <td class="left">Family name</td>
        <td><input class="m" type="text" name="c_fname" /><%
		if (errors.contains("c_fname")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="person">
        <td class="left">Given name</td>
        <td><input class="m" type="text" name="c_gname" /><%
		if (errors.contains("c_gname")) {
%><img class="icon" src="gfx/cross.png" /><% } %></td>
      </tr>
      <tr class="person">
        <td class="left">Title</td>
        <td><input type="text" name="c_title" /></td>
      </tr>
      <tr class="institution">
        <td class="left">Institution</td>
        <td><input class="m" type="text" name="c_inst" /></td>
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
        <td><input type="submit" name="create" value="Create account" /></td>
      </tr>
    </table>
    <div class="hide hwrap">
      <h1>Instructions and rules of the registry</h1>
      <p>Text with the user license agreement (will be added)</p>
    </div>
  </form>
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

			var message = "<div id='BROWSERINFO' style='width: 800px; margin-left:auto; margin-right:auto; margin-top:50px; font-size:1.5em;'>";
			message = message + "You are currently using <b>" + bro + " " + ver  + "</b>.<br /><br />"; 
			message = message + "Due to security and expense reasons this site can not support any browser/version. <div style='color: red'><b>" + bro + " " + ver + "</b> is NOT supported by this site! </div><br /><br />";
			message = message + "Please use instead one of the following suggestions:";
	        	message = message + "<ul>";
	        		message = message + "<li>";
	        		message = message + "<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox (vs. 10 or higher)</a>";
	        		message = message + "</li>";
	        		message = message + "<li>";
	        		message = message + "<a href='http://www.google.com/chrome/'>Google Chrome (vs. 17 or higher)</a>";
	        		message = message + "</li>";
	        		message = message + "<li>";
	        		message = message + "<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer (vs. 9 or higher)</a>";
	        		message = message + "</li>";
	        	message = message + "</ul>";
			
				message = message + "After having installed one of the above alternative browser suggestions, please come back to the \n\
                    <a href='http://eurahs.informatik.uni-wuerzburg.de/EuraHS-Dialog'> EuraHS Registry Login Site</a> to use the registry.<br /><br /> Should any browser problems persist then or should any new problems arise, please contact us:";
				message = message + "<div style='font-size: 0.7em'>";
				message = message + "<ul>";
					message = message +	"<li><a href='mailto:freiberg@informatik.uni-wuerzburg.de'>freiberg@informatik.uni-wuerzburg.de (Martina Freiberg, Uni W&uuml;rzburg programming/development team)</a></li>";
					message = message + "<li><a href='mailto:dietz_u@chirurgie.uni-wuerzburg.de'>dietz_u@chirurgie.uni-wuerzburg.de   (Uli Dietz, Uni W&uuml;rzburg - clinic)</a></li>";
				message = message + "</ul>";
				message = message + "</div>";
			
	        message = message + "</div>";
			document.body.innerHTML = message;
	}
}


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

<div id='LOGININFO' style='width: 800px; margin-left: auto; margin-right: auto; margin-top: 20px;'>
	<div style='font-size:1.2em;'>-> To login to EuraHS, please type in your <b>registered Email</b> and the <b>registration confirmation (
personal) password</b> here, and press the <b>login</b> button. </div>
	<div style='font-size:1em; margin-top: 10px'>The <i>registered Email</i> is the email address you used when registering for EuraHS.
		The <i>registration confirmation / personal password</i> is the password you received via email after having successfully regis
tered
		for EuraHS.<br />
		To be sure that the password works, despite of very different keyboards, copy-paste your password from your registration Email.
 The password will be saved and you will not have to type the password again.</div> 
	<div style='font-size:1.2em; margin-top: 20px'>-> If you have successfully logged in with your personal registration information you ar
e redirected 
		to the main page of the EuraHS registry where you can start entering your cases.</div>
		
	<div style='font-size:1.2em; margin-top: 40px'>-> In case you have not yet registered for EuraHS yet (and don't have any personal regis
tration information
	yet), please create a personal EuraHS account first by clicking on <b><i>Create a new account</i></b>, entering the required 
		information, <b><i>accepting the rules of the registry (confirm checkbox)</i></b>, and clicking the <b>Create account</b> butto
n.</div>
			<div style='font-size:1em; margin-top: 10px'>You will automatically receive Email notification confirming your successf
ul account creation and
				containing both your registration email and registration password. Please <b><i>activate</i></b> your account f
irst by clicking the verification link also contained in the Email. Afterwards, you can go back to the <a href='http://eurahs.informatik.uni-wu
erzburg.de/EuraHS-Dialog'> EuraHS Registry Login Site</a>, login with your personal registration information, and start using the EuraHS regist
ry.</div>
</div>

<div id=\"BROWSERINFO\" style='width: 800px; margin-left:auto; margin-right:auto; margin-top:50px; '>
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
</body>
</html>