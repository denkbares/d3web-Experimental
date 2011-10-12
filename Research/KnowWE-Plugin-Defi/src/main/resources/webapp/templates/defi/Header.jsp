<%@page import="de.knowwe.core.KnowWEEnvironment"%>
<%@page import="de.knowwe.jspwiki.JSPWikiKnowWEConnector"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*"%>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@ page import="de.knowwe.defi.*"%>
<%@page import="de.knowwe.defi.utils.DefiUtils"%><fmt:setLocale
	value="${prefs.Language}" />
<fmt:setBundle basename="templates.default" />
<%
	// *********************************************
	// Benutzername des Beraters:
	final String BERATER = "Dr. Stefan M. Schulz";
	// *********************************************

	WikiContext c = WikiContext.findContext(pageContext);
	String frontpage = c.getEngine().getFrontPage();
	JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
	JSPWikiKnowWEConnector wc = new JSPWikiKnowWEConnector(WikiEngine.getInstance(
			KnowWEEnvironment.getInstance().getContext(), null));
	String[] activeUsers = wc.getAllActiveUsers();
	boolean beraterOnline = false;

	for (String s : activeUsers) {
		if (s.equals(BERATER)) beraterOnline = true;
	}
%>

<div id="header">

	<div class="titlebox">
		<wiki:InsertPage page="TitleBox" />
	</div>

	<div class="applicationlogo">
		<a id="logo" href="<wiki:LinkTo page='<%=frontpage%>' format='url' />"
			title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> ">
			<!--<fmt:message key='actions.home' />--> <img
			src="KnowWEExtension/images/Logo_icd-forum.gif" height="101px"
			width="143px" alt="ICD-Forum Logo" /> ICD-Forum </a>
	</div>

	<div class="companylogo"></div>

		<div class="infobox">
			<div>
				<a href="Wiki.jsp?page=<%= BERATER %>" class="infobox_link" onmouseover="document.getElementById('infobox1').style.backgroundColor = '#eeeeee';" onmouseout="document.getElementById('infobox1').style.backgroundColor = '#F9F9F9';">
				<img src="KnowWEExtension/images/
				<% if (beraterOnline) { %>
					berater_farbig.jpg
				<% } else { %>
					berater_grau.jpg
				<% } %>
				" height="115px" width="86px" alt="Berater" style="margin:9px 0 3px 0;" />
				<span id="infobox1">Berater Dr. Schulz</span>
				</a>
			</div>
			<div>
				<a href="Wiki.jsp?page=Gruppe" class="infobox_link" onmouseover="document.getElementById('infobox2').style.backgroundColor = '#eeeeee';" onmouseout="document.getElementById('infobox2').style.backgroundColor = '#F9F9F9';"><img
				src="KnowWEExtension/images/Gruppe.jpg" height="98px" width="141px"
				alt="Gruppe" style="margin:14px 0 0 0;" />
				<span style="margin:15px 0 0 0;" id="infobox2">Gruppe</span></a>
			</div>
		</div>

	<wiki:Include page="UserBox.jsp" />

	<br />
	<%
		if (user.userIsAdmin()) {
	%>
	<div class="searchbox">
		<wiki:Include page="SearchBox.jsp" />
	</div>
	<%
		}
	%>
</div>