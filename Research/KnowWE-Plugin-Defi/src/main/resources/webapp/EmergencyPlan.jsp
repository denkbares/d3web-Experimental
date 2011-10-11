<%@page import="com.ecyrd.jspwiki.*" %>
<%@page import="de.d3web.we.jspwiki.*" %>
<%@page import="de.d3web.we.action.*" %>
<%@page import="de.d3web.we.core.*" %>
<%@page import="de.d3web.we.utils.*" %>
<%@page import="de.d3web.we.user.*" %>
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

<style type="text/css" media="all">
	table 
	{ border: 2px dashed black; background-color: firebrick; margin-bottom: 0%; padding: 0%; vertical-align: 0%;}
	
	.other 
	{border: 0px; background-color: white;}
	
	#inner 
	{ border-top: 0px;	border-left: 0px; border-right: 0px; border-bottom: 1px; border-style: solid; border-color: black; 
	  font-family: Arial, Helvetica, sans-serif; font-size: 14pt; text-align: center; vertical-align: top; line-height: 1em; }
	
	.inner2 
	{ border-top: 0px; border-left: 0px; border-right: 0px; border-bottom: 0px; border-style: solid; border-color: black; font-family: Arial, Helvetica, sans-serif;
	  font-size: 14pt; text-align: center; vertical-align: top; line-height: 1em; }
	
	th 
	{ border-top: 0px; border-left: 0px; border-right: 0px; border-bottom: 1px; border-style: solid; border-color: black; font-family: Arial, Helvetica, sans-serif;
      font-size: 14pt; text-align: center; vertical-align: top; line-height: 1em;}
	
	td,tr 
	{ border: 0px; border-style: solid; border-color: red; padding-top: 20%; padding-left: 6%;
	padding-bottom: 0%; font-family: Arial, Helvetica, sans-serif; font-size: 14pt; text-align: left; vertical-align: top; line-height: 1.4em;}

	.engl
	{ font-size:11pt; font-style:italic }
	
	.inhalt
	{ color:blue }
</style>
<%
if(requestUserCorrect) {
%>
<table border="1" style="border-collaps:collaps">
	<tr>
		<td width="400" height="290px" class="front" style="background-image:url(KnowWEExtension/images/front2.png); background-repeat:no-repeat"></td>
	</tr>

	<tr>
		<td width="400px" height="290px" class="tel" style="background-image: url(KnowWEExtension/images/tel.jpg); background-repeat: no-repeat">
		Mein Kardiologe / <span class="engl">cardiologist</span>
		<%=EmergencyPlanUtils.getCardiologist(context.getUserName())%>
		
		Mein Hausarzt / <span class="engl">attending physician</span>
		<%=EmergencyPlanUtils.getPhysician(context.getUserName())%>

		Im Notfall zu verst&auml;ndigen <div class="engl">In case of emergency please contact</div>
		<%=EmergencyPlanUtils.getEmergencyPerson(context.getUserName())%>
		</td>
	</tr>
	
	<tr>
		<td width="405px" height="290px" style="background-image:url(KnowWEExtension/images/plan.jpg); background-repeat:no-repeat"></td>
	</tr>

	<tr>
		<td width="400px" height="290px"
			style="background-image: url(KnowWEExtension/images/info.jpg); background-repeat: no-repeat">
		ICD Modell / <span class="engl">ICD model</span>
		<table class="other" border="0">
			<tr>
				<th width="160px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getICDModelTitle(context.getUserName())%></span></th>
				<th class="inner2" width="20px"></th>
				<th width="160px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getICDModelID(context.getUserName())%></span></th>
			</tr>
		</table>
		Grunderkrankung / <span class="engl">diagnosis</span>
		<table class="other" border="0">
			<tr>
				<th width="349px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getDiagnosis(context.getUserName())%></span></th>
			</tr>
		</table>
		Blutgruppe / <span class="engl">blood type</span>
		<table class="other" border="0">
			<tr>
				<th width="349px" height="20px"><span class="inhalt"> <%=EmergencyPlanUtils.getBloodType(context.getUserName())%></span></th>
			</tr>
		</table>


		</td>
	</tr>
	<tr>
		<td width="400px" height="290px"
			style="background-image: url(KnowWEExtension/images/info.jpg); background-repeat: no-repeat">
		Medikament&ouml;se Dauerbehandlung / <div class="engl">long term medication</div>
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