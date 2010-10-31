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

<%-- start change --%>
<!-- <img width="780" height="20" usemap="#topleiste" alt="" src="templates/biolog/images/top_leiste.jpg"/>
<map name="topleiste">
	<area alt="" href="" coords="551,5,568,18" shape="rect"/>
	<area alt="" href="" coords="574,5,595,18" shape="rect"/>
	<area alt="" href="" coords="728,6,772,18" shape="rect"/>
	<area alt="" href="" coords="655,7,715,18" shape="rect"/>
	<area alt="" href="" coords="610,6,645,18" shape="rect"/>
</map>-->
<ul class="biolog_top">
 <li>
   <wiki:UserCheck status="notAuthenticated">
  <wiki:CheckRequestContext context='!login'>
    <wiki:Permission permission="login">
      <a href="<wiki:Link jsp='Login.jsp' format='url'><wiki:Param 
         name='redirect' value='<%=c.getEngine().encodeName(c.getName())%>'/></wiki:Link>" 
        class="login"
        title="<fmt:message key='actions.login.title'/>"><fmt:message key="actions.login"/></a>
    </wiki:Permission>
  </wiki:CheckRequestContext>
  </wiki:UserCheck>
  
  <wiki:UserCheck status="authenticated">
   <a href="<wiki:Link jsp='Logout.jsp' format='url' />" 
     class="logout"
     title="<fmt:message key='actions.logout.title'/>"><fmt:message key="actions.logout"/></a>
   <%--onclick="return( confirm('<fmt:message key="actions.confirmlogout"/>') && (location=this.href) );"--%>
  </wiki:UserCheck>
 </li>
 <li>|</li>
 <li><a href="http://biolog-europe.org/impressum.html">Kontakt</a></li>
 <li>|</li>
 <li><a href="http://biolog-europe.org/impressum.html">Impressum</a></li>
 <li>|</li>
 <li><a href="http://biolog-europe.org/index.html">Home</a></li>
</ul>


<%-- start change --%>
<div id="biolog-heder-images" style="width:100%">
<img src="templates/biolog/images/biolog_logo.png" style="float:left"/>
<img src="templates/biolog/images/quad_or.png" style="float:left"/>
<img src="templates/biolog/images/bmbf_logo.png" style="float:right"/>
</div>
<%-- end change --%>

<div class="clearbox"></div>
  <%--<div class="titlebox"><wiki:InsertPage page="TitleBox"/></div> --%>
  <%--
  <div class="applicationlogo" > 
    <a href="<wiki:LinkTo page='<%=frontpage%>' format='url' />"
       title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> "><fmt:message key='actions.home' /></a>
  </div>--%>
  <%--<div class="companylogo"></div>--%>
  <%--<wiki:Include page="UserBox.jsp" /> --%>
<%-- end change --%>

  <div class="pagename"><wiki:PageName /></div>

<%-- start change --%>
  <%--<div class="searchbox"><wiki:Include page="SearchBox.jsp" /></div> --%>
  <%--<div class="breadcrumbs"><fmt:message key="header.yourtrail"/><wiki:Breadcrumbs /></div>--%>
<%-- end change --%>
</div>