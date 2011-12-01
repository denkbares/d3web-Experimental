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
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.knowwe.core.event.EventManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.AxiomFactory;
import de.knowwe.kdom.manchester.ManchesterClassExpression;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.EquivalentTo;
import de.knowwe.onte.editor.OWLApiAxiomCacheUpdateEvent;
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
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<ClassFrame> s, Collection<Message> messages) {

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
				EventManager.getInstance().fireEvent(new OWLApiAxiomCacheUpdateEvent(axiom, s));
				axioms.add(axiom);
			}
		}

		if (type.hasAnnotations(s)) { // Handle Annotations
			List<Section<Annotation>> annotations = type.getAnnotations(s);
			if (!annotations.isEmpty()) {
				for (Section<Annotation> annotation : annotations) {
					axiom = AxiomFactory.createAnnotations(annotation, clazz.getIRI(), messages);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, annotation));
						axioms.add(axiom);
					}
				}
			}
			else {
				messages.add(Messages.syntaxError(
						"Annotations missing! Please specify atleast one annotation or delete the keyword."));
			}
		}

		if (type.hasSubClassOf(s)) { // Handle SubClassOf
			Section<?> desc = type.getSubClassOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("SubClassOf is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);
				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLSubClassOf(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, mce));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasDisjointWith(s)) { // Handle DisjointWith
			Section<?> desc = type.getDisjointWith(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisJointWith is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLDisjointWith(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasEquivalentTo(s)) { // Handle EquivalentTo
			Section<?> desc = type.getEquivalentTo(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("EquivalentTo is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					axiom = AxiomFactory.createOWLEquivalentTo(clazz, e);
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
				}
			}
		}

		if (type.hasDisjointUnionOf(s)) { // Handle DisjointunionOf
			Section<?> desc = type.getDisjointUnionOf(s);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(desc,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("DisjointUnionOf is empty!"));
			}
			else {
				Map<OWLClassExpression, Section<? extends Type>> exp = AxiomFactory.createDescriptionExpression(
						mce, messages);

				for (OWLClassExpression e : exp.keySet()) {
					// FIXME not yet implemented
					// axiom = AxiomFactory.createOWLDisjointUnionOf(clazz, e);
					axiom = null;
					if (axiom != null) {
						EventManager.getInstance().fireEvent(
								new OWLApiAxiomCacheUpdateEvent(axiom, exp.get(e)));
						axioms.add(axiom);
					}
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
