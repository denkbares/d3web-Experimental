<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %>
<%@ page import="org.apache.wiki.*" %>
<%@ page import="org.apache.wiki.auth.*" %>
<%@ page import="org.apache.wiki.auth.user.*" %>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.*" %>
<fmt:setLocale value="${prefs.Language}"/>
<fmt:setBundle basename="templates.default"/>
<%
	/* dateformatting not yet supported by wiki:UserProfile tag - diy */
	WikiContext wikiContext = WikiContext.findContext(pageContext);
	UserManager manager = wikiContext.getEngine().getUserManager();
	UserProfile profile = manager.getUserProfile(wikiContext.getWikiSession());
%>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-2.1.0.min.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-ui.min.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-treeTable.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-tooltipster.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-plugin-collection.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery.mousewheel.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/simpleStorage.min.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/jquery-compatibility.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-helper.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Defi.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Defi-Alert.js'></script>
<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Defi-DoubleOptInRegistration.js'></script>
<h3>
	Registrierung
</h3>
<br>
<table>

	<!-- Login name -->
	<tr>
		<td><label for="loginname"><fmt:message key="prefs.loginname"/></label></td>
		<td>
			<wiki:UserProfile property="canChangeLoginName">
				<input type="text" name="loginname" id="loginname"
					   size="20" value="<wiki:UserProfile property='loginname' />"/>
			</wiki:UserProfile>
			<wiki:UserProfile property="!canChangeLoginName">
				<!-- If user can't change their login name, it's because the container manages the login -->
				<wiki:UserProfile property="new">
					<div class="warning"><fmt:message
							key="prefs.loginname.cannotset.new"/></div>
				</wiki:UserProfile>
				<wiki:UserProfile property="exists">
					<span class="formvalue"><wiki:UserProfile property="loginname"/></span>

					<div class="warning"><fmt:message
							key="prefs.loginname.cannotset.exists"/></div>
				</wiki:UserProfile>
			</wiki:UserProfile>
		</td>
	</tr>

	<!-- Password; not displayed if container auth used -->
	<wiki:UserProfile property="canChangePassword">
		<tr>
			<td><label for="password"><fmt:message key="prefs.password"/></label></td>
			<td>
					<%--FIXME Enter Old PW to validate change flow, not yet treated by JSPWiki
					<label for="password0">Old</label>&nbsp;
					<input type="password" name="password0" id="password0" size="20" value="" />
					&nbsp;&nbsp;--%>
				<input type="password" name="password" id="password" size="20" value=""/>
			</td>
		</tr>
		<tr>
			<td><label for="password2"><fmt:message key="prefs.password2"/></label></td>
			<td>
				<input type="password" name="password2" id="password2" size="20" value=""/>
					<%-- extra validation ? min size, allowed chars? --%>
			</td>
		</tr>
	</wiki:UserProfile>

	<!-- Full name -->
	<tr>
		<td><label for="fullname"><fmt:message key="prefs.fullname"/></label></td>
		<td>
			<input type="text" name="fullname" id="fullname"
				   size="20" value="<wiki:UserProfile property='fullname'/>"/>
			<span class="formhelp"><fmt:message key="prefs.fullname.description"/></span>
		</td>
	</tr>

	<!-- E-mail -->
	<tr>
		<td><label for="email"><fmt:message key="prefs.email"/></label></td>
		<td>
			<input type="text" name="email" id="email"
				   size="20" value="<wiki:UserProfile property='email' />"/>
			<span class="formhelp"><fmt:message key="prefs.email.description"/></span>
		</td>
	</tr>

	<wiki:UserProfile property="exists">
		<tr class="additinfo">
			<td><label><fmt:message key="prefs.roles"/></label></td>
			<td>
				<div class="formvalue"><wiki:UserProfile property="roles"/></div>
			</td>
		</tr>
		<tr class="additinfo">
			<td><label><fmt:message key="prefs.groups"/></label></td>
			<td>
					<%-- TODO this should become clickable group links so you can immediately go and look at them if you want --%>
				<div class="formvalue"><wiki:UserProfile property="groups"/></div>
				<div class="formhelp"><fmt:message key="prefs.acl.info"/></div>
			</td>
		</tr>

		<tr class="additinfo">
			<td><label><fmt:message key="prefs.creationdate"/></label></td>
			<td class="formvalue">
					<%--<wiki:UserProfile property="created"/>--%>
				<fmt:formatDate value="<%= profile.getCreated() %>"
								pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}"/>
			</td>
		</tr>
		<tr class="additinfo">
			<td><label><fmt:message key="prefs.profile.lastmodified"/></label></td>
			<td class="formvalue">
					<%--<wiki:UserProfile property="modified"/>--%>
				<fmt:formatDate value="<%= profile.getLastModified() %>"
								pattern="${prefs.DateFormat}" timeZone="${prefs.TimeZone}"/>
			</td>
		</tr>
	</wiki:UserProfile>

	<tr>
		<td>&nbsp;</td>
		<td>
			<button type="button" onclick="registerUserDoubleOptIn();">Registrieren</button>
			<wiki:UserCheck status="assertionsAllowed">
				<div class="formhelp"><fmt:message key="prefs.cookie.info"/></div>
			</wiki:UserCheck>
		</td>
	</tr>
</table>

