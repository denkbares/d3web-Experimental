<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="de.d3web.we.defi.*" %>

<%@page import="de.d3web.we.defi.utils.DefiUtils"%><fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  String frontpage = c.getEngine().getFrontPage(); 
  
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
  	<div class="searchbox"><wiki:Include page="SearchBox.jsp" /></div>
  </div>

  <div class="companylogo"></div>
  
  <wiki:Include page="UserBox.jsp" />
  
  <div class="infobox">
  	<table>
  		<tr><th>Therapeut</th>
  			<td rowspan="2" class="no_userpic"></td>
  			<th>Gruppe</th>
  		</tr>
  		<tr><td>Dr. S. M. Schulz<br /> <%=last_login%>
  			</td>
  			<td class="group_pic"></td>
  		</tr>
  	</table>
  </div>

  <br />
  
  <div class="pagename"><wiki:PageName /></div>

  <div class="breadcrumbs"><fmt:message key="header.yourtrail"/><wiki:Breadcrumbs /></div>

</div>