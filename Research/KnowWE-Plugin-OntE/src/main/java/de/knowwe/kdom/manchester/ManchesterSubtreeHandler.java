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
package de.knowwe.kdom.manchester;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.semanticweb.owlapi.model.OWLAxiom;

import de.d3web.core.session.blackboard.Facts;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.kdom.manchester.ManchesterMarkup.ManchesterMarkupContentType;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.Characteristics;
import de.knowwe.kdom.manchester.frames.objectproperty.ObjectPropertyFrame;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * @author smark
 * @created 07.09.2011
 */
public class ManchesterSubtreeHandler extends OWLAPISubtreeHandler<ManchesterMarkupContentType> {

	private Set<OWLAxiom> axioms;

	/**
	 * Constructor for the {@link SubtreeHandler}. here one can set if a sync
	 * with {@link RDF2Go} should be done. For further information see
	 * {@link OWLAPISubtreeHandler}.
	 */
	public ManchesterSubtreeHandler() {
		super(true);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<ManchesterMarkupContentType> s, Collection<KDOMReportMessage> messages) {

		axioms = new HashSet<OWLAxiom>();
		return axioms;

		// for (Section<? extends Type> child : s.getChildren()) {
		// Type t = child.get();
		//
		// if (t instanceof ClassFrame) {
		// // createClass(child);
		// }
		// else if (t instanceof IndividualFrame) {
		// Section<IndividualFrame> section = Sections.findChildOfType(s,
		// IndividualFrame.class);
		// createIndividuals(section);
		// }
		// }
		//
		// // get axioms from the singleton and return them
		// Collection<OWLAxiom> objects =
		// AxiomStorageSubtree.getInstance().getAxioms();
		// axioms.addAll(objects);
		// AxiomStorageSubtree.getInstance().clear();
	}

	/**
	 * <p>
	 * Walks over the KDOM and looks for {@link ClassFrame} sections and creates
	 * out of the findings correct OWL axioms. Those nodes are then added to the
	 * ontology.
	 * </p>
	 * <p>
	 * Currently handles: {@link OWLClassDefinition}, {@link SubClassOf},
	 * {@link EquivalentTo}, {@link DisJointWith}
	 * </p>
	 *
	 * @created 24.08.2011
	 * @param Section s A section containing a {@link ClassFrame}.
	 */
	@Deprecated
	private void createClass(Section<?> section) {

		// OWLClass clazz = null;
		//
		// for (Section<?> child : section.getChildren()) {
		// Type t = child.get();
		// // if (t instanceof OWLClassDefinition) {
		// // clazz = AxiomFactory.createClass(child);
		// // OWLAxiom e = AxiomFactory.getOWLAPIEntityDeclaration(clazz);
		// // axioms.add(e);
		// // }
		// if (t instanceof Annotations) {
		// List<Section<Annotation>> items =
		// Sections.findSuccessorsOfType(child,
		// Annotation.class);
		// for (Section<Annotation> item : items) {
		// OWLAxiom a = AxiomFactory.createAnnotations(item, clazz.getIRI());
		// if (a != null) {
		// axioms.add(a);
		// }
		// }
		// }
		// else {
		// // SubClassOf, DisJointWith, EquivalentTo
		// List<Section<ListItem>> items = Sections.findSuccessorsOfType(
		// child,
		// ListItem.class);
		// if (items.size() > 0) {
		// for (Section<ListItem> item : items) {
		// Section<ManchesterClassExpression> mce = Sections.findSuccessor(item,
		// ManchesterClassExpression.class);
		// if (mce != null) {
		// OWLClassExpression e = AxiomFactory.createDescriptionExpression(
		// mce);
		// if (t instanceof SubClassOf) {
		// axioms.add(AxiomFactory.createOWLSubClassOf(clazz, e));
		// }
		// else if (t instanceof DisjointWith) {
		// axioms.add(AxiomFactory.createOWLDisjointWith(clazz, e));
		// }
		// else if (t instanceof EquivalentTo) {
		// axioms.add(AxiomFactory.createOWLEquivalentTo(clazz, e));
		// }
		// }
		// }
		// }
		// else {
		// // ManchesterClassExpression
		// Section<ManchesterClassExpression> mce =
		// Sections.findSuccessor(child,
		// ManchesterClassExpression.class);
		// if (mce != null) {
		// OWLClassExpression e = AxiomFactory.createDescriptionExpression(
		// mce);
		// if (t instanceof SubClassOf) {
		// axioms.add(AxiomFactory.createOWLSubClassOf(clazz, e));
		// }
		// else if (t instanceof DisjointWith) {
		// axioms.add(AxiomFactory.createOWLDisjointWith(clazz, e));
		// }
		// else if (t instanceof EquivalentTo) {
		// axioms.add(AxiomFactory.createOWLEquivalentTo(clazz, e));
		// }
		// }
		// }
//			}
		// }
	}

