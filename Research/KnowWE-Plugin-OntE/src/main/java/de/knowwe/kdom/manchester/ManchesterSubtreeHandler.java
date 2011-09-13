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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.knowwe.kdom.manchester.ManchesterMarkup.ManchesterMarkupContentType;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.frames.clazz.DisjointWith;
import de.knowwe.kdom.manchester.frames.clazz.EquivalentTo;
import de.knowwe.kdom.manchester.frames.clazz.SubClassOf;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame.OWLClassDefinition;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame.IndividualContentType;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame.IndividualDefinition;
import de.knowwe.kdom.manchester.frames.objectproperty.Characteristics;
import de.knowwe.kdom.manchester.frames.objectproperty.Domain;
import de.knowwe.kdom.manchester.frames.objectproperty.InverseOf;
import de.knowwe.kdom.manchester.frames.objectproperty.ObjectPropertyFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.Range;
import de.knowwe.kdom.manchester.frames.objectproperty.SubPropertyOf;
import de.knowwe.kdom.manchester.frames.objectproperty.ObjectPropertyFrame.ObjectPropertyDefinition;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.kdom.manchester.types.Restriction;
import de.knowwe.kdom.manchester.types.CommaSeparatedList.ListItem;
import de.knowwe.owlapi.OWLAPISubtreeHandler;

/**
 * @author smark
 * @created 07.09.2011
 */
public class ManchesterSubtreeHandler extends OWLAPISubtreeHandler<ManchesterMarkupContentType> {

	private Set<OWLAxiom> axioms;

	@Override
	public Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<ManchesterMarkupContentType> s, Collection<KDOMReportMessage> messages) {

		axioms = new HashSet<OWLAxiom>();

		for (Section<?> child : s.getChildren()) {
			Type t = child.get();

			if (t instanceof ClassFrame) {
				createClass(child);
			}
			else if (t instanceof ObjectPropertyFrame) {
				createObjectProperties(child);
			}
			else if (t instanceof IndividualFrame) {
				Section<IndividualContentType> section =
						Sections.findChildOfType(child,
								IndividualContentType.class);
				if (section != null) {
					// createIndividuals(section);
				}
			}
		}
		return axioms;
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
	private void createClass(Section<?> section) {

		OWLClass clazz = null;

		for (Section<?> child : section.getChildren()) {
			Type t = child.get();
			if (t instanceof OWLClassDefinition) {
				clazz = AxiomFactory.createClass(child);
				OWLAxiom e = AxiomFactory.createOWLClass(clazz);
				axioms.add(e);
			}
			else if (t instanceof Annotations) {
				List<Section<ListItem>> items = Sections.findSuccessorsOfType(child,
						ListItem.class);
				for (Section<ListItem> item : items) {
					axioms.add(AxiomFactory.createAnnotations(item, clazz.getIRI()));
				}
			}
			else {
				List<Section<ListItem>> items = Sections.findSuccessorsOfType(
						child,
						ListItem.class);
				for (Section<ListItem> item : items) {
					List<Section<Restriction>> restriction = new ArrayList<Section<Restriction>>();
					Sections.findSuccessorsOfType(item, Restriction.class, 3, restriction);

					if (restriction.size() > 0) {
						OWLAxiom e = null;
						if (t instanceof SubClassOf) {
							e = AxiomFactory.createSubClassOf(restriction.get(0), clazz);
						}
						if (t instanceof EquivalentTo) {
							// e = AxiomFactory.createEquivalentTo(child2,
							// clazz);
						}
						if (t instanceof DisjointWith) {
							// e =
							// AxiomFactory.createDisjointWith(restriction.get(0),
							// clazz);
						}
						if (e != null) {
							axioms.add(e);
						}
					}
				}
			}
		}
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
	private void createObjectProperties(Section<?> section) {

		OWLObjectProperty p = null;
		OWLAxiom e = null;

		for (Section<?> child : section.getChildren()) {
			Type t = child.get();

			if (t instanceof ObjectPropertyDefinition) {
				p = AxiomFactory.createProperty(child);
				e = AxiomFactory.createPropertyDefinition(p);
				if (e != null) {
					axioms.add(e);
				}
			}
			else if (t instanceof Characteristics) {
				List<Section<Characteristics.CharacteristicsTerm>> l = Sections.findSuccessorsOfType(child,
						Characteristics.CharacteristicsTerm.class);
				if (l != null) {
					for (Section<Characteristics.CharacteristicsTerm> s : l) {
						e = AxiomFactory.createCharacteristics(s, p);
						if (e != null) {
							axioms.add(e);
						}
					}
				}
			}
			else if (t instanceof Domain) {
				Section<?> s = Sections.findSuccessor(child, OWLTermReferenceManchester.class);
				axioms.add(AxiomFactory.createDomain(s, p));
			}
			else if (t instanceof Range) {
				Section<?> s = Sections.findSuccessor(child, OWLTermReferenceManchester.class);
				axioms.add(AxiomFactory.createRange(s, p));
			}
			else if (t instanceof InverseOf) {
				Section<?> s = Sections.findSuccessor(child, ObjectPropertyExpression.class);
				axioms.add(AxiomFactory.createInverseOf(s, p));
			}
			else if (t instanceof SubPropertyOf) {
				Section<?> s = Sections.findSuccessor(child, ObjectPropertyExpression.class);
				axioms.addAll(AxiomFactory.createSubPropertyOf(s, p));
			}
			else if (t instanceof Annotations) {
				List<Section<ListItem>> items = Sections.findSuccessorsOfType(section,
						ListItem.class);
				for (Section<ListItem> item : items) {
					axioms.add(AxiomFactory.createAnnotations(item, p.getIRI()));
				}
			}
		}
	}

	/**
	 * <p>
	 * Walk over the KDOM and looks for {@link ObjectPropertyExpression} and
	 * creates correct OWL axioms out of the found KDOM nodes. Those nodes are
	 * then added to the ontology. Handle the s accordingly.
	 * </p>
	 * <p>
	 * Currently handles: {@link ObjectPropertyDefinition}, {link
	 * Characteristics}, {@link Annotations}, {@link InverseOf},
	 * {@link SubPropertyOf}, {@link Domain} and {@link Range}
	 * </p>
	 *
	 * @created 07.09.2011
	 * @param Section s A section containing a {@link ObjectPropertyExpression}.
	 */
	private void createIndividuals(Section<?> section) {
		OWLIndividual i = null;
		OWLAxiom e = null;

		for (Section<?> child : section.getChildren()) {
			Type t = child.get();
			if (t instanceof IndividualDefinition) {
				i = AxiomFactory.createIndividual(child);
				// e = AxiomFactory.createIndividualAssertion(i);
				axioms.add(e);
			}
		}
	}
}