package de.knowwe.wisskont.navigation;

import java.io.IOException;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.rendering.RenderResult;

public class RerenderConceptOverviewAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/plain; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String perform(UserActionContext context) {
		String title = context.getTitle();
		RenderResult resultBuffer = new RenderResult(context);
		new ShowConceptRelationsAppendHandler().append(Environment.DEFAULT_WEB,
				title, context, resultBuffer);

		return resultBuffer.toString();
	}

}
