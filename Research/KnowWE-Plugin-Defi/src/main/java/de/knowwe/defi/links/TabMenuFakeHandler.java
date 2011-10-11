package de.knowwe.defi.links;

import java.util.Map;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

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
