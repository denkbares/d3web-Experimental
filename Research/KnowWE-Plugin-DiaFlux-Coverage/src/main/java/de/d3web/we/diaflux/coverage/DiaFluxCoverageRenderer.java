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
import de.d3web.we.basic.D3webModule;
import de.d3web.we.flow.FlowchartRenderer;
import de.d3web.we.flow.FlowchartUtils;
import de.d3web.we.flow.type.FlowchartType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class DiaFluxCoverageRenderer extends DefaultMarkupRenderer<DiaFluxCoverageType> {

	public static final String DIA_FLUX_COVERAGE = "diafluxcoverage";

	@Override
	protected void renderContents(KnowWEArticle article, Section<DiaFluxCoverageType> section, UserContext user, StringBuilder string) {

		String master = DiaFluxCoverageType.getMaster(section, article.getTitle());

		KnowledgeBase kb =
				D3webModule.getKnowledgeRepresentationHandler(user.getWeb()).getKB(master);

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
		builder.append("<input id='coveragemaster' type='hidden' value='" + master + "'/>");
		builder.append("<select name='coverageSelector' onchange='DiaFlux.Coverage.refresh(this);'>");


		for (Flow flow : flows) {
			String name = flow.getName();

			Section<FlowchartType> flowSec = FlowchartRenderer.findFlowchartSection(web, name);

			if (flowSec != null) {
				builder.append("<option ");
				if (flow.isAutostart()) builder.append("selected='selected' ");
				builder.append("value='" + flowSec.getID() + "'>");
				builder.append(name);
				builder.append("</option>");

			}

		}

		builder.append("</select>");

		// TODO show autostart flow
		Flow firstFlow = flows.get(0);
		Section<FlowchartType> flowSec = FlowchartRenderer.findFlowchartSection(web,
				firstFlow.getName());
		builder.append(FlowchartUtils.createFlowchartRenderer(flowSec, user, "coverageContent",
				DIA_FLUX_COVERAGE));

		builder.append("</div>");

		string.append(KnowWEUtils.maskHTML(builder.toString()));

	}


}
