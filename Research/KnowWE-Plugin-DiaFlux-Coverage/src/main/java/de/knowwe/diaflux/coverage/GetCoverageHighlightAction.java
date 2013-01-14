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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.we.basic.SessionProvider;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.AbstractHighlightAction;
import de.knowwe.diaflux.FlowchartUtils;
import de.knowwe.diaflux.GetTraceHighlightAction;
import de.knowwe.diaflux.Highlight;
import de.knowwe.diaflux.type.DiaFluxType;
import de.knowwe.diaflux.type.FlowchartType;

/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class GetCoverageHighlightAction extends AbstractHighlightAction {

	private static final String PREFIX = "cover";
	private static final String COVERED = PREFIX + "Covered";
	private static final String UNCOVERED = PREFIX + "Uncovered";

	private CoverageResult getResult(UserContext context) {

		String coverageKdomid = context.getParameter("coveragesection");
		if (coverageKdomid != null) { // coverage shown in coverage section
			Section<DiaFluxCoverageType> coverageSec = Sections.getSection(
					coverageKdomid, DiaFluxCoverageType.class);

			if (coverageSec == null) return null;

			return DiaFluxCoverageType.getResult(coverageSec);

		}
		else { // coverage shown in diaflux section
			String flowKdomid = context.getParameter(Attributes.SECTION_ID);

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
	public void insertHighlighting(Section<FlowchartType> flowchart, Highlight highlight, UserActionContext context) throws IOException {

		CoverageResult result = getResult(context);
		if (result == null) return;
		Flow flow = GetTraceHighlightAction.findFlow(flowchart, result.getKb());
		// if flow has errors
		if (flow == null) return;

		Collection<Node> validNodes = DefaultCoverageResult.getValidNodes(flow);
		for (Edge edge : DefaultCoverageResult.getValidEdges(validNodes)) {
			int count = result.getTraceCount(edge);

			highlight.add(edge, Highlight.CSS_CLASS, (count > 0 ? COVERED : UNCOVERED));
			highlight.add(edge, Highlight.TOOL_TIP, String.valueOf(count));
		}


		for (Node node : validNodes) {
			int count = result.getTraceCount(node);

			highlight.add(node, Highlight.CSS_CLASS, (count > 0 ? COVERED : UNCOVERED));
			highlight.add(node, Highlight.TOOL_TIP, String.valueOf(count));

		}

	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}
}
