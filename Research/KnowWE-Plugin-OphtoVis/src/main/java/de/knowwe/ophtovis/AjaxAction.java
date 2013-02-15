package de.knowwe.ophtovis;

import java.io.IOException;
import java.util.LinkedList;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.ophtovis.GraphBuilder;


public class AjaxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String nodename =context.getParameter("node");
		String id =context.getParameter("id");
		String parent =context.getParameter("parent");
		System.out.println("name of node " + nodename + "id " + id + "parent " + parent);
		GraphNode node = new GraphNode(nodename, 0, Integer.parseInt(id),Integer.parseInt(parent) , true);
		GraphBuilder builder = GraphBuilder.getInstance();
		LinkedList<GraphNode> nodes = (LinkedList<GraphNode>) builder.buildNodeAndCoList(node,0,Integer.parseInt(parent),false);
		for (GraphNode graphNode : nodes) {
			graphNode.setId(graphNode.getId()+1000);
		}
		
		LinkedList <GraphNodeConnection>connections=(LinkedList<GraphNodeConnection>) builder.getConnections();
		String responseString=Visualization.convertNodelistToJSwithDrawnFather(nodes, connections, id);
		responseString+= Visualization.convertConnectionlistToJS(connections);
		
		System.out.println(responseString);
		context.setContentType("text/html; charset=UTF-8");
		context.getWriter().write(responseString);
		
	
		
	}

}
