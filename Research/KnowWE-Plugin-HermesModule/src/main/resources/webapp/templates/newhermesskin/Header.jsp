<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  String frontpage = c.getEngine().getFrontPage(); 
%>

<div id="header">

  <span class="applicationlogospan" >
	<span class="applicationlogo" > 
      <a href="<wiki:LinkTo page='<%=frontpage%>' format='url' />"
	     title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> "><fmt:message key='actions.home' /></a>
    </span>
  </span>

  <!-- div class="pagename"><wiki:PageName /></div-->
  <!-- span class="eule-logo"> </span-->
  <span id="pagename-container">
    <span class="pagename">
  		<br>Hermes Wiki
    </span>
  </span>

	<span class="user-search-box">
	<wiki:Include page="UserBox.jsp" />
	<span class="searchbox">
		<wiki:Include page="SearchBox.jsp" />
  	</span>
  	</span>
  <!-- div class="breadcrumbs"><fmt:message key="header.yourtrail"/><wiki:Breadcrumbs /></div -->

</div>