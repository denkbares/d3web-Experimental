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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.ChoiceValueAssignment;
import de.d3web.owl.assignment.Quantifier;
import de.d3web.we.kdom.auxiliary.Equals;
import de.d3web.we.object.QuestionReference;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.sectionFinder.AllBeforeTypeSectionFinder;

/**
 * 
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class ChoiceValueAssignmentType extends AssignmentType {

	private final String REGEX = "\\s*.+\\s*=\\s*" + ALLCLASS;

	public ChoiceValueAssignmentType() {
		// SectionFinder
		this.sectionFinder = new RegexSectionFinder(REGEX, Pattern.CASE_INSENSITIVE, 0);
		/* ChildrenTypes */
		this.addChildType(new ComplexOWLClassType());
		this.addChildType(new QuantifierType());
		Equals equals = new Equals();
		this.addChildType(equals);
		QuestionReference question = new QuestionReference();
		question.setSectionFinder(new AllBeforeTypeSectionFinder(equals));
		this.addChildType(question);
		// SubtreeHandler. Priority.LOWER is important!
		this.addSubtreeHandler(Priority.LOWER, new ChoiceValueAssignmentCompiler());
	}

	private class ChoiceValueAssignmentCompiler extends AssignmentCompiler<ChoiceValueAssignmentType> {

		@Override
		protected Assignment createAssignment(KnowWEArticle article, Section<ChoiceValueAssignmentType> s, KnowledgeBase kb, OWLOntologyUtil util, String baseURI, Collection<Message> messages) {
			/* Get the question */
			QuestionChoice question = getQuestion(article, s, messages);
			if (question == null) {
				return null;
			}
			/* Check the Quantifier */
			if (!checkQuantifier(s, Quantifier.UNIVERSAL, messages)) {
				return null;
			}
			/* Get and Check IRI of the OWLClass */
			IRI owlClassIRI = getOWLClassIRI(s, baseURI, util, messages);
			if (owlClassIRI == null) {
				return null;
			}
			// Create the assignment
			return new ChoiceValueAssignment(owlClassIRI, question);
		}

		private QuestionChoice getQuestion(KnowWEArticle article, Section<ChoiceValueAssignmentType> s, Collection<Message> messages) {
			// Get Question section
			Section<QuestionReference> questionSection = Sections.findSuccessor(s,
					QuestionReference.class);
			// Get Question object
			Question question = questionSection.get().getTermObject(article, questionSection);
			// Check QuestionChoice
			if (!(question instanceof QuestionChoice)) {
				messages.add(Messages.syntaxError("There is no QuestionYN: "
						+ questionSection.getText()));
				return null;
			}
			return (QuestionChoice) question;

		}
	}
}
