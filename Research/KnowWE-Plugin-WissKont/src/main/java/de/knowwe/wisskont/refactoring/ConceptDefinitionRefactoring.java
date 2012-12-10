/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.wisskont.refactoring;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdfs.SimpleIRIDefintionMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class ConceptDefinitionRefactoring extends AbstractAction {

	public void refactor(UserActionContext user) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Collection<Article> articles = articleManager.getArticles();
		Map<String, String> replacementMap = new HashMap<String, String>();
		for (Article article : articles) {
			List<Section<SimpleIRIDefintionMarkup>> defs = Sections.findSuccessorsOfType(
					article.getRootSection(), SimpleIRIDefintionMarkup.class);
			if (defs.size() == 1) {
				// this refactoring is only applied on pages defining exactly
				// one term

				Section<SimpleIRIDefintionMarkup> defMarkup = defs.get(0);
				Section<SimpleDefinition> termDefinition = Sections.findSuccessor(defMarkup,
						SimpleDefinition.class);
				String termname = termDefinition.get().getTermName(termDefinition);
				String linebreak = System.getProperty("line.separator");
				replacementMap.put(defMarkup.getID(), linebreak + "Konzept: " + termname
						+ linebreak + linebreak);
			}
		}

		try {
			Sections.replaceSections(user, replacementMap);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		refactor(context);
		if (context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write("refactoring done");
		}

	}

}
