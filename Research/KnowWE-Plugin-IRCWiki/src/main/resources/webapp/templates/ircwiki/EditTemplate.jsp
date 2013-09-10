<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html id="top" xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title>
    <wiki:CheckRequestContext context="edit">
    <fmt:message key="edit.title.edit">
      <fmt:param><wiki:Variable var="ApplicationName" /></fmt:param>
      <fmt:param><wiki:PageName /></fmt:param>
    </fmt:message>
    </wiki:CheckRequestContext>
    <wiki:CheckRequestContext context="comment">
    <fmt:message key="comment.title.comment">
      <fmt:param><wiki:Variable var="ApplicationName" /></fmt:param>
      <fmt:param><wiki:PageName /></fmt:param>
    </fmt:message>
    </wiki:CheckRequestContext>
  </title>
  <meta name="robots" content="noindex,follow" />
  <wiki:Include page="commonheader.jsp"/>
</head>

<body <wiki:CheckRequestContext context='edit'>class="edit"</wiki:CheckRequestContext><wiki:CheckRequestContext context='comment'>class="comment"</wiki:CheckRequestContext> >
<div id="head"></div>
<div id="unilogo">
<a href="http://www.uni-wuerzburg.de/"><img src="templates/ircwiki/images/UniWue.gif" alt="Julius-Maximilians-Universit&auml;t W&uuml;rzburg" title="Julius-Maximilians-Universit&auml;t W&uuml;rzburg" border="0" height="72" width="169"></a>
<a href="/"><img src="templates/ircwiki/images/UniWueLS3Logo.gif" alt="Lehrstuhl f&uuml;r Informatik III" title="Lehrstuhl f&uuml;r Informatik III" border="0" height="72" width="570"></a>
</div>

<div id="wikibody" class="${prefs.Orientation}">

  <wiki:Include page="Header.jsp" />

  <div id="content">

    <div id="page">
     <wiki:UserCheck status="authenticated">
	      	<wiki:Include page="PageActionsTop.jsp"/>
	  </wiki:UserCheck>
      <wiki:Content/>
	      <wiki:UserCheck status="authenticated">
	      	<wiki:Include page="PageActionsBottom.jsp"/>
	      </wiki:UserCheck>
	      <wiki:UserCheck status="notAuthenticated">
	      	<br />
	      </wiki:UserCheck>
	</div>

    <wiki:Include page="Favorites.jsp"/> 

	<div class="clearbox"></div>
  </div>	

  <wiki:Include page="Footer.jsp" />

</div>

</body>
</html>