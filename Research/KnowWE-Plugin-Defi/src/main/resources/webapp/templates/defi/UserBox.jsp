<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
%>
<div class="userbox">

<table>
	<tr>
		<th style="width: 310px;">
	
  <wiki:UserCheck status="anonymous">
    <span class="username anonymous">
      <fmt:message key="fav.greet.anonymous" />
    </span>
  </wiki:UserCheck>
  <wiki:UserCheck status="asserted">
    <span class="username asserted">
      <fmt:message key="fav.greet.asserted">
      <fmt:param><wiki:Translate>[<wiki:UserName />]</wiki:Translate></fmt:param>
    </fmt:message>
    </span>
  </wiki:UserCheck>
  <wiki:UserCheck status="authenticated">
    <span class="username authenticated">
      <fmt:message key="fav.greet.authenticated">
        <fmt:param><wiki:Translate>[<wiki:UserName />]</wiki:Translate></fmt:param>
      </fmt:message>
    </span>
  </wiki:UserCheck>
  
		</th>
		<td class="no_userpic"></td>
	</tr>
	<tr colspan="2"><td>
  <%-- action buttons --%>
  <wiki:UserCheck status="notAuthenticated">
  <wiki:CheckRequestContext context='!login'>
    <wiki:Permission permission="login">
      <a href="<wiki:Link jsp='Login.jsp' format='url'><wiki:Param 
         name='redirect' value='<%=c.getEngine().encodeName(c.getName())%>'/></wiki:Link>" 
        class="action login"
        title="<fmt:message key='actions.login.title'/>"><fmt:message key="actions.login"/></a>
    </wiki:Permission>
  </wiki:CheckRequestContext>
  </wiki:UserCheck>
  
  <wiki:UserCheck status="authenticated">
   <a href="<wiki:Link jsp='Logout.jsp' format='url' />" 
     class="action logout"
     title="<fmt:message key='actions.logout.title'/>"><fmt:message key="actions.logout"/></a>
   <%--onclick="return( confirm('<fmt:message key="actions.confirmlogout"/>') && (location=this.href) );"--%>
  </wiki:UserCheck>

  <wiki:CheckRequestContext context='!prefs'>
  <wiki:CheckRequestContext context='!preview'>
    <a href="<wiki:Link jsp='UserPreferences.jsp' format='url' ><wiki:Param name='redirect'
      value='<%=c.getEngine().encodeName(c.getName())%>'/></wiki:Link>"
      class="action prefs" accesskey="p"
      title="<fmt:message key='actions.prefs.title'/>"><fmt:message key="actions.prefs" />
    </a>
  </wiki:CheckRequestContext>
  </wiki:CheckRequestContext>
  </td></tr>
</table>

  <div class="clearbox"></div>

</div>