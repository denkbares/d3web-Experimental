<%@page import="de.knowwe.core.Environment"%>
<%@page import="de.knowwe.jspwiki.JSPWikiConnector"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*"%>
<%@page import="de.knowwe.defi.readbutton.DataMarkup"%>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@page import="de.knowwe.core.kdom.parsing.Section"%>
<%@page import="de.knowwe.core.kdom.parsing.Sections" %>
<%@page import="de.knowwe.core.kdom.Article"%>
<%@ page import="de.knowwe.defi.*"%>
<%@page import="de.knowwe.defi.utils.DefiUtils"%><fmt:setLocale
	value="${prefs.Language}" />
<fmt:setBundle basename="templates.default" />
<%
	final String BERATER = "Dr. Stefan M. Schulz";
	final String WELCOME_PAGE = "Startseite";
	final String WELCOME_PAGE_FIRSTTIME = WELCOME_PAGE + "_firstTime";

	WikiContext c = WikiContext.findContext(pageContext);
	String frontpage = c.getEngine().getFrontPage();
	JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
	JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
	Environment.getInstance().getContext(), null));
	String[] activeUsers = wc.getAllActiveUsers();
	boolean beraterOnline = false;

	for (String s : activeUsers) {
		if (s.equals(BERATER)) beraterOnline = true;
	}
	
	// if user has visited welcomepage, redirect him to welcomepage_firsttime
	boolean welcomePage_firstTime = false;
	if (user.getTitle().equals(WELCOME_PAGE)) {
		welcomePage_firstTime = true;
		String[] readpages = new String[0];
		
		Article userData = Environment.getInstance().getArticleManager(
		Environment.DEFAULT_WEB).getArticle(user.getUserName() + "_data");
		if (userData != null) {
			Section<DataMarkup> data = Sections.findSuccessor(
			userData.getSection(), DataMarkup.class);
			if (data != null && DataMarkup.getAnnotation(data, "readpages") != null) {
				// Hole alle gelesenen Readbuttons
				readpages = DataMarkup.getAnnotation(data, "readpages").split(";");
				// Ist gesuchter dabei?
				for (String s : readpages) {
					// Vergleiche pagenames und ids 
					if (s.split("::")[0].equals(WELCOME_PAGE_FIRSTTIME))
						welcomePage_firstTime = false;
				}
			}
		}
	}
	
if(welcomePage_firstTime && user.userIsAsserted()) {
%>
<script type="text/javascript">
	window.location.href = "Wiki.jsp?page=<%=WELCOME_PAGE_FIRSTTIME%>";
</script>
<% } %>

<div id="header">
	
	<div class="titlebox">
		<wiki:InsertPage page="TitleBox" />
	</div>

	<div class="applicationlogo">
		<a id="logo" href="Wiki.jsp?page=Startseite"
			title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> ">
			<!--<fmt:message key='actions.home' />--> <img
			src="KnowWEExtension/images/Logo_icd-forum.gif" height="101px"
			width="143px" alt="ICD-Forum Logo" /> ICD-Forum </a>
	</div>

	<div class="companylogo"></div>

		<div class="infobox">
			<span>Kontaktfunktion</span>
			<div>
				<a href=""  onclick="newChat('<%= BERATER %>', '<%= user.getUserName() %>');return false" class="infobox_link" onmouseover="document.getElementById('infobox1').style.backgroundColor = '#eeeeee';" onmouseout="document.getElementById('infobox1').style.backgroundColor = '#F9F9F9';">
				<img src="KnowWEExtension/images/
				<% if (beraterOnline) { %>
					berater_farbig.jpg
				<% } else { %>
					berater_grau.jpg
				<% } %>
				" height="92px" width="70px" alt="Berater" style="margin:9px 0 0 0;" />
				<span id="infobox1">Berater Dr. Schulz</span>
				</a>
			</div>
			<div>
				<a href="Wiki.jsp?page=Diskussion" class="infobox_link" onmouseover="document.getElementById('infobox2').style.backgroundColor = '#eeeeee';" onmouseout="document.getElementById('infobox2').style.backgroundColor = '#F9F9F9';"><img
				src="KnowWEExtension/images/Gruppe.png" height="100px" width="133px"
				alt="Gruppe" style="margin:3px 0 -2px 0;" />
				<span style="margin:0 0 0 0;" id="infobox2">Diskussion</span></a>
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