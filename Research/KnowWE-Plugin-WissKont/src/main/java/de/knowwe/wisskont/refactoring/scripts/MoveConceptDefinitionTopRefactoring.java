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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.dashtree.DashSubtree;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.LabelMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 06.05.2013
 */
public class MoveConceptDefinitionTopRefactoring extends AbstractAction {

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
	 * @created 06.05.2013
	 * @param context
	 */
	private void refactor(UserActionContext context) {
		ArticleManager articleManager = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		Collection<Article> articles = articleManager.getArticles();
		for (Article article : articles) {
			Map<String, String> replacementMap = new HashMap<String, String>();

			Section<RootType> rootSection = article.getRootSection();
			Section<?> insertAfterSection = Sections.findSuccessor(rootSection, LabelMarkup.class);
			Section<ConceptMarkup> concept = Sections.findSuccessor(rootSection,
					ConceptMarkup.class);
			if (insertAfterSection == null) {
				insertAfterSection = Sections.findSuccessor(rootSection,
						ConceptMarkup.class);

			}
			if (insertAfterSection != null) {
				Section<TermDefinition> term = Sections.findSuccessor(concept, TermDefinition.class);

				Collection<Section<? extends SimpleReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
						new Identifier(term.get().getTermName(term)));

				List<String> terms = new ArrayList<String>();

				for (Section<? extends SimpleReference> section : termReferences) {
					if (section.getTitle().equals("Konzepthierarchie")) {
						List<Section<TermReference>> children = new ArrayList<Section<TermReference>>();
						Section<? extends Type> searchRoot = section.getFather().getFather().getFather();
						if (!(searchRoot.get() instanceof DashSubtree)) {
							continue;
						}
						Sections.findSuccessorsOfType(searchRoot,
								TermReference.class, 4, children);

						for (Section<TermReference> child : children) {
							if (child.get().getTermName(child).equals(term.get().getTermName(term))) {
								continue; // self
							}
							else {
								terms.add(child.get().getTermName(child));

							}
						}
					}
				}

				if (terms.size() > 0) {
					String newText = "\n!! Unterbegriffe\n\nUnterbegriffe: ";
					for (String childName : terms) {

						newText += childName + ", ";
					}
					newText = newText.substring(0, newText.length() - 2);
					newText += "\n";
					replacementMap.put(insertAfterSection.getID(), insertAfterSection.getText()
							+ newText);
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
