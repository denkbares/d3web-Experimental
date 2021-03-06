package de.knowwe.ophtovisD3;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

public class AjaxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		GraphBuilder builder = new GraphBuilder();
		String type, concept;
		type = context.getParameter("type");
		concept = context.getParameter("concept");

		if (type == null) {
			type = "";
		}
		String connectionType = context.getParameter("contype");
		if (connectionType == null) {
			connectionType = "unterkonzept";
		}
		String responseString;
		if (type.equals("force")) {
			responseString = builder.buildNamesandConnectionsJSON("Anamnese_Patientensituation", connectionType);
		}
		else if ((type.equals("bubble"))) {
			//responseString = GraphBuilder.buildGraph(concept, "unterkonzept", "temporalGraph",true);
			responseString = GraphBuilder.buildGraphExperimental(connectionType, concept);
		}
		else {
			// responseString = GraphBuilder.buildGraph(concept, "unterkonzept", "temporalGraph",false);
			responseString = GraphBuilder.buildGraphExperimental(connectionType, concept);
		}

		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(responseString);

	}

}

