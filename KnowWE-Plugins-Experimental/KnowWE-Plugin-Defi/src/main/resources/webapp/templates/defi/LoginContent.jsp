<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}"/>
<fmt:setBundle basename="templates.default"/>
<%
	String postURL = "";
	WikiContext ctx = WikiContext.findContext(pageContext);
	AuthenticationManager mgr = ctx.getEngine().getAuthenticationManager();

	if (mgr.isContainerAuthenticated()) {
		postURL = "j_security_check";
	}
	else {
		String redir = (String) ctx.getVariable("redirect");
		if (redir == null) redir = ctx.getEngine().getFrontPage();
		postURL = ctx.getURL(WikiContext.LOGIN, redir);
	}

	if (!postURL.endsWith("Startseite")) {
		String[] pathparts = postURL.split("redirect=");
		postURL = pathparts[0] + "redirect=Startseite";
	}

	boolean supportsCookieAuthentication = mgr.allowsCookieAuthentication();
%>
<%-- Additional stylesheets --%>
<link href="KnowWEExtension/css/general.css" type="text/css" rel="stylesheet">
<link href="KnowWEExtension/css/defi.css" type="text/css" rel="stylesheet">
<link href="KnowWEExtension/css/defi-alert.css" type="text/css" rel="stylesheet">
<style type="text/css">
	#header {
		border-bottom: 0;
	}

	#header div.infobox, #header div.userbox, #favorites {
		display: none;
	}

	#content {
		margin: 5px 0 0 196px;
	}

	#footer a.data, #footer a.about, #footer a.start {
		display: none !important;
	}

	.tabmenu {
		display: none;
	}

	.hinweis {
		margin: 15px 10px 0 10px;
		padding: 10px;
		border: 1px dotted black;
	}

	.hinweis h5 {
		margin-bottom: 5px;
	}
</style>
<div id="login_defi">
	<br/>

	<h2>Willkommen im ICD-Forum</h2>

	<h2>Benutzeranmeldung f&uuml;r registrierte Teilnehmer</h2>

</div>

<wiki:TabbedSection defaultTab="${param.tab}">

	<%-- Login functionality --%>
	<wiki:Tab id="logincontent"
			  title='<%=LocaleSupport.getLocalizedMessage(pageContext, "login.tab")%>'>
		<%--<wiki:Include page='LoginTab.jsp'/>--%>


		<form action="<%=postURL%>"
			  id="login"
			  class="wikiform"
			  onsubmit="return Wiki.submitOnce(this);"
			  method="post" accept-charset="<wiki:ContentEncoding />">

			<div class="center">

				<h3><fmt:message key="login.heading.login"><fmt:param><wiki:Variable
						var="applicationname"/></fmt:param></fmt:message></h3>

				<div class="formhelp"><fmt:message key="login.help"></fmt:message></div>

				<table>
					<tr>
						<td colspan="2" class="formhelp">
							<wiki:Messages div="error" topic="login"
										   prefix='<%=LocaleSupport.getLocalizedMessage(pageContext,"login.errorprefix")%>'/>
						</td>
					</tr>
					<tr>
						<td><label for="j_username"><fmt:message key="login.login"/></label></td>
						<td><input type="text" size="24"
								   value="<wiki:Variable var='uid' default='' />"
								   name="j_username" id="j_username"/></td>
					</tr>
					<tr>
						<td><label for="j_password"><fmt:message key="login.password"/></label></td>
						<td><input type="password" size="24"
								   name="j_password" id="j_password"/></td>
					</tr>
					<% if (supportsCookieAuthentication) { %>
					<tr>
						<td><label for="j_remember"><fmt:message key="login.remember"/></label></td>
						<td><input type="checkbox"
								   name="j_remember" id="j_remember"/></td>
					</tr>
					<% } %>
					<tr>
						<td>&nbsp;</td>
						<td>
							<input type="hidden" name="redirect"
								   value="<wiki:Variable var='redirect' default='' />"/>
							<input type="submit" name="submitlogin"
								   value="<fmt:message key='login.submit.login'/>"/>
						</td>
					</tr>
				</table>

			</div>
		</form>
		<div class="hinweis"><h5>Hinweis:</h5>
			Als registrierter Teilnehmer der Studie haben Sie Ihren Benutzernamen
			und Ihr Passwort per Post erhalten. Sollten Sie diese Daten verloren haben, setzen Sie
			sich bitte umgehend mit uns in Verbindung!
			<br><br>
			Falls Sie sich für das Forum registrieren möchten, können Sie dies
			<a href="Login.jsp?tab=register">hier</a> tun.
			<br><br>
			ICD-Forum ist optimiert f&uuml;r Mozilla Firefox - Sie k&ouml;nnen den Browser
			<a target="_blank" href="http://www.mozilla.org/de/firefox/new/">hier</a>
			herunterladen.
		</div>
	</wiki:Tab>

	<%-- Register new user profile --%>
	<!--wiki:Permission permission='editProfile'-->
	<wiki:Tab id="register"
			  title='<%=LocaleSupport.getLocalizedMessage(pageContext, "login.register.tab")%>'>
		<wiki:Include page='RegisterDoubleOptIn.jsp'/>
	</wiki:Tab>

	<wiki:Tab id="success" title='Registrierung erfolgreich!'>
		<div class="hinweis"><h5>Registrierung erfolgreich!</h5>
			Sie haben sich erfolgreich registriert. Eine Bestätigungsmail mit Aktivierungslink wurde
			an die angegebene E-Mail Adresse gesendet. Bitte klicken Sie diesen Link, um die
			Registrierung abzuschließen.
			<br><br>
			Sollten Sie keine E-Mail bekommen haben, prüfen Sie bitte zunächst Ihren Spam-Ordner und
			kontaktieren Sie uns bei weiteren Problemen.
			<br><br>
			Zurück zur <a href="Login.jsp">Login-Seite</a>.
		</div>
	</wiki:Tab>

	<wiki:Tab id="confirmed" title='Registrierung abgeschlossen!'>
		<div class="hinweis"><h5>Registrierung abgeschlossen!</h5>
			Glückwunsch, Sie haben die Registrierung abgeschlossen!
			Sie können sich nun mit Ihren Zugangsdaten im Forum anmelden.
			<br><br>
			Zurück zur <a href="Login.jsp">Login-Seite</a>.
		</div>
	</wiki:Tab>

	<!--/wiki:Permission-->

</wiki:TabbedSection>
<h3 style="color:#063d79;margin:1em 0 -1em 1em;">Eine gemeinsame Initiative von</h3>
<script type="text/javascript">
	window.onload = function () {
		if ($$('div.error').length > 0)
			$$('div.error')[0].innerHTML = "Fehler: Ung&uuml;ltige Benutzerdaten!<br>Falls Sie Ihren Account noch nicht best&auml;tigt haben, pr&uuml;fen Sie bitte Ihr E-Mail-Postfach.";
	}
</script>