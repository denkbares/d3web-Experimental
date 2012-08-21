/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverage;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.Path;
import de.d3web.diaflux.coverage.PathIndex;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.diaflux.Highlights;
import de.knowwe.diaflux.type.FlowchartType;


/**
 * 
 * @author Reinhard Hatko
 * @created 11.07.2012
 */
public class PathCoverageHighlightAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) throws IOException {

		String nodeid = context.getParameter("nodeid");

		CoverageResult result = getCoverageResult(context);
		if (result == null) {
			Highlights.write(context, Highlights.EMPTY_HIGHLIGHT);
			return;
		}

		KnowledgeBase kb = result.getKb();
		
		Flow flow = findFlowchart(context, kb);

		Node selectedNode = null;
		for (Node node : flow.getNodes()) {
			if (node.getID().equalsIgnoreCase(nodeid)) {
				selectedNode = node;
				break;
			}
		}
		
		if (selectedNode == null) {
			Highlights.write(context, Highlights.EMPTY_HIGHLIGHT);
			return;

		}

		PathIndex index = result.getPathIndex();
		Collection<Path> allPaths = index.getAllPaths(selectedNode);
		Collection<Path> coveredPaths = index.getCoveredPaths(selectedNode);

		double allPathsSize = allPaths.size();
		double coveredPathsSize = coveredPaths.size();
		double pathCoverage = (coveredPathsSize / allPathsSize);

		StringBuilder builder = new StringBuilder();
		Highlights.appendHeader(builder, flow.getName(), "");
		Map<Node, Map<String, String>> nodeHighlights = new HashMap<Node, Map<String, String>>();

		String colorString = getColorString(pathCoverage);
		Highlights.putValue(nodeHighlights, selectedNode, "style",
				"border-width: 2px; border-style: solid; border-color: " + colorString + ";");
		Highlights.putValue(nodeHighlights, selectedNode, "title",
				"Path Coverage: " + pathCoverage);


		Highlights.addNodeHighlight(builder, nodeHighlights);

		Highlights.appendFooter(builder);

		Highlights.write(context, builder.toString());

	}

	/**
	 * 
	 * @created 13.07.2012
	 * @param allPathsSize
	 * @param coveredPathsSize
	 * @return
	 */
	private static String getColorString(double pathcoverage) {
		float hue = (float) (0.333f * pathcoverage);
		String hexString = Integer.toHexString(Color.getHSBColor(hue, 1f, 8f).getRGB() & 0x00FFFFFF);
		return "#" + (hexString.length() == 5 ? "0" + hexString : hexString);
	}




	/**
	 * 
	 * @created 12.07.2012
	 * @param context
	 * @param kb
	 * @return
	 */
	public Flow findFlowchart(UserActionContext context, KnowledgeBase kb) {
		String flowid = context.getParameter("kdomid");
		Section<FlowchartType> section = Sections.getSection(flowid, FlowchartType.class);
		String name = FlowchartType.getFlowchartName(section);

		return (Flow) kb.getManager().search(name);
	}

	/**
	 * 
	 * @created 12.07.2012
	 * @param context
	 * @return
	 */
	private CoverageResult getCoverageResult(UserActionContext context) {
		String coverageKdomid = context.getParameter("coveragesection");

		Section<DiaFluxCoverageType> coverageSec = Sections.getSection(
				coverageKdomid, DiaFluxCoverageType.class);

		return DiaFluxCoverageType.getResult(coverageSec);
	}

}
