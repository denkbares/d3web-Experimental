/*
 * Copyright (C) 2013 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.wisskont.refactoring.scripts;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * 
 * @author jochenreutelshofer
 * @created 07.05.2013
 */
public class MoveRelationsBeforeDescriptionScript extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		refactor(context);
		if (context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write("refactoring done");
		}

	}

	/**
	 * 
	 * @created 07.05.2013
	 * @param context
	 */
	private void refactor(UserActionContext context) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Collection<Article> articles = articleManager.getArticles();
		for (Article article : articles) {
			Map<String, String> replacementMap = new HashMap<String, String>();
			String articleText = article.getRootSection().getText();

			String descriptionHeader = "!! Beschreibung";
			String realtionsHeader = "!! Beziehungen";
			if (articleText.contains(descriptionHeader) && articleText.contains(realtionsHeader)) {
				if (articleText.indexOf(descriptionHeader) < articleText.indexOf(realtionsHeader)) {
					Pattern relationsPattern = Pattern.compile("^" + realtionsHeader + ".*?\\z",
							Pattern.DOTALL | Pattern.MULTILINE);
					Matcher m = relationsPattern.matcher(articleText);
					if (m.find()) {
						String relationsText = m.group();
						String newText = articleText.replace(relationsText, "");
						newText = newText.replace(descriptionHeader, relationsText
								+ descriptionHeader);
						replacementMap.put(article.getRootSection().getID(), newText);
						try {
							Sections.replaceSections(context, replacementMap);
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}

	}
}
