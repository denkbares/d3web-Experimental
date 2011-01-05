package de.d3web.we.lod.action;

import de.d3web.we.core.KnowWEParameterMap;
import java.io.IOException;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;

public class GetDataAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {
		KnowWEParameterMap map = context.getKnowWEParameterMap();
		context.getWriter().write("test");
	}

}