	/**
	 * <p>
	 * Walks over the KDOM and looks for {@link ObjectPropertyExpression} and
	 * creates correct OWL axioms out of the found KDOM nodes. Those nodes are
	 * then added to the ontology.
	 * </p>
	 * <p>
	 * Currently handles: {@link ObjectPropertyDefinition},
	 * {@link Characteristics}, {@link Annotations}, {@link InverseOf},
	 * {@link SubPropertyOf}, {@link Domain} and {@link Range}
	 * </p>
	 *
	 * @created 07.09.2011
	 * @param Section s A section containing a {@link ObjectPropertyExpression}.
	 */
	@Deprecated
	private void createObjectProperties(Section<ObjectPropertyFrame> section) {

		// OWLObjectProperty p = null;
		// OWLAxiom e = null;
		//
		// // handle definition, then the rest
		// ObjectPropertyFrame frame = section.get();
		// if (frame.hasObjectPropertyDefinition(section)) {
		// Section<?> def = frame.getObjectPropertyDefinition(section);
		// p = (OWLObjectProperty) AxiomFactory.getOWLAPIEntity(def,
		// OWLObjectProperty.class);
		// e = AxiomFactory.getOWLAPIEntityDeclaration(p);
		// if (e != null) {
		// axioms.add(e);
		// }
		// }
		//
		// // done with the definition handle the rest
		// for (Section<?> child : section.getChildren()) {
		// Type t = child.get();
		//
		// if (t instanceof Characteristics) {
		// List<Section<Characteristics.CharacteristicsTerm>> l =
		// Sections.findSuccessorsOfType(child,
		// Characteristics.CharacteristicsTerm.class);
		// if (l != null) {
		// for (Section<Characteristics.CharacteristicsTerm> s : l) {
		// e = AxiomFactory.createCharacteristics(s, p);
		// if (e != null) {
		// axioms.add(e);
		// }
		// }
		// } // term missing, please remove definition or one
		// }
		// else if (t instanceof Domain) {
		// // get SingleListItems and handle each accordingly
		// Section<?> s = Sections.findSuccessor(child,
		// OWLTermReferenceManchester.class);
		// axioms.add(AxiomFactory.createDomain(s, p));
		// }
		// else if (t instanceof Range) {
		// Section<?> s = Sections.findSuccessor(child,
		// OWLTermReferenceManchester.class);
		// axioms.add(AxiomFactory.createRange(s, p));
		// }
		// else if (t instanceof InverseOf) {
		// Section<?> s = Sections.findSuccessor(child,
		// ObjectPropertyExpression.class);
		// axioms.add(AxiomFactory.createInverseOf(s, p));
		// }
		// else if (t instanceof SubPropertyOf) {
		//
		// Sections.findSuccessorsOfType(child, ObjectPropertyExpression.class);
		//
		// Section<?> s = Sections.findSuccessor(child,
		// ObjectPropertyExpression.class);
		// axioms.addAll(AxiomFactory.createSubPropertyOf(s, p));
		// }
		// else if (t instanceof Annotations) {
		// List<Section<Annotation>> items =
		// Sections.findSuccessorsOfType(child,
		// Annotation.class);
		// for (Section<Annotation> item : items) {
		// e = AxiomFactory.createAnnotations(item, p.getIRI());
		// if (e != null) {
		// axioms.add(e);
		// }
		// }
		// }
		// }
	}

