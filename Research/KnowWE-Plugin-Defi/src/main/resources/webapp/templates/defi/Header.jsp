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
       <!--<fmt:message key='actions.home' />-->
       <img src="KnowWEExtension/images/Logo_icd-forum.gif" height="101px" width="143px" alt="ICD-Forum Logo" />
    	ICD-Forum
    </a>
  </div>

  <div class="companylogo"></div>
  
  <div class="infobox">
   <div class="therapeut">
  		<h3>Therapeuth</h3>
  		<p>Dr. S. M. Schulz<br /> <%=last_login%></p>
  	</div>
  	<div class="gruppe">
  		<h3>Gruppe</h3>
  		<a href="Wiki.jsp?page=Gruppe"><img src="KnowWEExtension/images/Gruppe.jpg" height="98px" width="141px" alt="Gruppe" /></a>
  	</div>
  </div>
  
  <wiki:Include page="UserBox.jsp" />

  <br />
  <div class="searchbox"><wiki:Include page="SearchBox.jsp" /></div>
</div>