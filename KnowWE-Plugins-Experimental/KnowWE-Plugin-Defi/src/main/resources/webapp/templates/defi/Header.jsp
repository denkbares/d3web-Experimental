<%@page import="de.knowwe.kdom.dashtree.DashTreeElement"%>
<%@page import="de.knowwe.defi.menu.MenuUtilities"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="de.knowwe.core.Environment"%>
<%@page import="de.knowwe.jspwiki.JSPWikiConnector"%>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.HashMap"%>
<%@ page import="org.apache.wiki.*"%>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@page import="com.denkbares.events.EventManager" %>
<%@page import="de.knowwe.core.kdom.parsing.Section"%>
<%@page import="de.knowwe.core.kdom.parsing.Sections"%>
<%@page import="de.knowwe.core.kdom.Article"%>
<%@ page import="de.knowwe.defi.*"%>
<%@page import="de.knowwe.defi.forum.DiscussionUtils"%>
<%@page import="de.knowwe.defi.readbutton.ReadbuttonUtilities"%>
<%@page import="de.knowwe.defi.utils.DefiUtils"%>
<%@page import="de.knowwe.defi.event.DefiPageEvent"%>
<%
	final String WELCOME_PAGE = "Startseite";
	final String WELCOME_PAGE_FIRSTTIME = WELCOME_PAGE + "_firstTime";
	final String WELCOME_PAGE_BUTTON = "firstpage";
	final String OLD_BROWSER_BUTTON = "oldbrowser";
%>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default" />

<wiki:UserCheck status="asserted">
	<script type="text/javascript">
	if(!document.URL.match(/Login.jsp/))
		window.location.href = "Login.jsp";
	</script>
</wiki:UserCheck>
<%
	WikiContext c = WikiContext.findContext(pageContext);
	JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
	JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(Environment.getInstance().getContext(), null));
%>

<%
	// if user is not allowed to visit this unit, show 404
	for (Section<DashTreeElement> unit : MenuUtilities.getAllUnits())  {
		
		// find visited unit, check whether it is opened or closed
		if (MenuUtilities.getUnitPagename(unit).equals(user.getTitle()) && !MenuUtilities.isUnitOpen(unit, user.getUserName())) {
%>
			<script type="text/javascript">
			if(!document.URL.match(/Login.jsp/))
				window.location.href = "403.jsp";
			</script>
<%
		}
	}
%>

<%
	// fire page event
	EventManager.getInstance().fireEvent(new DefiPageEvent(user.getUserName(), user.getTitle()));
	
	// therapist online?
	String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString("defi.berater");
	String therapistImg = Arrays.asList(wc.getAllActiveUsers()).contains(berater) ? "KnowWEExtension/images/berater_farbig.jpg" : "KnowWEExtension/images/berater_grau.jpg";
	
	// sort user names for chat
	String[] usernames = { berater , user.getUserName() };
	Arrays.sort(usernames);
	
	// new message?
	boolean newTherapistMsg = DiscussionUtils.userHasNewTherapistMessage(user.getUserName());
	boolean newChatMsg = DiscussionUtils.userHasNewUserMessage(user.getUserName());
	if (newTherapistMsg && user.getUserName().equals(berater)) {
		newTherapistMsg = false;
		newChatMsg = true;
	}
	
	// if user has old browser and is on the welcome page
	if (user.getTitle().equals(WELCOME_PAGE) || user.getTitle().equals(WELCOME_PAGE_FIRSTTIME)) {
		if (!ReadbuttonUtilities.isPageRated(OLD_BROWSER_BUTTON, user.getUserName())) {
%>
<script type="text/javascript">
	if(!document.URL.match(/Login.jsp/)) {
		function get_browser(){
			var N=navigator.appName, ua=navigator.userAgent, tem;
			var M=ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
			if(M && (tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
			M=M? [M[1], M[2]]: [N, navigator.appVersion, '-?'];
			return M[0];
		}
		function get_browser_version(){
			var N=navigator.appName, ua=navigator.userAgent, tem;
			var M=ua.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
			if(M && (tem= ua.match(/version\/([\.\d]+)/i))!= null) M[2]= tem[1];
			M=M? [M[1], M[2]]: [N, navigator.appVersion, '-?'];
			return M[1];
		}
		var browser=get_browser();
		var browser_version= parseFloat(get_browser_version());
		var oldBrowserPage = "Browser";
		
		if (browser == "Firefox" && browser_version < 25) {
			window.location.href = "Wiki.jsp?page=" + oldBrowserPage;
		} else if (browser == "Chrome" && browser_version < 25) {
			window.location.href = "Wiki.jsp?page=" + oldBrowserPage;
		} else if (browser == "MSIE" && browser_version < 9) {
			window.location.href = "Wiki.jsp?page=" + oldBrowserPage;
		} else if (browser == "Safari" && browser_version < 5) {
			window.location.href = "Wiki.jsp?page=" + oldBrowserPage;
		}
	}	
</script>
<%
		}
	}
	
	// if user has visited welcomepage, redirect him to welcomepage_firsttime
	boolean welcomePage_firstTime = false;
	if (user.getTitle().equals(WELCOME_PAGE)) {
		welcomePage_firstTime = !ReadbuttonUtilities.isPageRated(WELCOME_PAGE_BUTTON, user.getUserName());
	}
	if(welcomePage_firstTime) {
%>
<script type="text/javascript">
	if(!document.URL.match(/Login.jsp/)) {
		window.location.href = "Wiki.jsp?page=<%=WELCOME_PAGE_FIRSTTIME%>";
	}	
</script>
<%
	}
%>
<div id="header">

	<div class="titlebox">
		<wiki:InsertPage page="TitleBox" />
	</div>

	<div class="applicationlogo">
		<a id="logo" href="Wiki.jsp?page=Startseite"
			title="<fmt:message key='actions.home.title' ><fmt:param><%=c.getEngine().getFrontPage()%></fmt:param></fmt:message> ">
			<!--<fmt:message key='actions.home' />--> <img
			src="KnowWEExtension/images/Logo_icd-forum.gif" height="101px"
			width="143px" alt="ICD-Forum Logo" /> ICD-Forum
		</a>
	</div>

	<div class="companylogo"></div>

	<div class="infobox">
		<span class="label topic">Kontakt-Funktionen</span>
		
		<!-- TODO: Neu designen mit css, container -> relativ, inhalt absolut -->
		
		<div class="therapistbox">
			<a href="" onclick="newChat('<%=usernames[0]%>', '<%=usernames[1]%>');return false"
				onmouseover="document.getElementById('infobox1').style.backgroundColor = '#eeeeee';"
				onmouseout="document.getElementById('infobox1').style.backgroundColor = '#F9F9F9';">
				
				<img src="<%=therapistImg%>" height="92px" width="70px" alt="Berater" />
				<% if (newTherapistMsg) { %>
					<p><span>Sie haben Post!</span></p>
				<% } %>
				<span class="label" id="infobox1">Berater Dr. Schulz</span>
			</a>
		</div>
		
		<div class="chatbox">
			<a href="Wiki.jsp?page=Diskussion"
				onmouseover="document.getElementById('infobox2').style.backgroundColor = '#eeeeee';"
				onmouseout="document.getElementById('infobox2').style.backgroundColor = '#F9F9F9';">
				<img src="KnowWEExtension/images/Gruppe.png" height="100px" width="133px" alt="Gruppe"/>
				<% if (newChatMsg) { %>
					<p><span>Sie haben Post!</span></p>
				<% } %>
				<span class="label" id="infobox2">Diskussion</span>
			</a>
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