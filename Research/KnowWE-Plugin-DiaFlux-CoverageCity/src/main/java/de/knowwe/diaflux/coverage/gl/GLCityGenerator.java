/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverage.gl;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.CoverageResult;
import de.knowwe.diaflux.coverage.CoverageUtils;
import de.knowwe.diaflux.coverage.kdtree.AddRectanglesVisitor;
import de.knowwe.diaflux.coverage.kdtree.CollectAssignedRectangles;
import de.knowwe.diaflux.coverage.kdtree.KDNode;
import de.knowwe.diaflux.coverage.kdtree.MarginMapper;
import de.knowwe.diaflux.coverage.kdtree.RectanglePacker;
import de.knowwe.diaflux.coverage.kdtree.RectanglePacker.Mapping;
import de.knowwe.diaflux.coverage.metrics.IncomingEdgesMetric;
import de.knowwe.diaflux.coverage.metrics.MaximumInSameFlowNodeCoverage;
import de.knowwe.diaflux.coverage.metrics.Metrics;
import de.knowwe.diaflux.coverage.metrics.MetricsAggregator;
import de.knowwe.diaflux.coverage.metrics.NodeCoverageMetric;
import de.knowwe.diaflux.coverage.metrics.NodeTypeColorMetric;
import de.knowwe.diaflux.coverage.metrics.OutgoingEdgesMetric;

/**
 * 
 * @author Reinhard Hatko
 * @created 08.02.2012
 */
public class GLCityGenerator {


	/**
	 * 
	 * @created 16.02.2012
	 * @param kb
	 * @param coverage
	 * @return
	 */
	public static GLCity generateCity(KnowledgeBase kb, CoverageResult coverage) {
		MetricsAggregator<Node> metrics = createMetrics(coverage);

		Map<Flow, Collection<Flow>> structure = createStructure(kb);
		Map<Flow, GLDistrict> city = new HashMap<Flow, GLDistrict>();

		while (!structure.isEmpty()) {

			// find leaf
			Flow flow = null;
			for (Flow temp : structure.keySet()) {
				Collection<Flow> calledFlows = structure.get(temp);
				if (calledFlows.isEmpty()) {
					flow = temp;
					structure.remove(temp);
					break;
				}
			}

			assert flow != null;

			// remove leaf from tree
			for (Flow temp : structure.keySet()) {
				Collection<Flow> calledFlows = structure.get(temp);
				calledFlows.remove(flow);
			}

			GLDistrict district = createDistrict(kb, flow, metrics, city);
			city.put(flow, district);

		}

		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(kb);

		Flow mainFlow = nodes.get(0).getFlow();

		GLDistrict diaFluxCity = city.get(mainFlow);
		return new GLCity(diaFluxCity);
	}



	/**
	 * 
	 * @param coverage
	 * @created 08.02.2012
	 * @return
	 */
	public static MetricsAggregator<Node> createMetrics(CoverageResult coverage) {
		MetricsAggregator<Node> metrics = new MetricsAggregator<Node>();
		metrics.setHeightMetric(Metrics.multiply(Metrics.relate(new NodeCoverageMetric(coverage),
				new MaximumInSameFlowNodeCoverage(coverage)), 5));
		metrics.setLengthMetric(new IncomingEdgesMetric());
		metrics.setWidthMetric(new OutgoingEdgesMetric());
		metrics.setColorMetric(new NodeTypeColorMetric());
		return metrics;
	}


	private static GLDistrict createDistrict(KnowledgeBase kb, Flow flow, MetricsAggregator<Node> metrics, Map<Flow, GLDistrict> city) {
		
		List<GLBuilding> boxes = new LinkedList<GLBuilding>();
		
		for (Node node : flow.getNodes()) {

			GLBuilding box;
			if (node instanceof ComposedNode) {
				Flow calledFlow = CoverageUtils.getCalledFlow(kb, (ComposedNode) node);
				box = city.remove(calledFlow);
				// can be null in a leaf, that calls a flow, that came in the
				// tree earlier and is inserted elsewhere
				if (box == null) continue;
			}
			else {
				double xDim = metrics.getLength(node);
				double yDim = metrics.getHeight(node);
				double zDim = metrics.getWidth(node);
				Color color = metrics.getColor(node);
				box = new GLBuilding(xDim, yDim, zDim);
				box.setColor(color);
				box.setName(node.getID() +" in " + flow.getName());

			}
			boxes.add(box);
		}
		
		GLDistrict district = createDistrict(boxes, new GLNodeMapper(), 2, .3);
		district.setName(flow.getName());
		return district;
		
	}

	public static GLDistrict createDistrict(List<GLBuilding> boxes, Mapping<GLBuilding> mapper, double margin, double height) {
		KDNode<GLBuilding> root = RectanglePacker.pack(boxes, new MarginMapper<GLBuilding>(
				mapper, margin));

		Map<GLBuilding, Rectangle2D> rects =
				root.visitInorder(new
						CollectAssignedRectangles<GLBuilding>(margin)).getAssignments();

		Rectangle2D bounds = root.visitInorder(new AddRectanglesVisitor<GLBuilding>()).getBounds();
		GLDistrict ground = createGround(bounds.getWidth(), bounds.getHeight(), height);

		for (GLBuilding box : rects.keySet()) {
			Rectangle2D rectangle2d = rects.get(box);
			box.setX(rectangle2d.getX());
			box.setY(rectangle2d.getY());
			ground.addChild(box);

		}

		// RectanglePacker.printResult(root, "d:\\cities\\city" +
		// System.nanoTime());

		return ground;
	}

	public static GLDistrict createGround(double length, double width, double height) {

		GLDistrict group = new GLDistrict(length, width, height);
		group.setColor(new Color(128,128,128,100));
		

		return group;
	}


	/**
	 * 
	 * @created 08.02.2012
	 * @param kb
	 * @return
	 */
	private static Map<Flow, Collection<Flow>> createStructure(KnowledgeBase kb) {
		Map<Flow, Collection<Flow>> structure = CoverageUtils.createFlowStructure(kb);
		return structure;
	}

}
