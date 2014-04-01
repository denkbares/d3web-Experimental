package de.knowwe.ophtovisD3;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

public class PartTreeAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String concept = context.getParameter("concept");
		if (concept == null) {
			concept = "";
		}
		String connectionType = context.getParameter("contype");
		System.out.println("connectionType = " + connectionType);
		System.out.println("test");
		if (connectionType == null) {
			connectionType = "unterkonzept";
		}
		String responseString = GraphBuilder.buildPartTree(concept, connectionType);
		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(responseString);
	}

}
