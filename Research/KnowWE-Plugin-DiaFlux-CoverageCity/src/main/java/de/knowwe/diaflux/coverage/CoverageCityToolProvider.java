package de.knowwe.diaflux.coverage;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class CoverageCityToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		String url =
				"CoverageCity.jsp?kdomID=" + section.getID();
		String winID = section.getID().replaceAll("[^\\w]", "_");
		
		String jsAction = "javascript:window.open('" + url + "', '" + winID + "');";
		
		return new Tool[]{
			new DefaultTool(null, "Show CoverageCity", "Shows the city visualization of the coverage", jsAction) 
			
			
		};
	}

}
