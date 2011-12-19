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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
	private final KnowledgeBase kb;

	private DefaultCoverageResult(Map<Node, Integer> nodeCount, Map<Edge, Integer> edgeCount, KnowledgeBase kb) {
		this.edges = new HashMap<Edge, Integer>(edgeCount);
		this.nodes = new HashMap<Node, Integer>(nodeCount);
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
		Integer count = nodes.get(node);
		if (count == null) {
			return 0;
		}
		else return count;

	}

	@Override
	public int getTraceCount(Edge edge) {
		Integer count = edges.get(edge);
		if (count == null) {
			return 0;
		}
		else return count;

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
		Collection<CommentNode> commentNodes = flow.getNodesOfClass(CommentNode.class);

		// Do not consider Commentnodes, unless they are used to route nodes...
		for (CommentNode commentNode : commentNodes) {
			if (commentNode.getIncomingEdges().isEmpty()) {
				nodes.remove(commentNode);
			}
		}

		return nodes;
	}

	public static CoverageResult calculateResult(Collection<CoverageSessionObject> coverages, KnowledgeBase kb) {
		Map<Edge, Integer> edgeSum = new HashMap<Edge, Integer>();
		Map<Node, Integer> nodeSum = new HashMap<Node, Integer>();

		for (CoverageSessionObject coverageSessionObject : coverages) {
			Map<Node, Integer> counts = coverageSessionObject.getNodeCounts();
			for (Node node : counts.keySet()) {
				Integer integer = nodeSum.get(node);
				if (integer == null) {
					nodeSum.put(node, 1);
				}
				else {
					nodeSum.put(node, 1 + integer);

				}

			}

			Map<Edge, Integer> edges = coverageSessionObject.getEdgeCounts();
			for (Edge edge : edges.keySet()) {
				Integer integer = edgeSum.get(edge);
				if (integer == null) {
					edgeSum.put(edge, 1);
				}
				else {
					edgeSum.put(edge, 1 + integer);
				}
			}
		}

		DefaultCoverageResult result = new DefaultCoverageResult(nodeSum, edgeSum, kb);
		result.calculate();
		return result;
		
	}

	public static CoverageResult calculateResult(CoverageSessionObject coverage, KnowledgeBase kb) {
		Collection<CoverageSessionObject> coverages = new LinkedList<CoverageSessionObject>();
		coverages.add(coverage);
		return calculateResult(coverages, kb);
	}

}