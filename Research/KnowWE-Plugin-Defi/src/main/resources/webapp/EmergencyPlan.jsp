<%@page import="com.ecyrd.jspwiki.*" %>
<%@page import="de.knowwe.jspwiki.*" %>
<%@page import="de.knowwe.core.*" %>
<%@page import="de.knowwe.core.action.*" %>
<%@page import="de.knowwe.core.user.*" %>
<%@page import="de.knowwe.defi.*" %>
<%@page import="de.knowwe.defi.emergency.*" %>
<%@page import="java.util.Map"%>
<%
	//Create wiki context; authorization check not needed
	WikiEngine wiki = WikiEngine.getInstance( getServletConfig() );
	WikiContext wikiContext = wiki.createContext( request, WikiContext.VIEW );
	
	// Check if KnowWE is initialized
	if (!KnowWEEnvironment.isInitialized()) {
		KnowWEEnvironment.initKnowWE(new JSPWikiKnowWEConnector(wiki));
	}
	// We need to do this, because the paramterMap is locked!
	Map<String, String> parameters = UserContextUtil.getParameters(request);
	
	// Add user
	if (!parameters.containsKey(KnowWEAttributes.USER)) {
		parameters.put(KnowWEAttributes.USER, wikiContext.getWikiSession().getUserPrincipal().getName());
	}
	// Create AuthenticationManager instance
	AuthenticationManager manager = new JSPAuthenticationManager(wikiContext);
	
	// Create action context
	UserActionContext context = new ActionContext(parameters.get("action"), AbstractActionServlet.getActionFollowUpPath(request), parameters, request, response, wiki.getServletContext(), manager);
	
	String requestUser = (String)parameters.get("user");
	boolean requestUserCorrect = (requestUser.equals(context.getUserName()));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html id="top" xmlns="http://www.w3.org/1999/xhtml">
<head>
  
<style type="text/css" media="all">
	table 
	{ border: 2px dashed black; background-color: firebrick; margin-bottom: 0%; padding: 0%; vertical-align: 0%; }
	
	.other 
	{ border: 0px; background-color: transparent; }
	
	#inner 
	{ border-top: 0px; border-left: 0px; border-right: 0px; border-bottom: 1px; border-style: solid; border-color: black; font-family: Arial, Helvetica, sans-serif;
      font-size: 12pt; text-align: center; vertical-align: top; line-height: 0.98em; }
	
	.inner2 
	{ border-top: 0px; border-left: 0px; border-right: 0px; border-bottom: 0px; border-style: solid; border-color: black; font-family: Arial, Helvetica, sans-serif;
      font-size: 12pt; text-align: center; vertical-align: top; line-height: 0.98em; }
	
	th 
	{ border-top: 0px; border-left: 0px; border-right: 0px; border-bottom: 1px; border-style: solid; border-color: black; font-family: Arial, Helvetica, sans-serif;
      font-size: 12pt; text-align: center; vertical-align: top; line-height: 0.98em; }
	
	td,tr 
	{ border: 0px; border-style: solid; border-color: red; padding-top: 18%; padding-left: 6%; padding-bottom: 0%; font-family: Arial, Helvetica, sans-serif;
      font-size: 12pt; text-align: left; vertical-align: top; line-height: 1.2em; }

	.engl
	{font-size:11pt; font-style:italic}
	
	.inhalt
	{color: blue; font-size: 12pt;}
</style>
</head>
<body>
<%
if(requestUserCorrect) {
%>
<table border="1" style="border-collaps:collaps">
	<tr>
		<td width="280px" height="190px" style="background-image:url(KnowWEExtension/images/front3.png); background-repeat:no-repeat">	
		<br />
		<br />
		<br />
		<br />
		<br />
		<br />
			<table class="other" border="0">
				<tr>
					<th width="239px" height="15px">
					<span class="inhalt"><%=context.getUserName()%></span>
					</th>
				</tr>
			</table>		
		</td>
	</tr>
	<tr>
		<td width="280px" height="190px" class="tel" style="background-image: url(KnowWEExtension/images/tel.png); background-repeat: no-repeat">
		Mein Kardiologe 
		<%=EmergencyPlanUtils.getCardiologist(context.getUserName())%>
		
		Mein Hausarzt 
		<%=EmergencyPlanUtils.getPhysician(context.getUserName())%>

		Im Notfall zu verst&auml;ndigen 
		<%=EmergencyPlanUtils.getEmergencyPerson(context.getUserName())%>
		</td>
	</tr>
	
	<tr>
		<td width="280px" height="190px" style="background-image:url(KnowWEExtension/images/plan.png); background-repeat:no-repeat"></td>
	</tr>

	<tr>
		<td width="280px" height="190px"
			style="background-image: url(KnowWEExtension/images/info.png); background-repeat: no-repeat">
		ICD Modell 
		<table class="other" border="0">
			<tr>
				<th width="160px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getICDModelTitle(context.getUserName())%></span></th>
				<th class="inner2" width="20px"></th>
				<th width="160px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getICDModelID(context.getUserName())%></span></th>
			</tr>
		</table>
		Grunderkrankung 
		<table class="other" border="0">
			<tr>
				<th width="349px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getDiagnosis(context.getUserName())%></span></th>
			</tr>
		</table>
		Blutgruppe 
		<table class="other" border="0">
			<tr>
				<th width="349px" height="20px"><span class="inhalt"> <%=EmergencyPlanUtils.getBloodType(context.getUserName())%></span></th>
			</tr>
		</table>


		</td>
	</tr>
	<tr>
		<td width="280px" height="190px"
			style="background-image: url(KnowWEExtension/images/info.png); background-repeat: no-repeat">
		Medikament&ouml;se Dauerbehandlung
		<table class="other" border="0">
		<%= EmergencyPlanUtils.getMedics(context.getUserName())%>
		</table>
		</td>
	</tr>
</table>
<% 
}
else {

} 
%>
</body>
</html>