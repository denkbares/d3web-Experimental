/*
 * Copyright (C) 2014 University Wuerzburg, Computer Science VI
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
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.wisskont.AssociationMarkup;
import de.knowwe.wisskont.CanMarkup;
import de.knowwe.wisskont.CaveMarkup;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.MustMarkup;

/**
 * 
 * @author Johanna Latt
 * @created 08.01.2014
 */
public class FillInMissingDefaultListsRefactoring extends AbstractAction {

	private final String CANKEY = CanMarkup.KEY + ": \n";
	private final String MUSTKEY = MustMarkup.KEY + ": \n";
	private final String CAVEKEY = CaveMarkup.KEY + ": \n";
	private final String ASSOCIATIONKEY = AssociationMarkup.KEY + ": \n";

	private Section<? extends Type> canMarkup;
	private Section<? extends Type> mustMarkup;
	private Section<? extends Type> caveMarkup;
	private Section<? extends Type> associationMarkup;

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

		Map<String, String> replacementMap = new HashMap<String, String>();
		Section<RootType> rootSection;

		articleManager.open();
		try {
			for (Article article : articles) {
				if (hasConceptDefinition(article)) {
					rootSection = article.getRootSection();

					replacementMap.clear();

					// check if any of the four default sections already exist
					canMarkup = Sections.findChildOfType(rootSection, CanMarkup.class);
					mustMarkup = Sections.findChildOfType(rootSection, MustMarkup.class);
					caveMarkup = Sections.findChildOfType(rootSection, CaveMarkup.class);
					associationMarkup = Sections.findChildOfType(rootSection,
							AssociationMarkup.class);

					if (canMarkup == null) {
						insertMarkups(rootSection, replacementMap);
					}
					else {
						insertRemainingMarkups(rootSection, replacementMap);
					}

					try {
						Sections.replaceSections(context, replacementMap);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		finally {
			articleManager.commit();
		}
	}

	/**
	 * Finds the correct position for the default sections depending on where
	 * the other, maybe already existing sections are and inserts the keys for
	 * the missing markups.
	 * 
	 * @created 09.01.2014
	 */
	private void insertMarkups(Section<? extends Type> rootSection, Map<String, String> replacementMap) {
		List<Section<? extends Type>> rootChildren = rootSection.getChildren();
		String key;
		String value;

		if (mustMarkup == null) {
			// none of the default sections exist yet: All of them are inserted
			// at the end of the article
			if (caveMarkup == null && associationMarkup == null) {
				Section<? extends Type> lastSection = rootChildren.get(rootChildren.size() - 1);
				key = lastSection.getID();
				value = lastSection.getText() + "\n" + CANKEY + MUSTKEY + CAVEKEY + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup == null && associationMarkup != null) {
				key = associationMarkup.getID();
				value = CANKEY + MUSTKEY + CAVEKEY + associationMarkup.getText();
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup == null) {
				key = caveMarkup.getID();
				value = CANKEY + MUSTKEY + caveMarkup.getText() + "\n" + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup != null) {
				key = caveMarkup.getID();
				value = CANKEY + MUSTKEY + caveMarkup.getText();
				replacementMap.put(key, value);
				return;
			}
		}
		else {
			if (caveMarkup == null && associationMarkup == null) {
				key = mustMarkup.getID();
				value = CANKEY + mustMarkup.getText() + "\n" + CAVEKEY + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup != null) {
				key = mustMarkup.getID();
				value = CANKEY + mustMarkup.getText();
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup == null && associationMarkup != null) {
				key = mustMarkup.getID();
				value = CANKEY + mustMarkup.getText() + "\n" + CAVEKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup == null) {
				key = mustMarkup.getID();
				value = CANKEY + mustMarkup.getText();
				replacementMap.put(key, value);

				key = caveMarkup.getID();
				value = caveMarkup.getText() + "\n" + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}
		}
	}

	/**
	 * Finds the correct position for all other sections depending on where the
	 * other, maybe already existing sections are and inserts the keys for the
	 * missing markups.
	 * 
	 * @created 14.01.2014
	 */
	private void insertRemainingMarkups(Section<? extends Type> rootSection, Map<String, String> replacementMap) {
		String key = canMarkup.getID();
		String value;

		if (mustMarkup == null) {
			if (caveMarkup == null && associationMarkup == null) {
				value = canMarkup.getText() + "\n" + MUSTKEY + CAVEKEY + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup == null && associationMarkup != null) {
				value = canMarkup.getText() + "\n" + MUSTKEY + CAVEKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup == null) {
				key = mustMarkup.getID();
				value = CANKEY + mustMarkup.getText();
				replacementMap.put(key, value);

				key = caveMarkup.getID();
				value = caveMarkup.getText() + "\n" + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup != null) {
				value = canMarkup.getText() + "\n" + MUSTKEY;
				replacementMap.put(key, value);
				return;
			}
		}
		else {
			if (caveMarkup == null && associationMarkup == null) {
				key = mustMarkup.getID();
				value = mustMarkup.getText() + "\n" + CAVEKEY + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup == null && associationMarkup != null) {
				key = mustMarkup.getID();
				value = mustMarkup.getText() + "\n" + CAVEKEY;
				replacementMap.put(key, value);
				return;
			}

			if (caveMarkup != null && associationMarkup == null) {
				key = caveMarkup.getID();
				value = caveMarkup.getText() + "\n" + ASSOCIATIONKEY;
				replacementMap.put(key, value);
				return;
			}
		}
	}

	private static boolean hasConceptDefinition(Article a) {
		return getConceptName(a) != null;
	}

	private static String getConceptName(Article a) {
		Section<ConceptMarkup> def = Sections.findSuccessor(a.getRootSection(), ConceptMarkup.class);
		if (def != null) {
			Section<SimpleDefinition> termDef = Sections.findSuccessor(def, SimpleDefinition.class);
			return termDef.get().getTermName(termDef);
		}
		return null;
	}
}