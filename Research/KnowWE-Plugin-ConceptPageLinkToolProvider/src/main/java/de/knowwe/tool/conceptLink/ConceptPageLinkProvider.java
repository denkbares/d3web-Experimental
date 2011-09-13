package de.knowwe.tool.conceptLink;


import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.tools.DefaultTool;
import de.d3web.we.tools.Tool;
import de.d3web.we.tools.ToolProvider;
import de.d3web.we.user.UserContext;

public class ConceptPageLinkProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {

		String text = section.getOriginalText();
		Tool t =  new DefaultTool(
				"KnowWEExtension/images/dt_icon_premises.gif",
				"<a href='Wiki.jsp?page="+text+"'>"+text+"</a>",
				text,
				null);
		return new Tool[] { t };
	}
	
	

}
