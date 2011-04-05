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
package de.d3web.owl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.semanticweb.owlapi.model.IRI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.owl.assignment.Assignment;
import de.d3web.owl.assignment.AssignmentSet;
import de.d3web.owl.assignment.ChoiceValueAssignment;
import de.d3web.owl.assignment.RatingAssignment;
import de.d3web.owl.assignment.YesNoAssignment;

/**
 * Persistence handler for @link{Assignment}s. It is designed for easy
 * extension. If a new type of @link{Assignment} is created, the only thing that
 * needs to be done is adding a new "else if" clause.
 *
 * @author Sebastian Furth
 * @created Apr 5, 2011
 */
public class AssignmentPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	private final String ASSIGNMENTS = "Assignments";
	private final String ASSIGNMENT = "Assignment";
	private final String OWLCLASS = "owlClass";
	private final String TARGET = "target";
	private final String TYPE = "type";
	private final String RATING = "rating";

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		// Create document and root element
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement(ASSIGNMENTS);
		doc.appendChild(root);

		// Necessary for progess listener
		int cur = 0;
		int max = getEstimatedSize(kb);

		// Get AssignmentSet from knoweldge base
		AssignmentSet assignments = kb.getKnowledgeStore().getKnowledge(
				AssignmentSet.KNOWLEDGE_KIND);

		// Write all assignments
		for (Assignment assignment : assignments.getAssignments()) {
			root.appendChild(getAssignmentElement(assignment, doc));
			listener.updateProgress(++cur / max, "Saving knowledge base: Assignments");
		}

		// Save the created document
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private Node getAssignmentElement(Assignment assignment, Document doc) {
		Element assignmentElement = doc.createElement(ASSIGNMENT);
		assignmentElement.setAttribute(OWLCLASS, assignment.getComplexOWLClass().toString());
		if (assignment instanceof ChoiceValueAssignment) {
			ChoiceValueAssignment cva = (ChoiceValueAssignment) assignment;
			assignmentElement.setAttribute(TARGET, cva.getTarget().getName());
			assignmentElement.setAttribute(TYPE, cva.getClass().getSimpleName());
		}
		else if (assignment instanceof RatingAssignment) {
			RatingAssignment ra = (RatingAssignment) assignment;
			assignmentElement.setAttribute(RATING, ra.getRating().getState().name());
			assignmentElement.setAttribute(TYPE, ra.getClass().getSimpleName());
		}
		else if (assignment instanceof YesNoAssignment) {
			YesNoAssignment yna = (YesNoAssignment) assignment;
			assignmentElement.setAttribute(TARGET, yna.getTarget().getName());
			assignmentElement.setAttribute(TYPE, yna.getClass().getSimpleName());
		}
		else {
			// We should never get to this point ;-)
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR, "Unknown assignment type: "
					+ assignment.getClass());
		}
		return assignmentElement;
	}

	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		return knowledgeBase.getKnowledgeStore().getKnowledge(AssignmentSet.KNOWLEDGE_KIND).getAssignments().size();
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		// create object for xml document
		Document doc = Util.streamToDocument(stream);
		listener.updateProgress(0, "Loading knowledge base: assignments");

		// get all assignment elements
		NodeList assignments = doc.getElementsByTagName(ASSIGNMENT);

		// necessary for the progress listener
		int cur = 0;
		int max = assignments.getLength();

		// Create new AssignmentSet
		AssignmentSet assignmentSet = new AssignmentSet();
		kb.getKnowledgeStore().addKnowledge(AssignmentSet.KNOWLEDGE_KIND, assignmentSet);
		for (int i = 0; i < assignments.getLength(); i++) {
			Node current = assignments.item(i);
			addAssignment(assignmentSet, kb, current);
			listener.updateProgress(++cur / max, "Loading knowledge base: Assignments");
		}
	}

	private void addAssignment(AssignmentSet assignmentSet, KnowledgeBase kb, Node assignmentNode) {
		NamedNodeMap attributes = assignmentNode.getAttributes();
		IRI owlClass = IRI.create(attributes.getNamedItem(OWLCLASS).getTextContent());
		String type = attributes.getNamedItem(TYPE).getTextContent();
		if (type.equalsIgnoreCase(YesNoAssignment.class.getSimpleName())) {
			String target = attributes.getNamedItem(TARGET).getTextContent();
			QuestionYN question = (QuestionYN) kb.getManager().searchQuestion(target);
			assignmentSet.addAssignment(new YesNoAssignment(owlClass, question));
		}
		else if (type.equalsIgnoreCase(RatingAssignment.class.getSimpleName())) {
			String ratingText = attributes.getNamedItem(RATING).getTextContent();
			Rating rating = getRating(ratingText);
			assignmentSet.addAssignment(new RatingAssignment(owlClass, rating));
		}
		else if (type.equalsIgnoreCase(ChoiceValueAssignment.class.getSimpleName())) {
			String target = attributes.getNamedItem(TARGET).getTextContent();
			QuestionChoice question = (QuestionChoice) kb.getManager().searchQuestion(target);
			assignmentSet.addAssignment(new ChoiceValueAssignment(owlClass, question));
		}
		else {
			// We should never get to this point ;-)
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR, "Unknown assignment type: "
					+ type);
		}
	}

	private Rating getRating(String ratingText) {
		for (Rating.State state : Rating.State.values()) {
			if (ratingText.equalsIgnoreCase(state.name())) {
				return new Rating(state);
			}
		}
		return null;
	}

}
