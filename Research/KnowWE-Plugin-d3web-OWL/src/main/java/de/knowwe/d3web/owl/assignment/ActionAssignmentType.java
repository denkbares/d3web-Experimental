/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.owl.assignment;

import java.util.Collection;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.IRI;

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.assignment.ActionAssignment;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.Quantifier;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.rules.action.D3webRuleAction;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.StringSectionFinder;
import de.d3web.we.kdom.type.AnonymousType;

/**
 *
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class ActionAssignmentType extends AssignmentType {

	private final String REGEX = "\\s*" + EXISTSCLASS + "\\s*" + RIGHTARROW + "\\s*.+";

	public ActionAssignmentType() {
		// SectionFinder
		this.sectionFinder = new RegexSectionFinder(REGEX, Pattern.CASE_INSENSITIVE, 0);
		/* ChildrenTypes */
		this.addChildType(new ComplexOWLClassType());
		this.addChildType(new QuantifierType());
		RuleAction action = new RuleAction();
		action.setSectionFinder(new RegexSectionFinder(RIGHTARROW + "\\s*(.+)",
				Pattern.CASE_INSENSITIVE, 1));
		this.addChildType(action);
		AnonymousType rightArrow = new AnonymousType("RightArrow");
		rightArrow.setSectionFinder(new StringSectionFinder(RIGHTARROW));
		this.addChildType(rightArrow);
		// SubtreeHandler. Priority.LOWER is important!
		this.addSubtreeHandler(Priority.LOWER, new ActionAssignmentCompiler());
	}

	private class ActionAssignmentCompiler extends AssignmentCompiler<ActionAssignmentType> {

		@Override
		protected Assignment createAssignment(KnowWEArticle article, Section<ActionAssignmentType> s, KnowledgeBase kb, OWLOntologyUtil util, String baseURI, Collection<KDOMReportMessage> messages) {
			/* Get the PSAction */
			PSAction psAction = getPSAction(s, article);
			if (psAction == null) {
				messages.add(new NoSuchObjectError("Unable to load action."));
				return null;
			}
			/* Check the Quantifier */
			if (!checkQuantifier(s, Quantifier.EXISTENTIAL, messages)) {
				return null;
			}
			/* Get and Check IRI of the OWLClass */
			IRI owlClassIRI = getOWLClassIRI(s, baseURI, util, messages);
			if (owlClassIRI == null) {
				return null;
			}
			/* Create the assignment */
			return new ActionAssignment(owlClassIRI, psAction);
		}

		@SuppressWarnings("unchecked")
		private PSAction getPSAction(Section<ActionAssignmentType> actionAssignment, KnowWEArticle article) {
			// Get RuleAction section
			Section<RuleAction> action = Sections.findSuccessor(actionAssignment, RuleAction.class);
			/* Try to extract the PSAction */
			PSAction psAction = null;
			if (action != null) {
				@SuppressWarnings("rawtypes")
				Section<? extends D3webRuleAction> actionType =
						Sections.findChildOfType(action, D3webRuleAction.class);
				psAction = actionType != null
						? actionType.get().getAction(article, actionType)
						: null;
			}
			return psAction;
		}
	}
}
