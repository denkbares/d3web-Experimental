<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default"/>
<%
  WikiContext c = WikiContext.findContext(pageContext);
  String frontpage = c.getEngine().getFrontPage(); 
%> 
<div id="footer">
  <div class="logosDefi">
  	<img src="KnowWEExtension/images/LogoLeiste.jpg" width="607" height="70" alt="Logos" />
  </div>

  <div class="footerlinks">
  	<!--  <a href="Wiki.jsp?page=Datenschutz" class="data">Datenschutz</a>-->
  	<!-- <a href="Wiki.jsp?page=About" class="about">&Uuml;ber uns</a> -->
  	<a href="Impressum.html" class="impressum">Impressum</a>
  	<a href="Bildnachweis.html" class="impressum">Bildnachweis</a>
    <a href="<wiki:LinkTo page='<%=frontpage%>' format='url' />" class="start" title="<fmt:message key='actions.home.title' ><fmt:param><%=frontpage%></fmt:param></fmt:message> "><fmt:message key='actions.home' /></a>
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