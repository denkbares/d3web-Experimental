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
	if (!Environment.isInitialized()) {
		Environment.initInstance(new JSPWikiConnector(wiki));
	}
	// We need to do this, because the paramterMap is locked!
	Map<String, String> parameters = UserContextUtil.getParameters(request);
	
	// Add user
	if (!parameters.containsKey(Attributes.USER)) {
		parameters.put(Attributes.USER, wikiContext.getWikiSession().getUserPrincipal().getName());
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
	#emergency {
    	margin: 0px;
    	padding: 0px;
    	border: 3px dotted black;
    	border-collapse: collapse;
    	background-color: rgb(218,37,29);
	}
	#emergency td, #emergency tr {
		border: 0px;
		width: 279px;
	}
	#emergency td {
		background-repeat: no-repeat;
		background-position: center;
	}
	#emergency td.tel {
		background-image:url(KnowWEExtension/images/tel_head.png);
		background-position: top center;
		padding-top: 40px;
	}
	#emergency td.info {
		background-image:url(KnowWEExtension/images/info_head.png);
		background-position: top center;
		padding-top: 40px;
		padding-bottom: 6px;
	}
	#emergency div.tel, #emergency div.info {
		background-color: white;
		margin: 0px 6px;
		padding: 0px 6px 6px 6px;
	}
	#emergency span {
		color: blue;
		font-family: Arial, Helvetica, sans-serif;
		font-size: 16px;
		font-weight:700;
	}
	#emergency div.tel, #emergency div.info {
		font-family: Arial, Helvetica, sans-serif;
     	font-size: 12pt; 
     	line-height: 1.2em;
	}
	#emergency span.first {
		text-align: center;
		background-color: white;
		position: relative;
		top: 78px;
		display: block;
		margin: 0 6px;
		line-height: 18pt;
	}
	table.other th { border-bottom: 1px solid black; }
	table.other th.inner2 { border: 0px; }
</style>
<script type="text/javascript">
	function checkFontSize() {
		var inputs = document.getElementsByClassName('inhalt');
		var nameInput = document.getElementsByClassName('first')[0];
		var fontSize = 16;
		var fit = false;
		
		// fit namefield
		while(!fit && fontSize > 6) {
			fontSize--;
			fit = true;
			
			if (nameInput.offsetHeight > 24)
				fit = false;
			
			if (!fit)
				nameInput.style.fontSize = fontSize + "px";
		}
		
		// fit inputfields
		fontSize = 16;
		fit = false;
		
		while(!fit && fontSize > 6) {
			fontSize--;
			fit = true;
			
			for (var i = 0; i < inputs.length; i++) {
				
				if (inputs[i].offsetHeight > 19) {
					fit = false;
					break;
				}
			}	
			
			if (!fit) {
				for (var i = 0; i < inputs.length; i++) {
					inputs[i].style.fontSize = fontSize + "px";
				}
			}
			
		}
	}
	
	window.onload = checkFontSize;
</script>
</head>
<body>
<%
if(requestUserCorrect) {
%>
<table id="emergency">
	<tr><td style="background-image:url(KnowWEExtension/images/front3.png);height: 200px;"><span class='first'>
	<%=EmergencyPlanUtils.getPatientName(context.getUserName())%>&nbsp;</span></td></tr>
	<tr><td class="tel"><div class="tel">
		Notrufnummer
		<table class="other" border="0">
			<tr>
				<th width="349px" height="20px"><span class="inhalt"><%=EmergencyPlanUtils.getEmergencyNumber(context.getUserName())%></span></th>
			</tr>
		</table>
	
		Mein Kardiologe 
		<%=EmergencyPlanUtils.getCardiologist(context.getUserName())%>
		
		Mein Hausarzt 
		<%=EmergencyPlanUtils.getPhysician(context.getUserName())%>

		Im Notfall zu verst&auml;ndigen 
		<%=EmergencyPlanUtils.getEmergencyPerson(context.getUserName())%>
	</div></td></tr>
	<tr><td style="background-image:url(KnowWEExtension/images/plan.png);height:200px;"></td></tr>
	<tr><td class="info"><div class="info">
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
	</div></td></tr>
	<tr><td class="info"><div class="info">
		Medikament&ouml;se Dauerbehandlung
		<table class="other" border="0">
		<%= EmergencyPlanUtils.getMedics(context.getUserName())%>
		</table>
	</div></td></tr>
</table>
<% 
}
else {

} 
%>
</body>
</html>