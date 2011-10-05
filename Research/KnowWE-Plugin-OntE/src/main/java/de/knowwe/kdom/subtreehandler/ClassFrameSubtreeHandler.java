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
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * <p>
 * Walks over the KDOM and looks for {@link ClassFrame} sections and creates out
 * of the findings correct OWL axioms. Those nodes are then added to the
 * ontology.
 * </p>
 * <p>
 * Currently handles: {@link OWLClassDefinition}, {@link SubClassOf},
 * {@link EquivalentTo}, {@link DisJointWith}
 * </p>
 *
 * @author Stefan Mark
 * @created 24.08.2011
 */
public class ClassFrameSubtreeHandler extends OWLAPISubtreeHandler<ClassFrame> {

	/**
	 * Constructor for the {@link SubtreeHandler}. Here you can set if a sync
	 * with {@link RDF2Go} should occur. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public ClassFrameSubtreeHandler() {
		super(true);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<ClassFrame> s, Collection<KDOMReportMessage> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLClass clazz = null;
		OWLAxiom axiom = null;

		// handle class definition, then the rest
		ClassFrame type = s.get();
		if (type.hasClassDefinition(s)) {
			Section<?> def = type.getClassDefinition(s);
			clazz = (OWLClass) AxiomFactory.getOWLAPIEntity(def, OWLClass.class);
			axiom = AxiomFactory.getOWLAPIEntityDeclaration(clazz);
			if (axiom != null) {
				axioms.add(axiom);
			}
		}

		if (type.hasAnnotations(s)) { // Handle Annotations
			for (Section<Annotation> annotation : type.getAnnotations(s)) {
				axiom = AxiomFactory.createAnnotations(annotation, clazz.getIRI());
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if(type.hasSubClassOf(s)){ //Handle SubClassOf
			Section<?> desc = type.getSubClassOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);
			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createOWLSubClassOf(clazz, e);
				if(axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if(type.hasDisjointWith(s)){ //Handle DisjointWith
			Section<?> desc = type.getDisjointWith(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createOWLDisjointWith(clazz, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if (type.hasEquivalentTo(s)) { // Handle EquivalentTo
			Section<?> desc = type.getEquivalentTo(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				axiom = AxiomFactory.createOWLEquivalentTo(clazz, e);
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		if (type.hasDisjointUnionOf(s)) { // Handle DisjointunionOf
			Section<?> desc = type.getDisjointUnionOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);
			Set<OWLClassExpression> exp = AxiomFactory.createDescriptionExpression(mce);

			for (OWLClassExpression e : exp) {
				// FIXME not yet implemented
				// axiom = AxiomFactory.createOWLDisjointUnionOf(clazz, e);
				axiom = null;
				if (axiom != null) {
					axioms.add(axiom);
				}
			}
		}

		// Note: use OWLEntityCollector instead of below?
		// necessary to avoid errors through the OWLApi due not defined entities
		Set<OWLAxiom> addAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom a : axioms) {
			Set<OWLClass> classes = a.getClassesInSignature();
			for (OWLClass owlClass : classes) {
				OWLAxiom t = AxiomFactory.getOWLAPIEntityDeclaration(owlClass);
				addAxioms.add(t);
			}

			Set<OWLObjectProperty> properties = a.getObjectPropertiesInSignature();
			for (OWLObjectProperty p : properties) {
				OWLAxiom t = AxiomFactory.getOWLAPIEntityDeclaration(p);
				addAxioms.add(t);
			}
		}
		addAxioms.addAll(axioms);
		return addAxioms;
	}
}
