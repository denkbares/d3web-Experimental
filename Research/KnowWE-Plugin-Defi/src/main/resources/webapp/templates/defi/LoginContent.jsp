<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="com.ecyrd.jspwiki.auth.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
    String postURL = "";
    WikiContext ctx = WikiContext.findContext( pageContext );
    AuthenticationManager mgr = ctx.getEngine().getAuthenticationManager();

    if( mgr.isContainerAuthenticated() )
    {
        postURL = "j_security_check";
    }
    else
    {
        String redir = (String)ctx.getVariable("redirect");
        if( redir == null ) redir = ctx.getEngine().getFrontPage();
        postURL = ctx.getURL( WikiContext.LOGIN, redir );
    }

    boolean supportsCookieAuthentication = mgr.allowsCookieAuthentication();
%>
<%-- Additional stylesheets --%>
<link href="KnowWEExtension/css/general.css" type="text/css" rel="stylesheet">
<link href="KnowWEExtension/css/defi.css" type="text/css" rel="stylesheet">
<link href="KnowWEExtension/css/toolsMenuDecorator.css" type="text/css" rel="stylesheet">
<link href="KnowWEExtension/css/defaultMarkup.css" type="text/css" rel="stylesheet">
<style type="text/css">
#header {
	border-bottom:0;
}
#header div.infobox, #header div.userbox, #favorites {
	display:none;
}
#content {
	margin:5px 0 0 196px;
}
#footer a.data, #footer a.about, #footer a.start {
	display:none !important;
} 
</style>

<div id="login_defi"> 
<br />
<h2>Willkommen im ICD-Forum</h2><h2>Benutzeranmeldung f&uuml;r registrierte Teilnehmer</h2>
  
</div>

<wiki:TabbedSection defaultTab="${param.tab}">

<%-- Login functionality --%>
<wiki:Tab id="logincontent" title='<%=LocaleSupport.getLocalizedMessage(pageContext, "login.tab")%>'>
<%--<wiki:Include page='LoginTab.jsp'/>--%>


<form action="<%=postURL%>"
          id="login"
       class="wikiform"
    onsubmit="return Wiki.submitOnce(this);"
      method="post" accept-charset="<wiki:ContentEncoding />" >

<div class="center">

  <h3><fmt:message key="login.heading.login"><fmt:param><wiki:Variable var="applicationname" /></fmt:param></fmt:message></h3>

  <div class="formhelp"><fmt:message key="login.help"></fmt:message></div>

  <table>
    <tr>
      <td colspan="2" class="formhelp">
        <wiki:Messages div="error" topic="login"
                    prefix='<%=LocaleSupport.getLocalizedMessage(pageContext,"login.errorprefix")%>' />
      </td>
    </tr>
    <tr>
      <td><label for="j_username"><fmt:message key="login.login"/></label></td>
      <td><input type="text" size="24" value="<wiki:Variable var='uid' default='' />"
                 name="j_username" id="j_username" /></td>
    </tr>
    <tr>
      <td><label for="j_password"><fmt:message key="login.password"/></label></td>
      <td><input type="password" size="24"
                 name="j_password" id="j_password" /></td>
    </tr>
    <% if( supportsCookieAuthentication ) { %>
    <tr>
      <td><label for="j_remember"><fmt:message key="login.remember"/></label></td>
      <td><input type="checkbox"
                 name="j_remember" id="j_remember" /></td>
    </tr>
    <% } %>
    <tr>
      <td>&nbsp;</td>
      <td>
        <input type="hidden" name="redirect" value="<wiki:Variable var='redirect' default='' />" />
        <input type="submit" name="submitlogin" value="<fmt:message key='login.submit.login'/>" />
      </td>
    </tr>
    </table>

</div>
</form>

</wiki:Tab>

</wiki:TabbedSection>
<h3 style="color:#063d79;margin:1em 0 -1em 1em;">Eine gemeinsame Initiative von</h3>