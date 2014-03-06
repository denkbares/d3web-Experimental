<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="de.knowwe.core.Environment"%>
<%@page import="de.knowwe.jspwiki.JSPWikiConnector"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*"%>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@page import="de.knowwe.core.event.EventManager"%>
<%@page import="de.knowwe.core.kdom.parsing.Section"%>
<%@page import="de.knowwe.core.kdom.parsing.Sections"%>
<%@page import="de.knowwe.core.kdom.Article"%>
<%@ page import="de.knowwe.defi.*"%>
<%@page import="de.knowwe.defi.forum.DiscussionUtils"%>
<%@page import="de.knowwe.defi.readbutton.ReadbuttonUtilities"%>
<%@page import="de.knowwe.defi.utils.DefiUtils"%>
<%@page import="de.knowwe.defi.event.DefiPageEvent"%>

<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default" />

<wiki:UserCheck status="asserted">
	<script type="text/javascript">
	if(!document.URL.match(/Login.jsp/))
		window.location.href = "Login.jsp";
	</script>
</wiki:UserCheck>
<%
	final String WELCOME_PAGE = "Startseite";
	final String WELCOME_PAGE_FIRSTTIME = WELCOME_PAGE + "_firstTime";
	final String BUTTON_ID = "firstpage";

	WikiContext c = WikiContext.findContext(pageContext);
	String frontpage = c.getEngine().getFrontPage();
	JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
	JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(Environment.getInstance().getContext(), null));
	EventManager.getInstance().fireEvent(new DefiPageEvent(user.getUserName(), user.getTitle()));
	String[] activeUsers = wc.getAllActiveUsers();
	String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString("defi.berater");
	List<String> activeUserList = Arrays.asList(activeUsers);
	String therapistImg = activeUserList.contains(berater) ? "KnowWEExtension/images/berater_farbig.jpg" : "KnowWEExtension/images/berater_grau.jpg";

	// new message?
	String groupImg = DiscussionUtils.userHasNewMessage(user.getUserName()) ? "KnowWEExtension/images/GruppeNeueNachricht.png" : "KnowWEExtension/images/Gruppe.png";
	
	// if user has visited welcomepage, redirect him to welcomepage_firsttime
	boolean welcomePage_firstTime = false;
	if (user.getTitle().equals(WELCOME_PAGE)) {
		welcomePage_firstTime = !ReadbuttonUtilities.isPageRated(BUTTON_ID, user.getUserName());
	}
	
if(welcomePage_firstTime) {
%>
<script type="text/javascript">
	if(!document.URL.match(/Login.jsp/))
		window.location.href = "Wiki.jsp?page=<%=WELCOME_PAGE_FIRSTTIME%>";
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
			title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> ">
			<!--<fmt:message key='actions.home' />--> <img
			src="KnowWEExtension/images/Logo_icd-forum.gif" height="101px"
			width="143px" alt="ICD-Forum Logo" /> ICD-Forum
		</a>
	</div>

	<div class="companylogo"></div>

	<div class="infobox">
		<span>Kontakt-Funktionen</span>
		<div>
			<a href=""
				onclick="newChat('<%=berater%>', '<%=user.getUserName()%>');return false"
				class="infobox_link"
				onmouseover="document.getElementById('infobox1').style.backgroundColor = '#eeeeee';"
				onmouseout="document.getElementById('infobox1').style.backgroundColor = '#F9F9F9';">
				<img src="<%=therapistImg%>" height="92px" width="70px"
				alt="Berater" style="margin: 9px 0 0 0;" /> <span id="infobox1">Berater
					Dr. Schulz</span>
			</a>
		</div>
		<div>
			<a href="Wiki.jsp?page=Diskussion" class="infobox_link"
				onmouseover="document.getElementById('infobox2').style.backgroundColor = '#eeeeee';"
				onmouseout="document.getElementById('infobox2').style.backgroundColor = '#F9F9F9';">
				<img src="<%=groupImg%>" height="100px" width="133px" alt="Gruppe"
				style="margin: 3px 0 -2px 0;" /> <span style="margin: 0 0 0 0;"
				id="infobox2">Diskussion</span>
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