	/**
	 * <p>
	 * Walk over the KDOM and looks for {@link IndividualFrame} and creates
	 * correct OWL axioms out of the found KDOM nodes. Those nodes are then
	 * added to the ontology.
	 * </p>
	 * <p>
	 * Currently handles: IndividualDefinition, {@link Annotations},
	 * {@link Types}, {@link Facts}, {@link SameAs} and {@link DifferentFrom}
	 * </p>
	 *
	 * @created 07.09.2011
	 * @param Section s A section containing a {@link IndividualFrame}.
	 */
	private void createIndividuals(Section<IndividualFrame> section) {
		// OWLIndividual i = null;
		//
		// // handle definition, then the rest
		// IndividualFrame frame = section.get();
		// if (frame.hasIndividualDefinition(section)) {
		// Section<?> def = frame.getIndividualDefinition(section);
		// i = (OWLIndividual) AxiomFactory.getOWLAPIEntity(def,
		// OWLIndividual.class);
		// }
		//
		// // handle the rest
		// List<Section<?>> children = section.getChildren();
		// for (Section<?> child : children) {
		// Type t = child.get();
		// if (t instanceof Facts) { // FACTS
		// Section<Facts> facts = Sections.findSuccessor(section, Facts.class);
		// List<Section<?>> factItems = ((Facts) t).getFacts(facts);
		// for (Section<?> item : factItems) {
		// OWLAxiom a = null;
		// a = AxiomFactory.createFact(item, i);
		// if (a != null) {
		// axioms.add(a);
		// }
		// }
		// }
		// else if (t instanceof DifferentFrom) {
		//
		// }
		// else {
		// // Type|SameAs -> List of one or more objects, handle directly
		// List<Section<ManchesterClassExpression>> items =
		// Sections.findSuccessorsOfType(
		// child,
		// ManchesterClassExpression.class);
		//
		// // FIXME list
		// for (Section<ManchesterClassExpression> mce : items) {
		// OWLClassExpression o = AxiomFactory.createDescriptionExpression(
		// mce);
		// OWLAxiom a = null;
		// if (t instanceof Types) {
		// a = AxiomFactory.createNamedIndividualAxiom(o, i);
		// }
		// else if (t instanceof SameAs) {
		// a = AxiomFactory.createSameIndividualsAxiom(o, i);
		// }
		// if (a != null) {
		// axioms.add(a);
		// }
		// }
		// }
		// }
	}

	/**
	 * <p>
	 * Walk over the KDOM and looks for {@link MiscFrame} and creates correct
	 * OWL axioms out of the found KDOM nodes. Those nodes are then added to the
	 * ontology.
	 * </p>
	 * <p>
	 * Currently handles: DisjointClasses, EquivalentClasses, SameIndividual,
	 * DifferentIndividuals, EquivalentProperties, DisjointProperties.
	 * </p>
	 *
	 * @created 07.09.2011
	 * @param Section<MiscFrame> s A section containing a {@link MiscFrame}.
	 */
	@Deprecated
	private void createMiscFrame(Section<MiscFrame> section) {

		// MiscFrame type = (MiscFrame) section.get();
		//
		// if (type.isDifferentIndividuals(section) ||
		// type.isSameIndividuals(section)) {
		// axioms.add(AxiomFactory.createMiscFrameIndividuals(section));
		//
		// }
		// else if (type.isDisjointClasses(section) ||
		// type.isEquivalentClasses(section)) {
		// axioms.add(AxiomFactory.createMiscFrameClasses(section));
		// }
		// else if (type.isDisjointProperties(section) ||
		// type.isEquivalentProperties(section)) {
		// axioms.add(AxiomFactory.createMiscFrameObjectProperties(section));
		// }
		// else if (type.isDisjointDataProperties(section) ||
		// type.isEquivalentDataProperties(section)) {
		// axioms.add(AxiomFactory.createMiscFrameDataProperties(section));
		// }
	}
}