<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@page import="java.util.HashMap"%>
<%@ page import="org.apache.wiki.*" %>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  WikiPage wikipage = c.getPage();
  JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
%>
<div id="actionsTop" class="pageactions"> 
  <ul>

    <wiki:CheckRequestContext context='view|info|diff|upload|rename'>
    <wiki:Permission permission="edit">
	<li>
        <wiki:PageType type="page">
          <a href="<wiki:EditLink format='url' />" accesskey="e"  class="action edit"
            title="<fmt:message key='actions.edit.title'/>" ><fmt:message key='actions.edit'/></a>
        </wiki:PageType>
        <wiki:PageType type="attachment">
          <a href="<wiki:BaseURL/>Edit.jsp?page=<wiki:ParentPageName />" accesskey="e" class="action edit"
            title="<fmt:message key='actions.editparent.title'/>" ><fmt:message key='actions.editparent'/></a>
        </wiki:PageType>
    </li>
    </wiki:Permission>
    </wiki:CheckRequestContext>

    <%-- converted to popup menu by jspwiki-common.js--%>
  <wiki:Permission permission="edit">
   <% if(user.userIsAdmin()) { %>
    <li id="morebutton">
      <a href="<wiki:Link format='url' page='MoreMenu' />" class="action more"><fmt:message key="actions.more"/></a>
    </li>
   <% } %>
  </wiki:Permission>

  </ul>
</div>