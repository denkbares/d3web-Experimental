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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.types.Delimiter;
import de.knowwe.onte.editor.OWLApiAxiomCache;
import de.knowwe.onte.editor.OWLApiReplacementVisitor;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

public class OWLApiRepairInconsistencies extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String option = context.getParameter("options");
		String[] selectedAxioms = (option != null) ? option.split("::") : new String[0];

		for (String sectionID : selectedAxioms) {

			Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSectionPerID(
					sectionID, OWLApiAxiomCache.STORE_EXPLANATION);
			String articlename = section.getArticle().getTitle();


			// ... then lock the page to avoid changes by other users ...
			boolean isPageLocked = KnowWEEnvironment.getInstance().getWikiConnector()
										.isPageLocked(articlename);
			 if (isPageLocked) {
				context.getWriter().write("{msg : 'page locked sorry', success : 'false'}");
			 }
			 else {
				KnowWEEnvironment.getInstance().getWikiConnector().setPageLocked(articlename,
							context.getUserName());

				// ... create the according replacements
				KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
							context.getWeb());

				// ... replace the results ...
				String replacement = getReplacementText(sectionID);
				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(section.getID(), section.getOriginalText().replace(replacement, ""));

				Section<? extends Type> possibleDelimiter = getDelimiterReplacing(sectionID);
				if (possibleDelimiter != null) {
					nodesMap.put(possibleDelimiter.getID(), "");
				}

				Sections.replaceSections(context, nodesMap);

				// .. and finally delete the page lock
				KnowWEEnvironment.getInstance().getWikiConnector().undoPageLocked(articlename);
				context.getWriter().write("{msg : 'axioms deleted', success : 'true'}");
			}
		}
	}

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

	private Section<? extends Type> getDelimiterReplacing(String sectionID) {
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

			Section<ManchesterClassExpression> mce = Sections.findAncestorOfExactType(section,
					ManchesterClassExpression.class);
			if (hasRightSon(mce, Delimiter.class, replacement)) {
				return getRightSon(mce, Delimiter.class,
						replacement);
			}
		}
		return null;
	}

	private <OT extends Type> boolean hasRightSon(Section<?> section, Class<OT> cls, String text) {
		for (Section<? extends Type> child : section.getChildren()) {
			if (child.get().isAssignableFromType(cls)) {
				if (section.getText().indexOf(text) < child.getOffSetFromFatherText()) {
					return true;
				}
			}
			else {
				if (hasRightSon(child, cls, text)) {
					return true;
				}
			}
		}
		return false;
	}

	private <OT extends Type> Section<OT> getRightSon(Section<?> section, Class<OT> cls, String text) {
		for (Section<? extends Type> child : section.getChildren()) {
			if (child.get().isAssignableFromType(cls)) {
				if (section.getText().indexOf(text) < child.getOffSetFromFatherText()) {
					return Sections.findSuccessor(child, cls);
				}
			}
			else {
				if (getRightSon(child, cls, text) != null) {
					return Sections.findSuccessor(child, cls);
				}
			}
		}
		return null;
	}
}
