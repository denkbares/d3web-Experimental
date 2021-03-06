<%@ page import="org.apache.wiki.*" %>
<%@ page import="de.knowwe.jspwiki.*" %>
<%@ page import="de.knowwe.core.user.*" %>
<%@ page import="de.knowwe.core.action.*" %>
<%@ page import="de.knowwe.core.utils.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.d3web.we.action.*" %>
<%@ page import="de.knowwe.core.*" %>
<%@ page import="de.knowwe.utils.*" %>
<%@ page import="de.d3web.strings.*" %>
<%@ page import="de.knowwe.user.*" %>
<%@ page import="de.knowwe.ophtovis.*" %>
<%@ taglib uri="http://jspwiki.apache.org/tags" prefix="wiki" %><%!
String findParam( PageContext ctx, String key )
    {
        ServletRequest req = ctx.getRequest();
        String val = req.getParameter( key );
        if( val == null )
        {
            val = (String)ctx.findAttribute( key );
        }
        return val;
    }
%><%//Create wiki context; authorization check not needed
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
	
	// Add web
	if(!parameters.containsKey(Attributes.WEB)) {
		parameters.put(Attributes.WEB, "default_web");
	}
	
	// Create AuthenticationManager instance
	AuthenticationManager manager = new JSPAuthenticationManager(wikiContext);
	
	// Create action context
	UserActionContext context = new ActionContext(parameters.get("action"), AbstractActionServlet.getActionFollowUpPath(request), parameters, request, response, wiki.getServletContext(), manager);
	
	String concept = parameters.get("concept");
	String min= parameters.get("minimized");
	
	// Perform action
	if(concept == null || concept.length() == 0) {
		concept = "PRAEOPERATIVE";		
	}
	if(min == null || min.length() == 0) {
		min = "false";		
	}
	String content = "";
	if(min.equals("true")){
		content = Visualization.visualize(concept,true);
	}else{
		content = Visualization.visualize(concept,false);	
	}
	content = Strings.unmaskHTML(content);
	
	%><%=content %>