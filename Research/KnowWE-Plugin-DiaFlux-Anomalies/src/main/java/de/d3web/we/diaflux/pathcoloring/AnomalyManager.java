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
package de.d3web.we.diaflux.pathcoloring;

import java.util.HashMap;
import java.util.Map;

import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class AnomalyManager {

	private class AnomaliesList {

		private final Map<Edge, String> edges;
		private final Map<Node, String> nodes;

		public AnomaliesList() {
			edges = new HashMap<Edge, String>();
			nodes = new HashMap<Node, String>();
		}

		public void addAnomaly(Edge edge, String string) {
			edges.put(edge, string);
		}

		public void addAnomaly(Node node, String string) {
			nodes.put(node, string);
		}

		public Map<Edge, String> getEdges() {
			return edges;
		}

		public Map<Node, String> getNodes() {
			return nodes;
		}

	}

	private static AnomalyManager anomalyManager;

	private static Map<Flow, AnomaliesList> anomalies;

	private AnomalyManager() {
		anomalies = new HashMap<Flow, AnomaliesList>();
	}

	/**
	 * returns an AnomalyManager-Singleton
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public static AnomalyManager getAnomalyManager() {
		if (anomalyManager == null) {
			anomalyManager = new AnomalyManager();
		}
		return anomalyManager;
	}

	/**
	 * adds an Anomaly that appears in an edge
	 * 
	 * @created 08.05.2012
	 * @param flow
	 * @param edge
	 * @param anomaly
	 */
	public void addAnomaly(Flow flow, Edge edge, String anomaly) {
		if (!anomalies.containsKey(flow)) {
			AnomaliesList newList = new AnomaliesList();
			newList.addAnomaly(edge, anomaly);

			anomalies.put(flow, newList);
		}
		else {
			AnomaliesList list = anomalies.get(flow);
			list.addAnomaly(edge, anomaly);
			anomalies.put(flow, list);
		}
	}

	/**
	 * adds an Anomaly that appears in a node
	 * 
	 * @created 08.05.2012
	 * @param flow
	 * @param node
	 * @param anomaly
	 */
	public void addAnomaly(Flow flow, Node node, String anomaly) {
		if (!anomalies.containsKey(flow)) {
			AnomaliesList newList = new AnomaliesList();
			newList.addAnomaly(node, anomaly);

			anomalies.put(flow, newList);
		}
		else {
			AnomaliesList list = anomalies.get(flow);
			list.addAnomaly(node, anomaly);
			anomalies.put(flow, list);
		}
	}

	/**
	 * returns all Edge-Anomalies appearing in the Flowchart
	 * 
	 * @created 08.05.2012
	 * @param flow
	 * @return
	 */
	public Map<Edge, String> getAnomalyEdges(Flow flow) {
		if (!anomalies.containsKey(flow)) {

			anomalies.put(flow, new AnomaliesList());
		}
		AnomaliesList list = anomalies.get(flow);
		return list.getEdges();
	}

	/**
	 * returns all Node-Anomalies appearing in the Flowchart
	 * 
	 * @created 08.05.2012
	 * @param flow
	 * @return
	 */
	public Map<Node, String> getAnomalyNodes(Flow flow) {

		if (!anomalies.containsKey(flow)) {
			anomalies.put(flow, new AnomaliesList());
		}
		AnomaliesList list = anomalies.get(flow);
		return list.getNodes();
	}

	public void deleteFlow(Flow flow) {
		anomalies.remove(flow);
	}
}
