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
package de.knowwe.kdom.subtreehandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import de.d3web.core.session.blackboard.Facts;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.frame.IndividualFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * <p>
 * Walk over the KDOM and looks for {@link IndividualFrame} and creates correct
 * OWL axioms out of the found KDOM nodes. Those nodes are then added to the
 * ontology.
 * </p>
 * <p>
 * Currently handles: IndividualDefinition, {@link Annotations}, {@link Types},
 * {@link Facts}, {@link SameAs} and {@link DifferentFrom}
 * </p>
 *
 * @author Stefan Mark
 * @created 07.09.2011
 */
public class IndividualFrameSubtreeHandler extends OWLAPISubtreeHandler<IndividualFrame> {

	/**
	 * Constructor for the SubtreeHandler. Here you can set if a sync
	 * with RDF2Go should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public IndividualFrameSubtreeHandler() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<IndividualFrame> s, Collection<KDOMReportMessage> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLIndividual i = null;
		OWLAxiom axiom = null;

		// handle definition, then the rest
		IndividualFrame type = s.get();
		if (type.hasIndividualDefinition(s)) {
			Section<?> def = type.getIndividualDefinition(s);
			i = (OWLIndividual) AxiomFactory.getOWLAPIEntity(def, OWLIndividual.class);
		}

		if (type.hasAnnotations(s)) { // Handle Annotations
			for (Section<Annotation> annotation : type.getAnnotations(s)) {
				IRI annotatetObject = i.asOWLNamedIndividual().getIRI();
				axiom = AxiomFactory.createAnnotations(annotation, annotatetObject);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if (type.hasFacts(s)) { // Handle Facts
			List<Section<?>> facts = type.getFacts(s);
			for (Section<?> fact : facts) {
				axiom = AxiomFactory.createFact(fact, i);
				if (axiom != null) {
					axioms.add(axiom);
				}
				// handleOptionalAnnotations(fact, i); // Optional annotations
			}
		}

		if (type.hasSameAs(s)) { // Handle SameAs
			List<Section<OWLTermReferenceManchester>> nodes = type.getSameAs(s);
			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual sameInd = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				axioms.add(AxiomFactory.createSameIndividualsAxiom(i, sameInd));

				// handleOptionalAnnotations(node, i); // Optional annotations
			}
		}

		if (type.hasDifferentFrom(s)) { // Handle DifferentFrom
			List<Section<OWLTermReferenceManchester>> nodes = type.getDifferentFrom(s);
			for (Section<OWLTermReferenceManchester> node : nodes) {
				OWLIndividual two = (OWLIndividual) AxiomFactory.getOWLAPIEntity(node,
						OWLIndividual.class);
				axioms.add(AxiomFactory.createDifferentFromIndividualsAxiom(i, two));

				// handleOptionalAnnotations(node, i); // Optional annotations
			}
		}

		if (type.hasTypes(s)) { // Handle Types
			Section<?> types = type.getTypes(s);

			Section<ManchesterClassExpression> mce = Sections.findChildOfType(types,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createNamedIndividualAxiom(e, i);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
			// FIXME handleOptionalAnnotations(types, i); optional Annotations
		}
		return axioms;
	}

	/**
	 * Handles the optional {@link Annotations} inside each description.
	 *
	 * @created 29.09.2011
	 * @param Section<? extends Type> section
	 * @return A Set with {@link OWLAnnotationAxiom}
	 */
	private Set<OWLAxiom> handleOptionalAnnotations(Section<? extends Type> section, OWLIndividual i) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		if (ManchesterSyntaxUtil.hasAnnotations(section)) {
			List<Section<Annotation>> annotations = ManchesterSyntaxUtil.getAnnotations(section);
			IRI annotatetObject = i.asOWLNamedIndividual().getIRI();
			for (Section<Annotation> annotation : annotations) {
				OWLAxiom a = AxiomFactory.createAnnotations(annotation, annotatetObject);
				if (a != null) {
					axioms.add(a);
				}
			}
		}
		return axioms;
	}
}
