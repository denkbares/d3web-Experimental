<%@page import="de.knowwe.defi.event.DefiPageEvent"%>
<%@page import="de.knowwe.core.event.EventManager"%>
<%@page import="com.ecyrd.jspwiki.WikiContext"%>
<%@page import="com.ecyrd.jspwiki.WikiEngine"%>
<%
	WikiEngine wiki = WikiEngine.getInstance( getServletConfig() );
    // Create wiki context and check for authorization
    WikiContext wikiContext = wiki.createContext( request, WikiContext.VIEW );
    String user = wikiContext.getCurrentUser().getName();
    EventManager.getInstance().fireEvent(new DefiPageEvent(user, "Zusatzmodul"));
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title></title>
</head>

<frameset rows="220,*,20" frameborder="0" framespacing="0" border="0">
  <frame src="navi.html" name="Navigation" frameborder="0">
  <frame src="inhalte.html" name="Inhalte" frameborder="0">
  <frame src="bottom.html" name="Deko" frameborder="0">
	<noframes>
		<body>



		</body>
	</noframes>
</frameset>
</html>