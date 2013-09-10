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
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.diaflux.AbstractHighlightAction;
import de.knowwe.diaflux.Highlight;
import de.knowwe.diaflux.type.FlowchartType;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 21.11.2012
 */
public class GetAnomaliesHighlightAction extends AbstractHighlightAction {

	private static final String PREFIX = "cover";
	private static final String COVER_ANOMALY = PREFIX + "Anomaly";


	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public void insertHighlighting(Section<FlowchartType> flowchart, Highlight highlight, UserActionContext context) throws IOException {
		KnowledgeBase kb = getKB(flowchart);
		Flow flow = findFlow(flowchart, kb);

		Map<Edge, String> edges = AnomalyManager.getAnomalyManager().getAnomalyEdges(flow);
		
		for (Edge edge : edges.keySet()) {
			highlight.add(edge, Highlight.CSS_CLASS, COVER_ANOMALY);
			highlight.add(edge, Highlight.TOOL_TIP, edges.get(edge));
		}
		
		Map<Node, String> nodes = AnomalyManager.getAnomalyManager().getAnomalyNodes(flow);

		for (Node node : nodes.keySet()) {
			highlight.add(node, Highlight.CSS_CLASS, COVER_ANOMALY);
			highlight.add(node, Highlight.TOOL_TIP, nodes.get(node));
		}

	}

}