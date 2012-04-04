<%@page import="de.knowwe.diaflux.coverage.gl.GLCity"%>
<%@page import="de.knowwe.diaflux.coverage.gl.GLBuilding"%>
<%@page import="de.d3web.diaflux.coverage.CoverageResult"%>
<%@page import="de.d3web.core.knowledge.KnowledgeBase"%>
<%@page import="de.knowwe.diaflux.coverage.DiaFluxCoverageType"%>
<%@page import="de.knowwe.kdom.defaultMarkup.DefaultMarkupType"%>
<%@page import="de.knowwe.diaflux.coverage.gl.GLCityGenerator"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="de.knowwe.core.wikiConnector.WikiConnector"%>
<%@ page import="de.d3web.plugin.Extension"%>
<%@ page import="de.d3web.plugin.JPFPluginManager"%>
<%@ page import="de.knowwe.core.kdom.parsing.Section"%>
<%@ page import="de.knowwe.core.kdom.parsing.Sections"%>
<%@ page import="de.knowwe.core.kdom.Article"%>
<%@ page import="de.knowwe.diaflux.type.DiaFluxType"%>
<%@ page import="com.ecyrd.jspwiki.*" %>
<%@ page import="de.knowwe.jspwiki.*" %>
<%@ page import="java.util.*" %>
<%@ page import="de.knowwe.core.*" %>
<%@ page import="de.knowwe.core.utils.*" %>
<%@ page import="de.knowwe.core.action.*" %>
<%@ page import="de.knowwe.diaflux.kbinfo.*" %>
<%@ page import="de.knowwe.diaflux.*" %>
<%@ page import="de.d3web.we.utils.*" %>
<%@ page import="de.knowwe.core.user.*" %>
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
	boolean canViewPage = connector.userCanViewPage(topic, context.getRequest());
	
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
<title>Coverage City</title>
</head>
<body>

<div id="container">
    
    <div id="content">
        <canvas id="theCanvas" width="1030" height="700">
            
        </canvas>
        
    </div>
    <div id="pickResult"></div>
</div>
<%

String master = DiaFluxCoverageType.getMaster(coverageSection);

KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), master);

CoverageResult coverage = DiaFluxCoverageType.getResult(coverageSection, context);

String city;
if (coverage == null){
	city = "{}";
} else {
	
	GLCity glCity = GLCityGenerator.generateCity(kb, coverage);
	
	city= glCity.toString();
}



%>


<script type="text/javascript">

var city = [<%=city%>];

</script>

<script type="text/javascript" src="KnowWEExtension/scripts/city.js"></script>


</body>
</html>