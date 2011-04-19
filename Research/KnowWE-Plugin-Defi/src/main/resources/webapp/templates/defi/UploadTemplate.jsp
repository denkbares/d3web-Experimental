<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="de.d3web.we.jspwiki.JSPWikiUserContext" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext( pageContext );
  WikiPage wikipage = c.getPage();
  JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
  int attCount = c.getEngine().getAttachmentManager().listAttachments(c.getPage()).size();
  String attTitle = LocaleSupport.getLocalizedMessage(pageContext, "attach.tab");
  if( attCount != 0 ) attTitle += " (" + attCount + ")";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html id="top" xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title><fmt:message key="upload.title"><fmt:param><wiki:Variable var="applicationname"/></fmt:param></fmt:message></title>
  <wiki:Include page="commonheader.jsp"/>
  <meta name="robots" content="noindex,nofollow" />
    <% if(!user.userIsAdmin()) { %>
  <style type="text/css">
  	#menu-attach, #menu-info { display:none; }
  </style>
  <% } %>
</head>

<body>

<div id="wikibody" class="${prefs.Orientation}">

  <wiki:Include page="Header.jsp" />

  <div id="content">
	<wiki:Include page="Favorites.jsp"/>
	<div id="pagecontainer">
	    <div id="page">
	      <wiki:Include page="PageActionsTop.jsp"/>
	
	      <wiki:TabbedSection defaultTab="attachments" >
	        <wiki:Tab id="pagecontent" title='<%=LocaleSupport.getLocalizedMessage(pageContext, "view.tab")%>'
		  		     url="<%=c.getURL(WikiContext.VIEW, c.getPage().getName())%>"
		       accesskey="v" >
	        </wiki:Tab>
	        
	        <wiki:PageExists>
	        <wiki:Tab id="attachments" title="<%= attTitle %>" >
	          <wiki:Include page="AttachmentTab.jsp"/>
	        </wiki:Tab>
	        <wiki:Tab id="info" title='<%=LocaleSupport.getLocalizedMessage(pageContext, "info.tab")%>'
	                 url="<%=c.getURL(WikiContext.INFO, c.getPage().getName())%>"
	           accesskey="i" >
	        </wiki:Tab>
	
	        </wiki:PageExists>
	      </wiki:TabbedSection>
	
			  <% if(user.userIsAdmin()) { %>
		      <wiki:Include page="PageActionsBottom.jsp"/>
		      <% } %>
	
	    </div>
	</div>

	<div class="clearbox"></div>
  </div>

  <wiki:Include page="Footer.jsp" />

</div>
</body>

</html>