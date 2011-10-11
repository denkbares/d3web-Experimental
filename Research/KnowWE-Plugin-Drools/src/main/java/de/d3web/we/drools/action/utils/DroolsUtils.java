package de.d3web.we.drools.action.utils;

import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;

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
