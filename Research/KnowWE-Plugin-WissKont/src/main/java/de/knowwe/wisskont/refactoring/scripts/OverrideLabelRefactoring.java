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
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.LabelMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 07.05.2013
 */
public class OverrideLabelRefactoring extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		refactor(context);
		if (context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write("refactoring done");
		}
	}

	private void refactor(UserActionContext context) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Collection<Article> articles = articleManager.getArticles();
		for (Article article : articles) {
			Map<String, String> replacementMap = new HashMap<String, String>();

			Section<LabelMarkup> labelMarkup = Sections.findSuccessor(article.getRootSection(),
					LabelMarkup.class);
			Section<ConceptMarkup> conceptMarkup = Sections.findSuccessor(article.getRootSection(),
					ConceptMarkup.class);

			if (labelMarkup != null && conceptMarkup != null) {
				List<Section<? extends Type>> children = labelMarkup.getChildren();
				Section<? extends Type> label = children.get(2);
				if (label != null && label.get().getName().contains("LabelType")
						&& label.getText().contains("..")) {
					Section<TermDefinition> def = Sections.findSuccessor(conceptMarkup,
							TermDefinition.class);
					String name = def.get().getTermName(def);
					String cleanedName = name.replaceAll("_", " ");
					replacementMap.put(label.getID(), cleanedName);
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
