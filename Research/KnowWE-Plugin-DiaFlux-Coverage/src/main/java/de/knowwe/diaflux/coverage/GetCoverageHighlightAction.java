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
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.diaflux.GetTraceHighlightAction;
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
		CoverageResult result;

		if (coverageKdomid != null) { // coverage shown in coverage section
			@SuppressWarnings("unchecked")
			Section<DiaFluxCoverageType> coverageSec = (Section<DiaFluxCoverageType>) Sections.getSection(
					coverageKdomid);

			return DiaFluxCoverageType.getResult(coverageSec, context);

		}
		else { // coverage shown in diaflux section
			String flowKdomid = context.getParameter("kdomid");
			@SuppressWarnings("unchecked")
			Section<DiaFluxType> diaFluxSec = (Section<DiaFluxType>) Sections.getSection(
					flowKdomid);

			KnowWEArticle article = KnowWEUtils.getCompilingArticles(diaFluxSec).iterator().next();

			SessionProvider provider = SessionProvider.getSessionProvider(context);
			KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), article.getTitle());
			Session session = provider.getSession(kb);

			result = DefaultCoverageResult.calculateResult(
					PSMDiaFluxCoverage.getCoverage(session),
					session.getKnowledgeBase());

		}

		return result;

	}

	@Override
	public void execute(UserActionContext context) throws IOException {

		CoverageResult result = getResult(context);
		if (result == null) {
			context.getWriter().write("<flow></flow>");
			return;
		}
		String flowKdomid = context.getParameter("kdomid");

		@SuppressWarnings("unchecked")
		Section<DiaFluxType> diaFluxSec = (Section<DiaFluxType>) Sections.getSection(
				flowKdomid);

		Section<FlowchartType> flowchart = Sections.findSuccessor(diaFluxSec, FlowchartType.class);
		if (flowchart == null) {
			context.getWriter().write("<flow></flow>");
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
	private StringBuilder createCoverageXML(CoverageResult result, Flow flow) {
		StringBuilder builder = new StringBuilder();

		GetTraceHighlightAction.appendHeader(builder, flow.getName(), PREFIX);

		List<Edge> coveredEdges = new LinkedList<Edge>();
		List<Edge> uncoveredEdges = new LinkedList<Edge>();

		Collection<Node> validNodes = DefaultCoverageResult.getValidNodes(flow);

		for (Edge edge : DefaultCoverageResult.getValidEdges(validNodes)) {
			int count = result.getTraceCount(edge);
			if (count != 0) coveredEdges.add(edge);
			else uncoveredEdges.add(edge);

		}

		GetTraceHighlightAction.addEdgeHighlight(builder, coveredEdges, COVERED);
		GetTraceHighlightAction.addEdgeHighlight(builder, uncoveredEdges, UNCOVERED);

		List<Node> coveredNodes = new LinkedList<Node>();
		List<Node> uncoveredNodes = new LinkedList<Node>();

		for (Node node : validNodes) {
			int count = result.getTraceCount(node);
			if (count != 0) coveredNodes.add(node);
			else uncoveredNodes.add(node);

		}

		GetTraceHighlightAction.addNodeHighlight(builder, coveredNodes, COVERED);
		GetTraceHighlightAction.addNodeHighlight(builder, uncoveredNodes, UNCOVERED);

		GetTraceHighlightAction.appendFooter(builder);
		return builder;
	}
}
