package de.d3web.we.diaflux.pathcoloring;

import java.util.HashMap;
import java.util.Map;

import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;


public class AnomalyManager {
	
	private class AnomaliesList {
		
		private Map<Edge, String> edges;
		private Map<Node, String> nodes;
		
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
	
	public static AnomalyManager getAnomalyManager() {
		if(anomalyManager == null) {
			anomalyManager = new AnomalyManager();
		}
		return anomalyManager;
	}

	public void addAnomaly(Flow flow, Edge edge, String anomaly) {
		if(!anomalies.containsKey(flow)) {
			AnomaliesList newList = new AnomaliesList();
			newList.addAnomaly(edge, anomaly);

			anomalies.put(flow, newList);
		} else {
			AnomaliesList list = anomalies.get(flow);
			list.addAnomaly(edge, anomaly);
			anomalies.put(flow, list);
		}
	}
	
	public void addAnomaly(Flow flow, Node node, String anomaly) {
		if(!anomalies.containsKey(flow)) {
			AnomaliesList newList = new AnomaliesList();
			newList.addAnomaly(node, anomaly);

			anomalies.put(flow, newList);
		} else {
			AnomaliesList list = anomalies.get(flow);
			list.addAnomaly(node, anomaly);
			anomalies.put(flow, list);
		}
	}
	
	public Map<Edge, String> getAnomalyEdges(Flow flow) {
		if(!anomalies.containsKey(flow)){

			anomalies.put(flow, new AnomaliesList());
		}
		AnomaliesList list = anomalies.get(flow);
		return list.getEdges();
	}
	
	public Map<Node, String> getAnomalyNodes(Flow flow) {

		if(!anomalies.containsKey(flow)){
			anomalies.put(flow, new AnomaliesList());
		}
		AnomaliesList list = anomalies.get(flow);
		return list.getNodes();
	}
	
	public void deleteFlow(Flow flow) {
		anomalies.remove(flow);
	}
}
