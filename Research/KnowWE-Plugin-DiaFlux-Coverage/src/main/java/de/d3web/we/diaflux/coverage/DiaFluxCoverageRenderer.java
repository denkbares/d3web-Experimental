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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.we.flow.FlowchartUtils;
import de.d3web.we.flow.type.FlowchartType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class DiaFluxCoverageRenderer extends DefaultMarkupRenderer<DiaFluxCoverageType> {

	public static final String DIA_FLUX_COVERAGE_SCOPE = "diafluxcoverage";

	@Override
	protected void renderContents(KnowWEArticle article, Section<DiaFluxCoverageType> section, UserContext user, StringBuilder string) {

		CoverageResult result = DiaFluxCoverageType.getResult(section, user);

		if (result == null) {
			renderButton(user, string, section, article);
		}
		else {
			renderResult(user, string, section, result);
		}

	}


	private void renderButton(UserContext user, StringBuilder string, Section<DiaFluxCoverageType> section, KnowWEArticle article) {

		StringBuilder bob = new StringBuilder();
		bob.append("<em>No coverage has been calculated yet.</em><input type='button' value='Calculate' onclick='DiaFlux.Coverage.calculateCoverage(\""
				+ section.getID() + "\")'></input>");

		string.append(KnowWEUtils.maskHTML(bob.toString()));


	}


	/**
	 * 
	 * @created 13.09.2011
	 * @param user
	 * @param string
	 * @param section
	 * @param result
	 * @param session
	 */
	private void renderResult(UserContext user, StringBuilder string, Section<DiaFluxCoverageType> section, CoverageResult result) {
		KnowledgeBase kb = result.getKb();
		FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);

		if (flowSet.size() == 0) {
			string.append("No DiaFlux model found.");
			return;
		}

		List<Flow> flows = new ArrayList<Flow>(flowSet.getFlows());

		Collections.sort(flows, new Comparator<Flow>() {

			public int compare(Flow o1, Flow o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});


		StringBuilder builder = new StringBuilder();
		String web = user.getWeb();

		
		builder.append("<div id='coverage' class='diafluxcoverage' height='500'>\n");
		builder.append("<input id='coveragesection' type='hidden' value='" + section.getID()
				+ "'/>");
		builder.append("<select name='coverageSelector' onchange='DiaFlux.Coverage.refresh(this);'>");


		Flow selectedFlow = null;

		for (Flow flow : flows) {
			String name = flow.getName();

			Section<FlowchartType> flowSec = FlowchartUtils.findFlowchartSection(web, name);

			if (flowSec != null) {
				builder.append("<option ");
				if ((selectedFlow == null) && (flow.isAutostart())) {
					builder.append("selected='selected' ");
					selectedFlow = flow;
				}
				builder.append("value='" + flowSec.getID() + "'>");
				builder.append(name);
				builder.append("</option>");
			}

		}

		builder.append("</select>");

		if (selectedFlow == null) selectedFlow = (Flow) flows.get(0);

		Section<FlowchartType> flowSec = FlowchartUtils.findFlowchartSection(web,
				selectedFlow.getName());
		builder.append(FlowchartUtils.createFlowchartRenderer(flowSec, user, "coverageContent",
				DIA_FLUX_COVERAGE_SCOPE, true));

		builder.append("</div>");

		builder.append("<div name='coverageresult'>");
		builder.append("FlowCoverage: " + result.getFlowCoverage(selectedFlow)).append("<br>");
		builder.append("NodeCoverage: " + result.getNodeCoverage(selectedFlow)).append("<br>");
		builder.append("EdgeCoverage: " + result.getEdgeCoverage(selectedFlow)).append("<br>");

		builder.append("</div>");

		string.append(KnowWEUtils.maskHTML(builder.toString()));
	}


}
