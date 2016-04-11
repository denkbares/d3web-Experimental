package de.knowwe.d3web.scoreTable;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.instantedit.tools.InstantEditTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.util.Icon;

public class EditToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return true;
	}

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		return new Tool[] { getEditTool(section, userContext) };
	}

	private Tool getEditTool(Section<?> section, UserContext userContext) {

		String jsAction = "KNOWWE.plugin.tableEditTool.supportLinks('" + section.getID()
				+ "', false);";
		return new InstantEditTool(
				Icon.EDITTABLE,
				"Edit Table",
				"Edit this table in a spreadsheet-like editor",
				section,
				"KNOWWE.plugin.tableEditTool",
				jsAction);
	}

}
