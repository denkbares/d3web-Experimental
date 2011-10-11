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
import java.util.LinkedList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.OntologyProvider;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.AssignmentSet;
import de.d3web.owl.assignment.Quantifier;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.MessageUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SimpleMessageError;
import de.knowwe.core.report.SyntaxError;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.report.message.NoSuchObjectError;

/**
 * Compiles parsed AssigmentTypes to d3web @link{Assignment}s.
 * 
 * @author Sebastian Furth
 * @created Mar 31, 2011
 */
public abstract class AssignmentCompiler<T extends AssignmentType> extends D3webSubtreeHandler<T> {

	private final String STOREKEY = "Assignment-Store-Key";

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<T> s) {
		// Load the underlying knowledge base
		KnowledgeBase kb = getKB(article);
		if (kb == null) {
			return MessageUtils.asList(new SimpleMessageError(
					"Unable to load knowledge base from article: " + article.getTitle()));
		}
		// Container for the messages
		Collection<KDOMReportMessage> messages = new LinkedList<KDOMReportMessage>();
		// Load an instance of the underlying ontology
		OWLOntology ontology = loadOntology(kb);
		if (ontology == null) {
			return MessageUtils.asList(new SimpleMessageError(
					"Unable to load ontology from knowledge base: " + kb.getName()));
		}
		// Create OWLOntologyUtil instance
		OWLOntologyUtil util = new OWLOntologyUtil(ontology);
		// Get the desired base URI
		String baseURI = getBaseURI(s, kb, ontology);
		// Create new AssignmentSet
		AssignmentSet assignments = getAssignmentSet(kb);
		// Create Assignment
		Assignment assignment = createAssignment(article, s, kb, util, baseURI, messages);
		if (assignment != null) {
			assignments.addAssignment(assignment);
			KnowWEUtils.storeObject(article, s, STOREKEY, assignment);
			// TODO: RemoveMe
			System.out.println("Assignments: " + assignments.getAssignments().size());
		}
		// Return the created messages
		return messages;
	}

	@Override
	public void destroy(KnowWEArticle article, Section<T> s) {
		Assignment assignment =
				(Assignment) s.getSectionStore().getObject(article, STOREKEY);
		if (assignment != null) {
			KnowledgeBase kb = getKB(article);
			if (kb != null) {
				AssignmentSet assignments = getAssignmentSet(kb);
				assignments.removeAssignment(assignment);
				// TODO: RemoveMe
				System.out.println("Assignments: " + assignments.getAssignments().size());
			}
		}
	}

	protected abstract Assignment createAssignment(KnowWEArticle article, Section<T> s, KnowledgeBase kb, OWLOntologyUtil util, String baseURI, Collection<KDOMReportMessage> messages);

	protected IRI getOWLClassIRI(Section<T> assignment, String baseURI, OWLOntologyUtil util, Collection<KDOMReportMessage> messages) {
		Section<ComplexOWLClassType> owlClass = Sections.findSuccessor(assignment,
				ComplexOWLClassType.class);
		IRI iri = IRI.create(baseURI + "#" + owlClass.getText());
		// Check existence of OWLClass in the ontology
		if (util.getOWLClassFor(iri) == null) {
			messages.add(new NoSuchObjectError("There is no OWLClass with IRI: " + iri));
			return null;
		}
		return iri;
	}

	protected boolean checkQuantifier(Section<T> assignment, Quantifier quantifier, Collection<KDOMReportMessage> messages) {
		Section<QuantifierType> quantifierSection = Sections.findSuccessor(assignment,
				QuantifierType.class);
		boolean exists = quantifierSection.getText().equalsIgnoreCase(quantifier.getSymbol());
		if (!exists) {
			messages.add(new SyntaxError("Wrong quantifier, expected: " + quantifier.getSymbol()));
		}
		return exists;
	}

	private AssignmentSet getAssignmentSet(KnowledgeBase kb) {
		AssignmentSet assignments = kb.getKnowledgeStore().getKnowledge(
				AssignmentSet.KNOWLEDGE_KIND);
		if (assignments == null) {
			assignments = new AssignmentSet();
			kb.getKnowledgeStore().addKnowledge(AssignmentSet.KNOWLEDGE_KIND, assignments);
		}
		return assignments;
	}

	private OWLOntology loadOntology(KnowledgeBase kb) {
		OntologyProvider provider = kb.getKnowledgeStore().getKnowledge(
				OntologyProvider.KNOWLEDGE_KIND);
		return provider != null ? provider.createOntologyInstance() : null;
	}

	private String getBaseURI(Section<T> section, KnowledgeBase kb, OWLOntology ontology) {
		Section<AssignmentMarkup> assignment = Sections.findAncestorOfType(section,
				AssignmentMarkup.class);
		String baseURI = assignment != null
				? DefaultMarkupType.getAnnotation(assignment, AssignmentMarkup.BASEURI)
				: null;
		// if no base URI is defined, we take the base URI of the ontology
		if (baseURI == null) {
			baseURI = ontology.getOntologyID().getOntologyIRI().toString();
		}
		return baseURI;
	}

}
