<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<fmt:setBundle basename="templates.default"/>
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
</head>

<body class="view">
<div id="head"></div>
<div id="unilogo">
<a href="http://www.uni-wuerzburg.de/"><img src="templates/ircwiki/images/UniWue.gif" alt="Julius-Maximilians-Universität Würzburg" title="Julius-Maximilians-Universität Würzburg" border="0" height="72" width="169"></a>
<a href="/"><img src="templates/ircwiki/images/UniWueLS3Logo.gif" alt="Lehrstuhl für Informatik III" title="Lehrstuhl für Informatik III" border="0" height="72" width="570"></a>
</div>



<div id="wikibody" class="${prefs.Orientation}">
 
  <wiki:Include page="Header.jsp" />

  <div id="content">

    <div id="page">
	      <wiki:Include page="PageActionsTop.jsp"/>
	      <wiki:Content/>
	      <wiki:Include page="PageActionsBottom.jsp"/>
    </div>
    <wiki:Include page="Favorites.jsp"/>

	<div class="clearbox"></div>
  </div>

  <wiki:Include page="Footer.jsp" />

</div>

</body>
</html>