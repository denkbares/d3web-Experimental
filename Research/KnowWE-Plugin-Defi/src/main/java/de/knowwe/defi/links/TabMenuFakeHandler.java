package de.knowwe.defi.links;

import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class TabMenuFakeHandler extends AbstractTagHandler {

	public TabMenuFakeHandler() {
		super("tabmenu");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		if (parameters.containsKey("pages")) {
			String pages = parameters.get("pages");
			String[] pageNames = pages.split("\\|");

			String baseUrl = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl();

			StringBuffer buffy = new StringBuffer();
			buffy.append(KnowWEUtils.maskHTML("<div class='tabmenu'>"));
			for (String page : pageNames) {
				String clazz = "";
				if (page.trim().equals(article.getTitle())) {
					clazz = "activetab";
				}
				buffy.append(KnowWEUtils.maskHTML("<a href='" + baseUrl
						+ "Wiki.jsp?page=" + page.trim()
						+ "' class='" + clazz + "'>"));
				buffy.append(KnowWEUtils.maskHTML(page.trim()));
				buffy.append(KnowWEUtils.maskHTML("</a>"));
			}
			buffy.append(KnowWEUtils.maskHTML("</div>"));
			return buffy.toString();

		}
		return "no pages specified: 'pages=page1|page2'";
	}

}
