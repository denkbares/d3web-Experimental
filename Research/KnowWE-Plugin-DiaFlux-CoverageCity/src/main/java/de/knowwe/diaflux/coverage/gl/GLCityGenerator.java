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
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.diaflux.FlowchartUtils;
import de.knowwe.diaflux.coverage.CoverageUtils;
import de.knowwe.diaflux.coverage.kdtree.AddRectanglesVisitor;
import de.knowwe.diaflux.coverage.kdtree.CollectAssignedRectangles;
import de.knowwe.diaflux.coverage.kdtree.KDNode;
import de.knowwe.diaflux.coverage.kdtree.MarginMapper;
import de.knowwe.diaflux.coverage.kdtree.RectanglePacker;
import de.knowwe.diaflux.coverage.kdtree.RectanglePacker.Mapping;
import de.knowwe.diaflux.coverage.metrics.Constant;
import de.knowwe.diaflux.coverage.metrics.CoveredOutgoingEdgesMetric;
import de.knowwe.diaflux.coverage.metrics.CoveredPathsColorMetric;
import de.knowwe.diaflux.coverage.metrics.Metrics;
import de.knowwe.diaflux.coverage.metrics.MetricsSet;
import de.knowwe.diaflux.coverage.metrics.NodeCoverageMetric;
import de.knowwe.diaflux.coverage.metrics.OutgoingEdgesMetric;
import de.knowwe.diaflux.type.FlowchartType;

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
	public static GLCity generateCity(CoverageResult coverage) {
		MetricsSet<Node> metrics = createMetrics(coverage);

		KnowledgeBase kb = coverage.getKb();
		Map<Flow, Collection<Flow>> structure = CoverageUtils.createFlowStructure(kb);
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
	public static MetricsSet<Node> createMetrics(CoverageResult coverage) {
		MetricsSet<Node> metrics = new MetricsSet<Node>();
		// metrics.setHeightMetric(Metrics.multiply(Metrics.relate(new
		// NodeCoverageMetric(coverage),
		// new MaximumInSameFlowNodeCoverage(coverage)), 5));
		metrics.setHeightMetric(new CoveredOutgoingEdgesMetric(coverage));
		metrics.setLengthMetric(new OutgoingEdgesMetric());
		metrics.setWidthMetric(Metrics.relate(new NodeCoverageMetric(coverage),
				new Constant<Node>(15)));
		// metrics.setColorMetric(new NodeTypeColorMetric());
		metrics.setColorMetric(new CoveredPathsColorMetric(coverage));
		return metrics;
	}


	private static GLDistrict createDistrict(KnowledgeBase kb, Flow flow, MetricsSet<Node> metrics, Map<Flow, GLDistrict> city) {
		
		List<GLBuilding> boxes = new LinkedList<GLBuilding>();
		
		// List<Node> nodes = flow.getNodes();
		Collection<Node> nodes = DefaultCoverageResult.getValidNodes(flow);
		for (Node node : nodes) {

			GLBuilding box;
			if (node instanceof ComposedNode) {
				Flow calledFlow = DiaFluxUtils.getCalledFlow(kb, (ComposedNode) node);
				if (CoverageUtils.isWaitNode(calledFlow)) {
					box = createBuilding(node, metrics);

				}
				else {
					box = city.remove(calledFlow);
					// can be null in a leaf, that calls a flow, that came in
					// the tree earlier and is inserted elsewhere
					if (box == null) continue;

				}

			}
			else {
				box = createBuilding(node, metrics);

			}
			boxes.add(box);
		}
		
		GLDistrict district = createDistrict(boxes, new GLNodeMapper(), 3, .75);
		district.setName("" + flow.getName() + "");
		return district;
		
	}



	/**
	 * 
	 * @created 22.05.2012
	 * @param node
	 * @param metrics
	 * @return
	 */
	public static GLBuilding createBuilding(Node node, MetricsSet<Node> metrics) {
		GLBuilding box;
		Flow flow = node.getFlow();
		Section<FlowchartType> flowSec = FlowchartUtils.findFlowchartSection("default_web",
				flow.getName());
		double xDim = metrics.getLength(node);
		double yDim = metrics.getHeight(node);
		double zDim = metrics.getWidth(node);
		Color color = metrics.getColor(node);
		box = new GLBuilding(xDim, yDim, zDim);
		box.setColor(color);
		box.setName(flow.getName() +"+++" + flowSec.getID() + "+++" + node.getID() + "");
		return box;
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


}
