package de.knowwe.defi;

import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The TabbedLinkTaghandler simplifies the creation of links to tabbed sections
 * in a wiki article. Please use the following syntax:
 * 
 * <blockquote> [{KnowWEPlugin tabbedlinktag , tab=NameOfTab , page=PageName ,
 * title=LinkName}] </blockquote>
 *
 * The title attribute is optional. If not given a combination of page and tab
 * value is used as the name of the link. (e.g.: PagenName &raquo; NameOfTab)
 *
 * @author smark
 * @created 28.02.2011
 */
public class TabbedLinkTaghandler extends AbstractTagHandler {

	public TabbedLinkTaghandler() {
		super("tabbedlinktag");
	}


	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		StringBuilder tabbedLink = new StringBuilder();

		String tab = parameters.get("tab");
		String page = parameters.get("page");
		String title = parameters.get("title");

		if (tab != null && page != null && tab != "" && page != "") {

			if (title == null || title == "") {
				title = page + " &raquo; " + tab;
			}

			tabbedLink.append("<a href=\"Wiki.jsp?page=");
			tabbedLink.append(KnowWEUtils.urlencode(page.trim()));
			tabbedLink.append("&amp;tab=");
			tabbedLink.append(KnowWEUtils.urlencode(tab.trim()));
			tabbedLink.append("\" title=\"Title:");
			tabbedLink.append(title);
			tabbedLink.append("\" rel=\"nofollow\">");
			tabbedLink.append(title);
			tabbedLink.append("</a>");
		}
		return KnowWEUtils.maskHTML(tabbedLink.toString());
	}
}
