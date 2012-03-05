<%@page import="java.util.ResourceBundle"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="de.knowwe.kdom.defaultMarkup.DefaultMarkupType"%>
<%@page import="java.util.HashMap"%>
<%@page import="de.knowwe.defi.aboutMe.AboutMe"%>
<%@page import="de.knowwe.defi.readbutton.DataMarkup"%>
<%@page import="de.knowwe.jspwiki.JSPWikiUserContext"%>
<%@page import="de.knowwe.core.kdom.parsing.Section"%>
<%@page import="de.knowwe.core.kdom.parsing.Sections" %>
<%@page import="de.knowwe.core.kdom.KnowWEArticle"%>
<%@page import="de.knowwe.core.KnowWEEnvironment"%>
<%@ page import="com.ecyrd.jspwiki.WikiContext"%>
<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="javax.servlet.jsp.jstl.fmt.*"%>
<fmt:setLocale value="${prefs.Language}" />
<fmt:setBundle basename="templates.default" />
<%
	WikiContext c = WikiContext.findContext(pageContext);
	String avatar = "1000px-Comic_image_missing.svg.jpg";
	JSPWikiUserContext user = new JSPWikiUserContext(c, new HashMap<String, String>());
	if (user.userIsAsserted()) {
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
				KnowWEEnvironment.DEFAULT_WEB, user.getUserName());
		if (article != null) {
			Section<?> s = article.getSection();
			if (s != null && s.toString().contains("@avatar:")) {
				Section<AboutMe> sec = Sections.findSuccessor(s, AboutMe.class);
				avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
			}
		}
	}

	ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
	String notfallplan = rb.getString("defi.link.notfall")+user.getUserName();
%>
<div class="userbox">
	<span>Pers&ouml;nliche Funktionen</span>
	<div class="notfall">
		<a href="<%=notfallplan%>" target="blank" class="notfall"
			title="test"> <img src="KnowWEExtension/images/notfall.png"
			width="73px" height="69px" alt="Notfallplan" />
			<p>Notfallplan</p> </a>
	</div>
	<div class="message">
		<%
			if (user.userIsAsserted()) {
		%>
		<p>Willkommen,</p>
		<p><%=user.getUserName()%>!
		</p>
		<wiki:UserCheck status="asserted">
			<p>(nicht angemeldet)</p>
		</wiki:UserCheck>
		<a href="Wiki.jsp?page=Startseite" class="princ">Meine
			Startseite</a>
		<%
			}
			else {
		%>
		<br /> <span class="username anonymous"> <fmt:message
				key="fav.greet.anonymous" /> </span>
		<%
			}
		%>
	</div>
	<%
		if (user.userIsAsserted()) {
	%>
	<div class="avatar">
		<img src="KnowWEExtension/images/avatars/<%=avatar%>" height="95px"
			width="95px" alt="avatar" style="border:1px solid #063D79;" />
	</div>
	<%
		}
	%>

	<%-- action buttons --%>
	<table>
		<tr>
			<td><a href="Wiki.jsp?page=<%=user.getUserName()%>"
				class="action">Meine Einstellungen</a>
			</td>
			<td><wiki:UserCheck status="notAuthenticated">
					<wiki:CheckRequestContext context='!login'>
						<wiki:Permission permission="login">
							<a
								href="<wiki:Link jsp='Login.jsp' format='url'><wiki:Param 
      		name='redirect' value='<%=c.getEngine().encodeName(c.getName())%>'/></wiki:Link>"
								class="action login"
								title="<fmt:message key='actions.login.title'/>"><fmt:message
									key="actions.login" /> </a>
						</wiki:Permission>
					</wiki:CheckRequestContext>
				</wiki:UserCheck> <wiki:UserCheck status="authenticated">
					<a href="<wiki:Link jsp='Logout.jsp' format='url' />"
						class="action logout"
						title="<fmt:message key='actions.logout.title'/>"><fmt:message
							key="actions.logout" /> </a>
					<%--onclick="return( confirm('<fmt:message key="actions.confirmlogout"/>') && (location=this.href) );"--%>
				</wiki:UserCheck>
			</td>
		</tr>
	</table>
</div>