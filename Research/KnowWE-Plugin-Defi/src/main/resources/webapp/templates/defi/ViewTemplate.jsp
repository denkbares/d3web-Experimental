<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@page import="de.knowwe.core.Environment"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="de.knowwe.jspwiki.JSPWikiConnector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*" %>
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  WikiPage wikipage = c.getPage();
  JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
  JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
			Environment.getInstance().getContext(), null));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html id="top" xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title>
    <fmt:message key="view.title.view">
      <fmt:param><wiki:Variable var="ApplicationName" /></fmt:param>
      <fmt:param><wiki:PageName /></fmt:param>
    </fmt:message>
  </title>
  <wiki:Include page="commonheader.jsp"/>
  <wiki:CheckVersion mode="notlatest">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckVersion>
  <wiki:CheckRequestContext context="diff|info">
    <meta name="robots" content="noindex,nofollow" />
  </wiki:CheckRequestContext>
  <wiki:CheckRequestContext context="!view">
    <meta name="robots" content="noindex,follow" />
  </wiki:CheckRequestContext>
  <% if(!user.userIsAdmin()) { %>
  <style type="text/css">
  	#menu-pagecontent, #menu-attach, #menu-info { display:none; }
  	a.action.edit, a.action.more { display:none; }
  </style>
  <% } %>
  <%  if (Environment.getInstance().getArticle(user.getWeb(), user.getTitle()) == null) { %>
	  <link rel="stylesheet" media="screen" type="text/css" href="templates/defi/jspwiki.css" />
	  <link rel="stylesheet" media="screen" type="text/css" href="KnowWEExtension/css/general.css" />
	  <link rel="stylesheet" media="screen" type="text/css" href="KnowWEExtension/css/defi.css" />
	  <link rel="stylesheet" media="screen" type="text/css" href="KnowWEExtension/css/defi-table.css" />
	  <script type="text/javascript">
	  	window.onload = function() {
	  		$$('div.information')[0].innerHTML = "Diese Seite existiert nicht. " 
	  		+ "<a href='' onclick='history.back();return false'>Hier geht es zur&uuml;ck zur letzten Seite.</a>";
	  		$$('div#actionsBottom')[0].innerHTML = "";
	  	}
	  </script>
  <% } %>
</head>

<body class="view">

<div id="wikibody" class="${prefs.Orientation}">
 
  <wiki:Include page="Header.jsp" />

  <div id="content">
	<wiki:Include page="Favorites.jsp"/>
  	<div id="pagecontainer">
	    <div id="page">
		      <wiki:Include page="PageActionsTop.jsp"/>
		      <wiki:Content/>
		      <% if(user.userIsAdmin()) { %>
		      <wiki:Include page="PageActionsBottom.jsp"/>
		      <% } else { %>
		      <div style="height:20px;"></div>
		      <% } %>
	    </div>
    </div>

	<div class="clearbox"></div>
  </div>

  <wiki:Include page="Footer.jsp" />

</div>

</body>
</html>