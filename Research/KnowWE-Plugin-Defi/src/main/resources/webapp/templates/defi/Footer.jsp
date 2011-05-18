<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  String frontpage = c.getEngine().getFrontPage(); 
%> 
<div id="footer">
  <div class="logosDefi">
  	<img src="KnowWEExtension/images/unilogo4cohne.jpg" width="" height="60px" alt="Universität Würzburg" />
  	<img src="KnowWEExtension/images/klinik bad neustadt.png" width="205" height="60" alt="Klinik Bad Neustadt" />
  	<img src="KnowWEExtension/images/Uniklinikum_Wue_logo.png" width="" height="60" alt="Uniklinikum Würzburg" />
  	<img src="KnowWEExtension/images/verbundklinikum.png" width="" height="60" alt="Verbundsklinikum" />
  	<img src="KnowWEExtension/images/Logo_CHFC.gif" width="" height="60" alt="CHFC" />
  </div>

  <div class="footerlinks">
  	<a href="Wiki.jsp?page=Datenschutz">Datenschutz</a>
  	<a href="Wiki.jsp?page=About">&Uuml;ber uns</a>
  	<a href="Wiki.jsp?page=Impressum">Impressum</a>
    <a href="<wiki:LinkTo page='<%=frontpage%>' format='url' />" title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> "><fmt:message key='actions.home' /></a>
  </div>
  
 <!--  <div class="uniwuelogo">
  	<a href="http://www.uni-wuerzburg.de/"></a>
  </div>
   <div class="chfclogo"></div>
  <div style="clear: right;"></div>
  <div class="companylogo"></div>-->

  <div class="copyright"><wiki:InsertPage page="CopyrightNotice"/></div>

  <div class="wikiversion">
    <%=Release.APPNAME%> v<%=Release.getVersionString()%>
  </div>

  <div class="rssfeed">
    <wiki:RSSImageLink title="Aggregate the RSS feed" />
  </div>

</div>