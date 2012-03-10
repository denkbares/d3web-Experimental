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
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.Quantifier;
import de.d3web.owl.assignment.RatingAssignment;
import de.d3web.we.kdom.auxiliary.Equals;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

/**
 * 
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class RatingAssignmentType extends AssignmentType {

	private final String REGEX = "\\s*" + ALLCLASS + "\\s*=\\s*" + RATING;

	public RatingAssignmentType() {
		// SectionFinder
		this.sectionFinder = new RegexSectionFinder(REGEX, Pattern.CASE_INSENSITIVE, 0);
		/* ChildrenTypes */
		this.addChildType(new ComplexOWLClassType());
		this.addChildType(new QuantifierType());
		this.addChildType(new Equals());
		this.addChildType(new RatingType());
		// SubtreeHandler. Priority.LOWER is important!
		this.addSubtreeHandler(Priority.LOWER, new RatingAssignmentCompiler());
	}

	private class RatingAssignmentCompiler extends AssignmentCompiler<RatingAssignmentType> {

		@Override
		protected Assignment createAssignment(Article article, Section<RatingAssignmentType> s, KnowledgeBase kb, OWLOntologyUtil util, String baseURI, Collection<Message> messages) {
			/* Get the rating */
			Rating rating = getRating(article, s, messages);
			if (rating == null) {
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
			return new RatingAssignment(owlClassIRI, rating);
		}

		private Rating getRating(Article article, Section<RatingAssignmentType> s, Collection<Message> messages) {
			// Get Rating section
			Section<RatingType> ratingSection = Sections.findSuccessor(s, RatingType.class);
			// Get textual representation of the desired state
			String ratingText = ratingSection != null ? ratingSection.getText() : "";
			/* Try to find a corresponding d3web Rating.State */
			for (Rating.State state : Rating.State.values()) {
				if (ratingText.equalsIgnoreCase(state.name())) {
					return new Rating(state);
				}
			}
			messages.add(Messages.noSuchObjectError("Can't get a Rating for state: " + ratingText));
			return null;
		}
	}

}
