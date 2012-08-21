<%@page import="de.d3web.diaflux.coverage.CoverageResult"%>
<%@page import="de.knowwe.diaflux.coverage.PathCoverageHighlight"%>
<%@page import="de.knowwe.diaflux.type.FlowchartType"%>
<%@page import="de.d3web.diaFlux.flow.StartNode"%>
<%@page import="de.d3web.diaFlux.flow.Node"%>
<%@page import="de.d3web.diaFlux.flow.FlowSet"%>
<%@page import="de.d3web.diaFlux.inference.DiaFluxUtils"%>
<%@page import="de.knowwe.diaflux.coverage.DiaFluxCoverageRenderer"%>
<%@page import="de.knowwe.diaflux.coverage.gl.GLCity"%>
<%@page import="de.knowwe.diaflux.coverage.gl.GLBuilding"%>
<%@page import="de.d3web.diaflux.coverage.CoverageResult"%>
<%@page import="de.d3web.core.knowledge.KnowledgeBase"%>
<%@page import="de.knowwe.diaflux.coverage.DiaFluxCoverageType"%>
<%@page import="de.knowwe.kdom.defaultMarkup.DefaultMarkupType"%>
<%@page import="de.knowwe.diaflux.coverage.gl.GLCityGenerator"%>
<%@page import="de.knowwe.core.wikiConnector.WikiConnector"%>
<%@page import="de.d3web.plugin.Extension"%>
<%@page import="de.d3web.plugin.JPFPluginManager"%>
<%@page import="de.knowwe.core.kdom.parsing.Section"%>
<%@page import="de.knowwe.core.kdom.parsing.Sections"%>
<%@page import="de.knowwe.core.kdom.Article"%>
<%@page import="de.knowwe.diaflux.type.DiaFluxType"%>
<%@page import="com.ecyrd.jspwiki.*" %>
<%@page import="de.knowwe.jspwiki.*" %>
<%@page import="java.util.*" %>
<%@page import="de.knowwe.core.*" %>
<%@page import="de.knowwe.core.utils.*" %>
<%@page import="de.knowwe.core.action.*" %>
<%@page import="de.knowwe.diaflux.kbinfo.*" %>
<%@page import="de.knowwe.diaflux.*" %>
<%@page import="de.d3web.we.utils.*" %>
<%@page import="de.knowwe.core.user.*" %>
<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
	
	String kdomID = parameters.get("kdomID");
	Section coverageSection = Sections.getSection(kdomID);
	
	if (coverageSection == null){
		out.println("<h3>Coverage not found. Please try opening this windows again.</h3>");
		out.println("<script>if (window.opener) window.opener.location.reload();</script>");
		
		return;
	}
	
	// Add topic as containing section of flowchart
	parameters.put(Attributes.TOPIC, coverageSection.getTitle());
	
	// Add web
	if(!parameters.containsKey(Attributes.WEB)) {
		parameters.put(Attributes.WEB, "default_web");
	}
	
	// Create AuthenticationManager instance
	AuthenticationManager manager = new JSPAuthenticationManager(wikiContext);
	
	// Create action context
	UserActionContext context = new ActionContext(parameters.get("action"), AbstractActionServlet.getActionFollowUpPath(request), parameters, request, response, wiki.getServletContext(), manager);
	
	String topic = context.getTitle();
	String web = context.getWeb();
	Article article = Environment.getInstance().getArticle(web, topic);
	if (article == null){
		// happens if article is no longer available
		out.println("<h3>Article not found: '" + topic + "'.</h3>");
		return;
	}
	
	WikiConnector connector = Environment.getInstance().getWikiConnector();
	boolean canViewPage = connector.userCanViewArticle(topic, context.getRequest());
	
	if (!canViewPage){
		out.println("<h3>Do not have the permission to view article: '" + topic + "'.</h3>");
		return;
	}
	
	
	JSPHelper jspHelper = new JSPHelper(context);
%>

<html>
<head>
<link rel="shortcut icon" type="image/x-icon" href="/KnowWE/images/favicon.ico" />
<link rel="icon" type="image/x-icon" href="/KnowWE/images/favicon.ico" /><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 <script type="text/javascript" src="KnowWEExtension/scripts/scenejs.js"></script>
 <script type="text/javascript" src="/KnowWE/scripts/mootools.js"></script>
 <script type="text/javascript" src="KnowWEExtension/scripts/KnowWE-helper.js"></script>
 <script type="text/javascript" src="KnowWEExtension/scripts/KnowWE.js"></script>
<title>Coverage City</title>
</head>
<body>
<input id="coveragesection" type="hidden" value="<%=kdomID%>">
<input id="nodeid" type="hidden" value="">

<div id="container">
    
    <div id="content">
        <canvas id="theCanvas" width="800" height="700">
            
        </canvas>
        
    </div>
    <div id="pickResult"></div>
    
</div>
<%



CoverageResult coverage = DiaFluxCoverageType.getResult(coverageSection);

String city;
if (coverage == null){
	city = "{}";
} else {
	
	GLCity glCity = GLCityGenerator.generateCity(coverage);
	
	city= glCity.toString();
	List<StartNode> startnodes =  DiaFluxUtils.getAutostartNodes(coverage.getKb());
	if (startnodes.isEmpty()){
		//TODO
	}
	String flowName = startnodes.get(0).getFlow().getName();
	
	Section<FlowchartType> flowSec = FlowchartUtils.findFlowchartSection(web, flowName);
	out.println(FlowchartUtils.createFlowchartRenderer(flowSec, context, "flow",
			PathCoverageHighlight.COVERAGE_CITY_SCOPE, true));
}


%>


<script type="text/javascript">

var city = [<%=city%>];

function picked(flowString){
	//var matches = flowString.match(/\(([^)]*)\) \[([^]]*)\]/);
	//var matches = /\(([^)]*)\) \[([^]]*)\]/.exec(flowString);
	var matches = flowString.split("+++");
	if (matches) {
		document.getElementById("pickResult").innerHTML = matches[0];
		var flowEl = $('flow');
		
		// clicked a node, remember its id
		if (matches[2]){
			$('nodeid').value = matches[2];			
		} else {
			$('nodeid').value = '';			
			
		}
		
		//load flowchart, if it changed
		if (flowEl.firstChild.id != matches[0]) {
		
			flowEl.innerHTML ="<div id='" + matches[0] + "'></div>";
		
			Flowchart.loadFlowchart(matches[1], $('flow').firstChild);
			
		} else {
			// if flow did not change, highlight new node
			if (matches[2]) {
				DiaFlux.Highlight.getHighlights.call({flow: flowEl.firstChild.firstChild.__flowchart}, 'PathCoverageHighlightAction', {coveragesection: $('coveragesection').value, nodeid: $('nodeid').value});
			}	
		}
		
		
		
	}
	
}

</script>

<script type="text/javascript" src="KnowWEExtension/scripts/city.js"></script>


</body>
</html>