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
import java.util.HashSet;
import java.util.LinkedList;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.Path;
import de.d3web.diaflux.coverage.PathGenerator;
import de.d3web.diaflux.coverage.PathIndex;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.diaflux.Highlight;
import de.knowwe.diaflux.type.FlowchartType;


/**
 * 
 * @author Reinhard Hatko
 * @created 11.07.2012
 */
public class PathCoverageHighlightAction extends AbstractAction {

	private static final String PREFIX = "cover";
	private static final String COVERED = PREFIX + "Covered";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String nodeid = context.getParameter("nodeid");

		CoverageResult result = getCoverageResult(context);
		if (result == null) {
			Highlight.writeEmpty(context);
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
		
		Highlight highlight;
		if (selectedNode == null) {
			highlight = createHighlight(result, flow);
			return;
		}
		else {
			highlight = createHighlight(result, selectedNode, kb);

		}

		highlight.write(context);

	}

	private Highlight createHighlight(CoverageResult result, Node selectedNode, KnowledgeBase kb) {
		Highlight highlight = new Highlight(selectedNode.getFlow(), PREFIX);
		Collection<DiaFluxElement> elements = new LinkedList<DiaFluxElement>();
		for (DiaFluxElement el : elements) {
			PathIndex index = result.getPathIndex();
			Collection<Path> allPaths = index.getAllPaths(el);
			Collection<Path> coveredPaths = index.getCoveredPaths(el);

			double allPathsSize = allPaths.size();
			double coveredPathsSize = coveredPaths.size();
			insertColor(highlight, el, allPathsSize, coveredPathsSize);

		}

		return highlight;
	}

	public Highlight createHighlight(CoverageResult result, Flow flow) {
		Highlight highlight = new Highlight(flow, PREFIX);
		Collection<DiaFluxElement> elements = new LinkedList<DiaFluxElement>();
		Collection<Node> validNodes = DefaultCoverageResult.getValidNodes(flow);
		elements.addAll(validNodes);
		elements.addAll(DefaultCoverageResult.getValidEdges(validNodes));

		for (DiaFluxElement el : elements) {
			PathIndex index = result.getPathIndex();
			Collection<Path> allPaths = index.getAllPaths(el);
			Collection<Path> coveredPaths = index.getCoveredPaths(el);

			double allPathsSize = allPaths.size();
			double coveredPathsSize = coveredPaths.size();
			insertColor(highlight, el, allPathsSize, coveredPathsSize);

		}
		return highlight;

	}

	/**
	 * 
	 * @created 22.08.2012
	 * @param highlight
	 * @param el
	 * @param allPathsSize
	 * @param coveredPathsSize
	 */
	public void insertColor(Highlight highlight, DiaFluxElement el, double allPathsSize, double coveredPathsSize) {
		double pathCoverage = (coveredPathsSize / allPathsSize);

		String colorString = getColorString(pathCoverage);

		if (el instanceof Node) {
			highlight.add((Node) el, Highlight.CSS_STYLE, "border-color: " + colorString + ";");
			highlight.add((Node) el, Highlight.CSS_CLASS, COVERED);
			highlight.add((Node) el, Highlight.TOOL_TIP, "Path Coverage: " + pathCoverage);
		}
		else {
			highlight.add((Edge) el, Highlight.CSS_STYLE, "border-color: " + colorString + ";");
			highlight.add((Edge) el, Highlight.CSS_CLASS, COVERED);
			highlight.add((Edge) el, Highlight.TOOL_TIP, "Path Coverage: " + pathCoverage);

		}
	}

	private Collection<DiaFluxElement> getSubsequentElements(Node selectedNode, KnowledgeBase kb) {
		PathsFromNodeStrategy strategy = new PathsFromNodeStrategy(selectedNode);
		new PathGenerator(kb, strategy).createPaths();
		Collection<DiaFluxElement> result = new HashSet<DiaFluxElement>();
		for (Path path : strategy.getPaths()) {
			for (DiaFluxElement el : path) {
				result.add(el);
			}
		}
		return result;
	}

	private static String getColorString(double pathcoverage) {
		float hue = (float) (0.3333f * pathcoverage);
		String hexString = Integer.toHexString(Color.getHSBColor(hue, 1f, 8f).getRGB() & 0x00FFFFFF);
		return "#" + (hexString.length() == 5 ? "0" + hexString : hexString);
	}


	public static void main(String[] args) {
		System.out.println(getColorString(0.3333333));
	}

	private Flow findFlowchart(UserActionContext context, KnowledgeBase kb) {
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
