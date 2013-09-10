package de.knowwe.ophtovisD3;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.ophtovisD3.utils.Connections;
import de.knowwe.ophtovisD3.utils.JsonFactory;


public class ConnectionsAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String concept = context.getParameter("concept");
		if(concept==null)
			concept="";
		Connections con =DataBaseHelper.getConnectionObject(concept);
		String response = JsonFactory.toJSON(con);
		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(response);
	}

}
