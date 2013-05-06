package de.knowwe.ophtovisD3;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;


public class PartTreeAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String concept = context.getParameter("concept");
		if(concept==null)
			concept="";
		
		String responseString =GraphBuilder.builtPartTree(concept,"unterkonzept");
		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(responseString);
		
	}

}
