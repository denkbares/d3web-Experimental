/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.onte.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.types.Delimiter;
import de.knowwe.kdom.manchester.types.HiddenComment;
import de.knowwe.onte.editor.OWLApiAxiomCache;
import de.knowwe.onte.editor.OWLApiReplacementVisitor;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

/**
 * OWLApiRepairInconsistencies.
 *
 * 
 * @author Stefan Mark
 * @created 30.11.2011
 */
public class OWLApiRepairInconsistencies extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		// only allow known users to execute the repair action
		boolean isAuthenticated = context.userIsAsserted();
		if (!isAuthenticated) {
			context.sendError(403,
					"I am sorry. I could not verify your identity. Please log in and try again.");
			return;
		}

		String option = context.getParameter("options");
		String[] selectedAxioms = (option != null) ? option.split("::") : new String[0];

		for (String sectionID : selectedAxioms) {

			Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSectionPerID(
					sectionID, OWLApiAxiomCache.STORE_EXPLANATION);
			String articlename = section.getArticle().getTitle();


			// ... then lock the page to avoid changes by other users ...
			boolean isPageLocked = Environment.getInstance().getWikiConnector()
										.isArticleLocked(articlename);
			 if (isPageLocked) {
				context.sendError(
						403,
						"I am sorry. The page is being edited by another user. Please try again later.");
				return;
			 }
			 else {
				Environment.getInstance().getWikiConnector().lockArticle(articlename,
							context.getUserName());

				// ... create the according replacements
				ArticleManager mgr = Environment.getInstance().getArticleManager(
							context.getWeb());

				String replacement = getReplacementText(sectionID);
				Map<String, String> nodesMap = new HashMap<String, String>();

				// ... now check for possible delimiter
				Section<Delimiter> possibleDelimiter = lookForDelimiter(sectionID);
				if (possibleDelimiter != null) {
					nodesMap.put(possibleDelimiter.getID(), "");
				}

				nodesMap.put(section.getID(),
						section.getText().replace(replacement,
								createHiddenComment(replacement, possibleDelimiter, context)));
				Sections.replaceSections(context, nodesMap);

				// .. and finally delete the page lock
				Environment.getInstance().getWikiConnector().unlockArticle(articlename);
				context.getWriter().write("{msg : 'axioms deleted', success : 'true'}");
			}
		}
	}

	/**
	 * Creates a comment string that is inserted in the replaced section on
	 * repair action. This comment indicates when and from whom the action has
	 * been executed.
	 *
	 * @created 30.11.2011
	 * @param replacement
	 * @param section
	 * @return
	 */
	private String createHiddenComment(String replacement, Section<?> section, UserActionContext context) {

		StringBuilder comment = new StringBuilder();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

		comment.append(HiddenComment.OPEN_TAG);
		comment.append("$Comment: Ontology Repaired ");
		comment.append(dfm.format(cal.getTime())).append(" ");
		comment.append(context.getUserName());
		comment.append("$ ");
		comment.append(replacement);

		if (section != null) {
			comment.append(section.getText()).append(" ");
			comment.append(HiddenComment.CLOSE_TAG);
		}
		return comment.toString();
	}

	/**
	 * Determines the replacement text from the given section. Therefore a look
	 * up is made in the stored sections.
	 *
	 * @created 30.11.2011
	 * @param sectionID
	 * @return
	 */
	private String getReplacementText(String sectionID) {
		Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSectionPerID(
				sectionID, OWLApiAxiomCache.STORE_EXPLANATION);

		Set<OWLObject> objects = OWLApiAxiomCache.getInstance().getStoredObjects(section,
				OWLApiAxiomCache.STORE_EXPLANATION);

		Section<ClassFrame> frame = Sections.findAncestorOfExactType(section, ClassFrame.class);

		for (OWLObject owlObject : objects) {

			OWLApiReplacementVisitor visitor = new OWLApiReplacementVisitor();
			OWLEntity entity = AxiomFactory.getOWLAPIEntity(frame.get().getClassDefinition(frame),
					OWLClass.class);
			visitor.setOWLClass(entity);
			owlObject.accept(visitor);
			OWLClassExpression exp = visitor.getReplacement();

			String replacement = OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(exp);
			return replacement;
		}
		return "";
	}

	/**
	 * Determines if the replacement is within a list, etc. and therefore a
	 * subsequent delimiter should also be removed. If a delimiter can be found
	 * the according section is returned for further handling.
	 *
	 * @created 30.11.2011
	 * @param sectionID
	 * @return
	 */
	private Section<Delimiter> lookForDelimiter(String sectionID) {

		// look up section for given section ID ...
		Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSectionPerID(
				sectionID, OWLApiAxiomCache.STORE_EXPLANATION);

		// ... get father MCE, this is either a list or not ...
		Section<ManchesterClassExpression> mce = Sections.findAncestorOfExactType(section,
				ManchesterClassExpression.class);

		if (mce.get().isNonTerminalList(mce)) {
			List<Section<?>> children = mce.getChildren();
			for (Section<?> child : children) {

				// .. found the correct position within the list
				String sectionText = section.getText().trim();
				String childText = child.getText().trim();

				if (childText.equals(sectionText)) {
					int index = children.indexOf(child);
					if (index == children.size() - 1) {
						return null; // .. end of list
					}
					else {
						return Sections.findSuccessor(children.get(index + 1),
								Delimiter.class);
					}
				}
			}
		}
		return null;
	}
}
