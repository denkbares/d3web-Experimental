package de.knowwe.tool.conceptLink;


import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

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
