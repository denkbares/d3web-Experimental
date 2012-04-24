package de.d3web.we.diaflux.pathcoloring;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.basic.SessionProvider;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.diaflux.FlowchartUtils;
import de.knowwe.diaflux.type.DiaFluxType;
import de.knowwe.diaflux.type.FlowchartType;


public class GetAnomaliesHighlightAction extends AbstractAction {

	private static final String PREFIX = "cover";
	private static final String COVER_ANOMALY = PREFIX + "Anomaly";

	@Override
	public void execute(UserActionContext context) throws IOException {
		
		AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
		
		String kdomid = context.getParameter("kdomid");

		Section<DiaFluxType> diaFluxSec = (Section<DiaFluxType>) Sections.getSection(kdomid);

		Section<FlowchartType> flowchart = Sections.findSuccessor(diaFluxSec, FlowchartType.class);
		if (flowchart == null) {
			context.getWriter().write("<flow></flow>");
			return;
		}
		String flowName = FlowchartType.getFlowchartName(flowchart);
		KnowledgeBase kb = FlowchartUtils.getKB(diaFluxSec);
		Session session = SessionProvider.getSession(context, kb);

		StringBuilder builder = new StringBuilder();
		appendHeader(builder, flowName, PREFIX);

		Flow flow = DiaFluxUtils.getFlowSet(session).get(flowName);
		
		List<Edge> anomalyEdges = new LinkedList<Edge>();
		List<Node> anomalyNodes = new LinkedList<Node>();

		//here anomalyManager
		anomalyEdges.addAll(anomalyManager.getAnomalyEdges(flow).keySet());
		anomalyNodes.addAll(anomalyManager.getAnomalyNodes(flow).keySet());
		
		addNodeHighlight(builder, anomalyNodes, COVER_ANOMALY);
		addEdgeHighlight(builder, anomalyEdges, COVER_ANOMALY);
		
		appendFooter(builder);

		context.setContentType("text/xml");
		context.getWriter().write(builder.toString());
	}

	public static void appendHeader(StringBuilder builder, String flowName, String prefix) {

		builder.append("<flow id='");
		builder.append(flowName);
		builder.append("' prefix ='" + PREFIX + "'>\r");

	}
	
	public static void addEdgeHighlight(StringBuilder builder, List<Edge> edges, String cssclass) {

		for (Edge edge : edges) {
			builder.append("<edge id='");
			builder.append(edge.getID());
			builder.append("'>");
			builder.append(cssclass);
			builder.append("</edge>\r");
		}

	}

	public static void addNodeHighlight(StringBuilder builder, List<Node> nodes, String cssclass) {

		for (Node node : nodes) {
			builder.append("<node id='");
			builder.append(node.getID());
			builder.append("'>");
			builder.append(cssclass);
			builder.append("</node>\r");
		}
	}
	
	public static void appendFooter(StringBuilder builder) {
		builder.append("</flow>");
		builder.append("\r");

	}
}