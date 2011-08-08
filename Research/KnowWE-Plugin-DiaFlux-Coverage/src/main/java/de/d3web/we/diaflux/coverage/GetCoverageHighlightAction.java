/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.coverage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.flow.GetTraceHighlightAction;
import de.d3web.we.flow.type.DiaFluxType;
import de.d3web.we.flow.type.FlowchartType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.utils.D3webUtils;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class GetCoverageHighlightAction extends AbstractAction {

	private static final String PREFIX = "cover";
	private static final String COVERED = PREFIX + "Covered";
	private static final String UNCOVERED = PREFIX + "Uncovered";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String kdomid = context.getParameter("kdomid");
		String master = context.getParameter("master");
		String web = context.getWeb();

		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(web);
		Section<DiaFluxType> diaFluxSec = (Section<DiaFluxType>) articleManager.findNode(kdomid);

		Section<FlowchartType> flowchart = Sections.findSuccessor(diaFluxSec, FlowchartType.class);
		if (flowchart == null) {
			context.getWriter().write("<flow></flow>");
			return;
		}
		
		
		KnowledgeBase kb = D3webUtils.getKB(context.getWeb(), master);
		if (kb == null)
		;// TODO error handling
		
		FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);

		Flow flow = null;
		String flowchartName = FlowchartType.getFlowchartName(flowchart);
		for (Flow kbFlow : flowSet) {
			if (kbFlow.getName().equalsIgnoreCase(flowchartName)) {
				flow = kbFlow;
				break;
			}
		}

		if (flow == null)
		;// TODO error handling
		
		StringBuilder builder = new StringBuilder();

		GetTraceHighlightAction.appendHeader(builder, flowchartName, PREFIX);

		List<Edge> coveredEdges = new LinkedList<Edge>();
		List<Edge> uncoveredEdges = new LinkedList<Edge>();

		for (Edge edge : flow.getEdges()) {
			int count = PSMDiaFluxCoverage.getCount(edge);
			if (count != 0) coveredEdges.add(edge);
			else uncoveredEdges.add(edge);

		}

		GetTraceHighlightAction.addEdgeHighlight(builder, coveredEdges, COVERED);
		GetTraceHighlightAction.addEdgeHighlight(builder, uncoveredEdges, UNCOVERED);

		List<Node> coveredNodes = new LinkedList<Node>();
		List<Node> uncoveredNodes = new LinkedList<Node>();

		for (Node node : flow.getNodes()) {
			if (node instanceof CommentNode && node.getIncomingEdges().isEmpty()) continue;
			int count = PSMDiaFluxCoverage.getCount(node);
			if (count != 0) coveredNodes.add(node);
			else uncoveredNodes.add(node);

		}

		GetTraceHighlightAction.addNodeHighlight(builder, coveredNodes, COVERED);
		GetTraceHighlightAction.addNodeHighlight(builder, uncoveredNodes, UNCOVERED);

		GetTraceHighlightAction.appendFooter(builder);
		
		context.setContentType("text/xml");
		context.getWriter().write(builder.toString());
		
	}

}
