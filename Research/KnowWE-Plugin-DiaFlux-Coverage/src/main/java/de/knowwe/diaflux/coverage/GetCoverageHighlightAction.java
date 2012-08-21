/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.we.basic.SessionProvider;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.FlowchartUtils;
import de.knowwe.diaflux.Highlights;
import de.knowwe.diaflux.type.DiaFluxType;
import de.knowwe.diaflux.type.FlowchartType;

/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class GetCoverageHighlightAction extends AbstractAction {

	private static final String PREFIX = "cover";
	private static final String COVERED = PREFIX + "Covered";
	private static final String UNCOVERED = PREFIX + "Uncovered";

	private CoverageResult getResult(UserContext context) {

		String coverageKdomid = context.getParameter("coveragesection");
		if (coverageKdomid != null) { // coverage shown in coverage section
			Section<DiaFluxCoverageType> coverageSec = Sections.getSection(
					coverageKdomid, DiaFluxCoverageType.class);

			return DiaFluxCoverageType.getResult(coverageSec);

		}
		else { // coverage shown in diaflux section
			String flowKdomid = context.getParameter("kdomid");

			Section<FlowchartType> flowchartSec = Sections.getSection(flowKdomid,
					FlowchartType.class);
			Section<DiaFluxType> diaFluxSec = Sections.findAncestorOfExactType(flowchartSec,
					DiaFluxType.class);

			KnowledgeBase kb = FlowchartUtils.getKB(diaFluxSec);
			Session session = SessionProvider.getSession(context, kb);

			return DefaultCoverageResult.calculateResult(
					CoverageUtils.getCoverage(session),
					session.getKnowledgeBase());
		}

	}

	@Override
	public void execute(UserActionContext context) throws IOException {

		CoverageResult result = getResult(context);
		if (result == null) {
			context.getWriter().write(Highlights.EMPTY_HIGHLIGHT);
			return;
		}
		String flowKdomid = context.getParameter("kdomid");

		Section<FlowchartType> flowchart = Sections.getSection(flowKdomid, FlowchartType.class);

		if (flowchart == null) {
			context.getWriter().write(Highlights.EMPTY_HIGHLIGHT);
			return;
		}

		String flowchartName = FlowchartType.getFlowchartName(flowchart);
		KnowledgeBase kb = result.getKb();
		Flow flow = DiaFluxUtils.getFlowSet(kb).get(flowchartName);

		if (flow == null) return;// TODO error handling

		StringBuilder builder = createCoverageXML(result, flow);

		context.setContentType("text/xml");
		context.getWriter().write(builder.toString());

	}

	/**
	 * 
	 * @created 10.10.2011
	 * @param result
	 * @param flow
	 * @return
	 */
	private static StringBuilder createCoverageXML(CoverageResult result, Flow flow) {
		StringBuilder builder = new StringBuilder();

		Highlights.appendHeader(builder, flow.getName(), PREFIX);

		Map<Edge, Map<String, String>> edges = new HashMap<Edge, Map<String, String>>();


		Collection<Node> validNodes = DefaultCoverageResult.getValidNodes(flow);
		for (Edge edge : DefaultCoverageResult.getValidEdges(validNodes)) {
			int count = result.getTraceCount(edge);
			String clazz;
			if (count == 0) {
				clazz = UNCOVERED;
			}
			else {
				clazz = COVERED;
			}

			Highlights.putValue(edges, edge, Highlights.CSS_CLASS, clazz);
			Highlights.putValue(edges, edge, Highlights.TOOL_TIP, String.valueOf(count));
		}

		Highlights.addEdgeHighlight(builder, edges);

		Map<Node, Map<String, String>> nodes = new HashMap<Node, Map<String, String>>();

		for (Node node : validNodes) {
			int count = result.getTraceCount(node);
			String clazz;
			if (count == 0) {
				clazz = UNCOVERED;
			}
			else {
				clazz = COVERED;
			}

			Highlights.putValue(nodes, node, Highlights.CSS_CLASS, clazz);
			Highlights.putValue(nodes, node, Highlights.TOOL_TIP,
					String.valueOf(count));

		}

		Highlights.addNodeHighlight(builder, nodes);

		Highlights.appendFooter(builder);
		return builder;
	}
}
