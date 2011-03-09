package de.d3web.we.drools.action.utils;

import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;

public class DroolsUtils {


	/**
	 * Loads the required KnowWEArticle
	 *
	 * @param context, stores the information which are necessary to load the article
	 * @return the loaded KnowWEArticle
	 */
	public static KnowWEArticle loadArticle(UserActionContext context) {
		String web = context.getParameter(KnowWEAttributes.WEB);
		String title = context.getParameter("title");
		return KnowWEEnvironment.getInstance().getArticleManager(web)
				.getArticle(title);
	}

}
