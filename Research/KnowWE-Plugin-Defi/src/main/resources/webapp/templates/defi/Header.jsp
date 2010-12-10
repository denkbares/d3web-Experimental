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
  	<div class="userinfobox">
  		<div class="usertext">
	  		<h3>Therapeut</h3>
	  		<p>Dr. S. M. Schulz</p>
	  		<p>Zuletzt online</p>
	  		<p>am 23.11.10</p>
	  		<p>um 15:30 Uhr</p>
  		</div>
  		<div class="userpic"></div>
  	</div>
  	<div class="userinfobox">
  		<div class="usertext">
	  		<h3 style="text-align:center;">Gruppe</h3>
			<div class="grouppic"></div>
  		</div>
  	</div>
  </div>

  <br />
  
  <div class="pagename"><wiki:PageName /></div>

  <div class="breadcrumbs"><fmt:message key="header.yourtrail"/><wiki:Breadcrumbs /></div>

</div>