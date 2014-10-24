package de.knowwe.diaflux.coverageCity;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.util.Icon;

public class CoverageCityToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		String url =
				"CoverageCity.jsp?kdomID=" + section.getID();
		String winID = section.getID().replaceAll("[^\\w]", "_");

		String jsAction = "javascript:window.open('" + url + "', '" + winID + "');";

		return new Tool[] {
				new DefaultTool(Icon.NONE, "Show CoverageCity",
						"Shows the city visualization of the coverage", jsAction, Tool.CATEGORY_INFO)
		};
	}

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return true;
	}
}
