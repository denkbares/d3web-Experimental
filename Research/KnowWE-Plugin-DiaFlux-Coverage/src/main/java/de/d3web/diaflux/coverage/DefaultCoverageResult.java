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
package de.d3web.diaflux.coverage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 13.09.2011
 */
public class DefaultCoverageResult implements CoverageResult {

	private final Map<Flow, Double> flowCoverage;
	private final Map<Flow, Double> nodeCoverage;
	private final Map<Flow, Double> edgeCoverage;

	private final Map<Edge, Integer> edges;
	private final Map<Node, Integer> nodes;
	private final Map<Path, Integer> paths;
	private final KnowledgeBase kb;

	private PathCoverage pathCoverage;

	private DefaultCoverageResult(Map<Node, Integer> nodeCount, Map<Edge, Integer> edgeCount, Map<Path, Integer> pathCount, KnowledgeBase kb) {
		this.edges = new HashMap<Edge, Integer>(edgeCount);
		this.nodes = new HashMap<Node, Integer>(nodeCount);
		this.paths = new HashMap<Path, Integer>(pathCount);
		this.kb = kb;

		this.flowCoverage = new HashMap<Flow, Double>();
		this.nodeCoverage = new HashMap<Flow, Double>();
		this.edgeCoverage = new HashMap<Flow, Double>();

	}

	private void setEdgeCoverage(Flow flow, double coverage) {
		this.edgeCoverage.put(flow, Double.valueOf(coverage));
	}

	private void setNodeCoverage(Flow flow, double coverage) {
		this.nodeCoverage.put(flow, Double.valueOf(coverage));
	}

	private void setFlowCoverage(Flow flow, double coverage) {
		this.flowCoverage.put(flow, Double.valueOf(coverage));
	}

	@Override
	public double getFlowCoverage(Flow flow) {
		return getValue(this.flowCoverage, flow);
	}

	@Override
	public double getEdgeCoverage(Flow flow) {
		return getValue(this.edgeCoverage, flow);
	}

	@Override
	public double getNodeCoverage(Flow flow) {
		return getValue(this.nodeCoverage, flow);
	}

	@Override
	public KnowledgeBase getKb() {
		return kb;
	}

	private double getValue(Map<Flow, Double> map, Flow flow) {
		if (!map.containsKey(flow)) return 0;
		return map.get(flow).doubleValue();
	}

	@Override
	public int getTraceCount(Node node) {
		return getCount(node, nodes);

	}

	@Override
	public int getTraceCount(Edge edge) {
		return getCount(edge, edges);

	}
	
	public int getTraceCount(Path path) {
		return getCount(path, paths);

	}

	private static <T> int getCount(T edge, Map<T, Integer> map) {
		Integer count = map.get(edge);
		if (count == null) {
			return 0;
		}
		else return count;
		
	}

	@Override
	public Map<Node, Integer> getNodeCounts() {
		return Collections.unmodifiableMap(nodes);
	}

	public Map<Path, Integer> getPathCounts() {
		return Collections.unmodifiableMap(paths);
	}

	@Override
	public Map<Edge, Integer> getEdgeCounts() {
		return Collections.unmodifiableMap(edges);
	}

	/**
	 * 
	 * @created 13.09.2011
	 */
	private void calculate() {

		FlowSet flowSet = DiaFluxUtils.getFlowSet(this.kb);

		for (Flow flow : flowSet) {
			Collection<Node> nodes = getValidNodes(flow);
			Collection<Edge> edges = getValidEdges(nodes);

			double nodeCount = nodes.size();
			double edgeCount = edges.size();

			double coveredNodes = 0.0D;
			double coveredEdges = 0.0D;

			for (Edge edge : edges) {
				if (getTraceCount(edge) <= 0) continue;
				coveredEdges += 1.0D;
			}

			for (Node node : nodes) {
				if (getTraceCount(node) <= 0) continue;
				coveredNodes += 1.0D;
			}

			double edgeCoverage = Math.round(coveredEdges / edgeCount * 1000.0D) / 10.0D;
			double nodeCoverage = Math.round(coveredNodes / nodeCount * 1000.0D) / 10.0D;
			double flowCoverage = Math.round((nodeCoverage + edgeCoverage) / 2.0D);

			setEdgeCoverage(flow, edgeCoverage);
			setNodeCoverage(flow, nodeCoverage);
			setFlowCoverage(flow, flowCoverage);
		}

		this.pathCoverage = new PathCoverage(paths, getKb());

		// Path2GraphViz.createPaths(kb, allPaths);

	}

	public PathCoverage getPathCoverage() {
		return pathCoverage;
	}

	public static Collection<Edge> getValidEdges(Collection<Node> validnodes) {
		Set<Edge> edges = new HashSet<Edge>();

		for (Node node : validnodes) {
			// edges.addAll(node.getIncomingEdges());
			edges.addAll(node.getOutgoingEdges());
		}

		return edges;
	}

	public static Collection<Node> getValidNodes(Flow flow) {
		Set<Node> nodes = new HashSet<Node>(flow.getNodes());

		// Do not consider Commentnodes, unless they are used to route nodes...
		for (CommentNode commentNode : flow.getNodesOfClass(CommentNode.class)) {
			if (commentNode.getIncomingEdges().isEmpty()) {
				nodes.remove(commentNode);
			}
		}

		return nodes;
	}

	public static CoverageResult calculateResult(Collection<CoverageSessionObject> coverages, KnowledgeBase kb) {
		Map<Edge, Integer> edgeSum = new HashMap<Edge, Integer>();
		Map<Node, Integer> nodeSum = new HashMap<Node, Integer>();
		Map<Path, Integer> pathSum = new HashMap<Path, Integer>();

		for (CoverageSessionObject coverage : coverages) {
			sumUp(nodeSum, coverage.getNodeCounts());
			sumUp(edgeSum, coverage.getEdgeCounts());
			sumUp(pathSum, coverage.getPathCounts());
		}

		DefaultCoverageResult result = new DefaultCoverageResult(nodeSum, edgeSum, pathSum, kb);
		result.calculate();
		return result;
		
	}

	/**
	 * 
	 * @created 05.04.2012
	 * @param sum
	 * @param coverage
	 */
	private static <T> void sumUp(Map<T, Integer> sum, Map<T, Integer> counts) {

		for (T elem : counts.keySet()) {
			Integer integer = sum.get(elem);

			if (integer == null) {
				sum.put(elem, counts.get(elem));
			}
			else {
				sum.put(elem, counts.get(elem) + integer);
			}
		}
	}

	public static CoverageResult calculateResult(CoverageSessionObject coverage, KnowledgeBase kb) {
		return calculateResult(Arrays.asList(coverage), kb);
	}

}