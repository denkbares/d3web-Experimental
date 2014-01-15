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
import de.knowwe.wisskont.RelationMarkup;
import de.knowwe.wisskont.SubconceptMarkup;

public class AddListsScript extends AbstractAction {

	private final String CANKEY = CanMarkup.KEY + ": \n";
	private final String MUSTKEY = MustMarkup.KEY + ": \n";
	private final String CAVEKEY = CaveMarkup.KEY + ": \n";
	private final String ASSOCIATIONKEY = AssociationMarkup.KEY + ": \n";


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


		articleManager.open();
		try {

			for (Article article : articles) {
				Map<String, String> replacementMap = new HashMap<String, String>();
				Section<RootType> rootSection;

				if (hasConceptDefinition(article)) {
					rootSection = article.getRootSection();

					Section<RelationMarkup> someRelationMarkup = Sections.findSuccessor(
							rootSection, RelationMarkup.class);
					if (someRelationMarkup == null) {
						// should not happen
						continue;
					}

					Section<? extends Type> parentSection = someRelationMarkup.getParent();
					String newParentText = createReplaceText(parentSection) + "\n";
					replacementMap.put(parentSection.getID(), newParentText);
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
		finally {
			articleManager.commit();
		}
	}

	Class[] markupClasses = {
			CanMarkup.class, MustMarkup.class, CaveMarkup.class, AssociationMarkup.class };
	String[] appendTexts = {
			CANKEY, MUSTKEY, CAVEKEY, ASSOCIATIONKEY };

	private String createReplaceText(Section<? extends Type> parentSection) {
		String text = ""; 
		boolean[] addedFlags = new boolean[4];
		List<Section<? extends Type>> children = parentSection.getChildren();
		for (Section<? extends Type> child : children) {
			boolean isLastList = false;
			if (child.get() instanceof RelationMarkup) {

				if (!(child.get() instanceof SubconceptMarkup)) {
					// we need to add those missing before
					for (int i = 0; i < 4; i++) {
						if (child.get().getClass().equals(markupClasses[i])) {
							break;
						}
						// add those which are missing and have not been added
						if ((!addedFlags[i])
								&& (!containsMarkupSection(markupClasses[i], parentSection))) {
							text += appendTexts[i];
							addedFlags[i] = true;
						}
					}

				}


				/*
				 * if this is the last list item, append all missing ones
				 */
				List<Section<RelationMarkup>> listMarkups = Sections.findSuccessorsOfType(
						parentSection, RelationMarkup.class);
				if (child == listMarkups.get(listMarkups.size() - 1)) {
					isLastList = true;

				}
			}

			text += child.getText();

			if (isLastList) {
				// append all missing ones
				text += "\n\n";
				for (int i = 0; i < 4; i++) {
					if ((!addedFlags[i])
							&& (!containsMarkupSection(markupClasses[i], parentSection))) {
						text += appendTexts[i];
						addedFlags[i] = true;
					}
				}
			}
		}
		return text;
	}

	private boolean containsMarkupSection(Class<? extends Type> clazz, Section<? extends Type> parentSection) {
		return Sections.findSuccessor(parentSection, clazz) != null;
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
