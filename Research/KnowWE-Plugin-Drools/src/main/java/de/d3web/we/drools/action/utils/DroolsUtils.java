package de.d3web.we.drools.action.utils;

import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;

public class DroolsUtils {

	/**
	 * Loads the required Article
	 * 
	 * @param context, stores the information which are necessary to load the
	 *        article
	 * @return the loaded Article
	 */
	public static Article loadArticle(UserActionContext context) {
		String web = context.getParameter(Attributes.WEB);
		String title = context.getParameter("title");
		return Environment.getInstance().getArticleManager(web)
				.getArticle(title);
	}

}
