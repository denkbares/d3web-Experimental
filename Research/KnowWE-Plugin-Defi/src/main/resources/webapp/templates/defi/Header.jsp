<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@page import="de.d3web.we.jspwiki.JSPWikiUserContext" %>
<%@ page import="de.knowwe.defi.*" %>

<%@page import="de.knowwe.defi.utils.DefiUtils"%><fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  String frontpage = c.getEngine().getFrontPage(); 
  JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
  
  //load the last login of Schulz
  String last_login = DefiUtils.lastLogin();
%>

<div id="header">

  <div class="titlebox"><wiki:InsertPage page="TitleBox"/></div>

  <div class="applicationlogo" > 
    <a id="logo" href="<wiki:LinkTo page='<%=frontpage%>' format='url' />"
       title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> ">
       ICD FORUM
       <!--<fmt:message key='actions.home' />-->
    </a>
  </div>

  <div class="companylogo"></div>
  
  <div class="infobox">
  	<table>
  		<tr><th>Therapeut</th>
  			<td rowspan="2" class="no_userpic"></td>
  			<th>Gruppe</th>
  		</tr>
  		<tr><td>Dr. S. M. Schulz<br /> <%=last_login%>
  			</td>
  			<td class="group_pic"><a href="Wiki.jsp?page=Gruppe"><img src="KnowWEExtension/images/Gruppe.jpg" height="98px" width="141px" alt="Gruppe" /></a></td>
  		</tr>
  	</table>
  </div>
  
  <wiki:Include page="UserBox.jsp" />

  <br />
  <div class="searchbox"><wiki:Include page="SearchBox.jsp" /></div>
<% if (user.userIsAdmin()) { %>
  <div class="pagename"><wiki:PageName /></div>
  <div class="breadcrumbs">
    <wiki:Permission permission="edit">
  	<fmt:message key="header.yourtrail"/>
  	<wiki:Breadcrumbs />
  	  </wiki:Permission>
  </div>
<% } else { %>
	<div style="height:70px;"></div>
<% } %>

</div>