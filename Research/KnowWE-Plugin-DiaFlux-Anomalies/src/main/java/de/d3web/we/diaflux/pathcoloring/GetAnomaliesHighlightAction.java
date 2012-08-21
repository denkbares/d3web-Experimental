/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.pathcoloring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.knowwe.diaflux.Highlights;
import de.knowwe.diaflux.type.DiaFluxType;
import de.knowwe.diaflux.type.FlowchartType;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class GetAnomaliesHighlightAction extends AbstractAction {

	private static final String PREFIX = "cover";
	private static final String COVER_ANOMALY = PREFIX + "Anomaly";

	@Override
	public void execute(UserActionContext context) throws IOException {

		AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();

		String kdomid = context.getParameter("kdomid");

		Section<FlowchartType> flowchart = Sections.getSection(kdomid, FlowchartType.class);
		Section<DiaFluxType> diaFluxSec = Sections.findAncestorOfExactType(flowchart,
				DiaFluxType.class);

		KnowledgeBase kb = FlowchartUtils.getKB(diaFluxSec);
		Session session = SessionProvider.getSession(context, kb);

		if (flowchart == null || session == null) {
			Highlights.write(context, Highlights.EMPTY_HIGHLIGHT);
			return;
		}
		String flowName = FlowchartType.getFlowchartName(flowchart);

		StringBuilder builder = new StringBuilder();
		Highlights.appendHeader(builder, FlowchartUtils.escapeHtmlId(flowName), PREFIX);

		Flow flow = DiaFluxUtils.getFlowSet(session).get(flowName);

		Map<Edge, Map<String, String>> anomalyedges = new HashMap<Edge, Map<String, String>>();
		Map<Node, Map<String, String>> anomalynodes = new HashMap<Node, Map<String, String>>();

		for (Node node : anomalyManager.getAnomalyNodes(flow).keySet()) {
			if (node.getFlow().getName().equals(flowName)) {
				Highlights.putValue(anomalynodes, node,
						Highlights.CSS_CLASS, COVER_ANOMALY);
				Highlights.putValue(anomalynodes, node,
						Highlights.TOOL_TIP,
						anomalyManager.getAnomalyNodes(flow).get(node));
			}
		}

		for (Edge edge : anomalyManager.getAnomalyEdges(flow).keySet()) {
			if (edge.getStartNode().getFlow().getName().equals(flowName)) {
				Highlights.putValue(anomalyedges, edge,
						Highlights.CSS_CLASS, COVER_ANOMALY);
				Highlights.putValue(anomalyedges, edge,
						Highlights.TOOL_TIP,
						anomalyManager.getAnomalyEdges(flow).get(edge));
			}
		}

		List<Edge> remainingEdges = new ArrayList<Edge>(flow.getEdges());
		List<Node> remainingNodes = new ArrayList<Node>(flow.getNodes());
		remainingEdges.removeAll(anomalyedges.keySet());
		remainingNodes.removeAll(anomalynodes.keySet());

		// clear classes on all remaining nodes and edges
		for (Node node : remainingNodes) {
			Highlights.putValue(anomalynodes, node, Highlights.CSS_CLASS, "");
		}

		for (Edge edge : remainingEdges) {
			Highlights.putValue(anomalyedges, edge, Highlights.CSS_CLASS, "");
		}

		Highlights.addNodeHighlight(builder, anomalynodes);
		Highlights.addEdgeHighlight(builder, anomalyedges);

		Highlights.appendFooter(builder);

		Highlights.write(context, builder.toString());

	}

}