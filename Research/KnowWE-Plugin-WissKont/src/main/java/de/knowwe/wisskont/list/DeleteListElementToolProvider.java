package de.knowwe.wisskont.list;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class DeleteListElementToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return true;
	}

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		String context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();
		String iconPath = context + "/KnowWEExtension/css/images/delete_icon.png";
		String jsAction = "sendDeleteAction('" + section.getID() + "')";

		DefaultTool tool = new DefaultTool(
				iconPath,
				"Löschen",
				"Diesen Begriff aus der Liste entfernen",
				jsAction);
		return new Tool[] { tool };
	}
}
