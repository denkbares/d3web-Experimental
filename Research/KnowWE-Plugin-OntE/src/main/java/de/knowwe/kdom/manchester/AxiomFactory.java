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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.d3web.we.kdom.condition.CompositeCondition.Conjunct;
import de.d3web.we.kdom.condition.CompositeCondition.Disjunct;
import de.d3web.we.kdom.condition.CompositeCondition.NegatedExpression;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.frames.clazz.DisjointWith;
import de.knowwe.kdom.manchester.frames.clazz.EquivalentTo;
import de.knowwe.kdom.manchester.frames.clazz.SubClassOf;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.CharacteristicTypes;
import de.knowwe.kdom.manchester.types.ExactlyRestriction;
import de.knowwe.kdom.manchester.types.MaxRestriction;
import de.knowwe.kdom.manchester.types.MinRestriction;
import de.knowwe.kdom.manchester.types.NonNegativeInteger;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.kdom.manchester.types.OnlyRestriction;
import de.knowwe.kdom.manchester.types.Restriction;
import de.knowwe.kdom.manchester.types.SomeRestriction;
import de.knowwe.kdom.manchester.types.ValueRestriction;
import de.knowwe.kdom.manchester.types.Annotations.AnnotationDatatypeTag;
import de.knowwe.kdom.manchester.types.Annotations.AnnotationLanguageTag;
import de.knowwe.kdom.manchester.types.Annotations.AnnotationTerm;
import de.knowwe.kdom.manchester.types.Annotations.RDFSComment;
import de.knowwe.kdom.manchester.types.Annotations.RDFSLabel;
import de.knowwe.kdom.manchester.types.CommaSeparatedList.ListItem;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 *
 *
 * @author smark
 * @created 07.09.2011
 */
public class AxiomFactory {

	private static final OWLAPIConnector connector;
	private static final PrefixManager pm;
	private static final OWLDataFactory factory;
	private static final OWLOntology ontology;
	private static final AxiomStorageSubtree storage;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		pm = new DefaultPrefixManager(Rdf2GoCore.basens);
		ontology = connector.getOntology();
		storage = AxiomStorageSubtree.getInstance();
	}

	/**
	 * Create a {@link OWLClass} from a KDOM OWLClass node.
	 *
	 * @created 14.09.2011
	 * @param section
	 * @return
	 */
	public static OWLClass createClass(Section<?> section) {
		Section<?> s = Sections.findSuccessor(section, ClassFrame.OWLClass.class);
		return factory.getOWLClass(":" + s.getOriginalText(), pm);
	}

	/**
	 * Create a {@link OWLClass} declaration axiom.
	 *
	 * @created 14.09.2011
	 * @param clazz
	 * @return
	 */
	public static OWLAxiom createOWLClass(OWLClass clazz) {
		return factory.getOWLDeclarationAxiom(clazz);
	}

	/**
	 * Create a {@link OWLSubClassOf} declaration axiom.
	 *
	 * @created 14.09.2011
	 * @param clazz
	 * @return
	 */
	public static OWLAxiom createOWLSubClassOf(OWLClass father, OWLClassExpression expression) {
		return factory.getOWLSubClassOfAxiom(father, expression);
	}

	public static OWLAxiom createOWLEquivalentTo(OWLClass clazz, OWLClassExpression expression) {
		return factory.getOWLEquivalentClassesAxiom(clazz, expression);
	}

	public static OWLAxiom createOWLDisjointWith(OWLClass clazz, OWLClassExpression expression) {
		return factory.getOWLDisjointClassesAxiom(clazz, expression);
	}
	/**
	 * Creates simple {@link Restriction} {@link OWLObject} that can then
	 * further handled in {@link SubClassOf}, {@link EquivalentTo}, etc.
	 * relations.
	 *
	 * @created 14.09.2011
	 * @param Section section A child of a {@link Restriction} section
	 * @return A list containing {@link OWLObject}s expressing the
	 *         {@link Restriction}
	 */
	public static OWLClassExpression createSimpleRestriction(Section<?> section, OWLClassExpression expression) {

		Section<?> restrictionType = section.getChildren().get(0); // use before
																	// methode
																	// entry
		if (restrictionType.get() instanceof Restriction) {
			// OWLTermReference found
			String term = Sections.findSuccessor(restrictionType, OWLTermReferenceManchester.class).getOriginalText();
			OWLClass clazz = factory.getOWLClass(":" + term, pm);
			storage.addAxiom(factory.getOWLDeclarationAxiom(clazz));
			return clazz;
		}

		Section<?> ope = Sections.findSuccessor(restrictionType, ObjectPropertyExpression.class);
		Section<?> mce = Sections.findSuccessor(restrictionType, ManchesterClassExpression.class);

		OWLObjectProperty objectProperty = factory.getOWLObjectProperty(":"
				+ ope.getOriginalText(), pm);

		storage.addAxiom(factory.getOWLDeclarationAxiom(objectProperty));

		if (restrictionType.get() instanceof SomeRestriction) {
			if (expression != null) {
				return factory.getOWLObjectSomeValuesFrom(objectProperty,
						expression);
			}
			else {
				OWLClass clazz = factory.getOWLClass(":"
						+ mce.getChildren().get(0).getOriginalText(),
					pm);
				storage.addAxiom(factory.getOWLDeclarationAxiom(clazz));
				return factory.getOWLObjectSomeValuesFrom(objectProperty, clazz);
			}
		}
		else if (restrictionType.get() instanceof OnlyRestriction) {
			if (expression != null) {
				return factory.getOWLObjectAllValuesFrom(objectProperty,
						expression);
			}
			else {
				OWLClass clazz = factory.getOWLClass(":"
						+ mce.getChildren().get(0).getOriginalText(),
						pm);
				storage.addAxiom(factory.getOWLDeclarationAxiom(clazz));
				return factory.getOWLObjectAllValuesFrom(objectProperty, clazz);
			}
		}
		else if (restrictionType.get() instanceof ValueRestriction) {
			OWLIndividual individual = factory.getOWLNamedIndividual(":"
					+ mce.getChildren().get(0).getOriginalText(), pm);
			// FIXME assert the individual ???
			return factory.getOWLObjectHasValue(objectProperty, individual);
		}
		else if (restrictionType.get() instanceof ExactlyRestriction) {
			Section<NonNegativeInteger> digit = Sections.findSuccessor(mce,
					NonNegativeInteger.class);
			return factory.getOWLObjectExactCardinality(
					Integer.parseInt(digit.getOriginalText()),
					objectProperty);
		}
		else if (restrictionType.get() instanceof MinRestriction) {
			Section<NonNegativeInteger> digit = Sections.findSuccessor(mce,
					NonNegativeInteger.class);
			return factory.getOWLObjectMinCardinality(
					Integer.parseInt(digit.getOriginalText()),
					objectProperty);
		}
		else if (restrictionType.get() instanceof MaxRestriction) {
			Section<NonNegativeInteger> digit = Sections.findSuccessor(mce,
					NonNegativeInteger.class);
			return factory.getOWLObjectMaxCardinality(
					Integer.parseInt(digit.getOriginalText()),
					objectProperty);
		}
		// TODO: oneOf, not missing
		return null;
	}

	/**
	 * Handles a token of {@link BracedCondition}. Each token is linked with one
	 * of the following types: {@link Conjunct}, {@link Disjunct},
	 * {@link NegatedExpression}. Each part can either a
	 * {@link TerminalCondition} or a {@link BracedCondition} again.
	 *
	 * @created 14.09.2011
	 * @param section
	 * @return
	 */
	public static OWLClassExpression handleBracedConditionPart(Section<?> section) {
		Section<?> mce = Sections.findSuccessor(section, ManchesterClassExpression.class);
		OWLClassExpression expression = null;

		if (mce.getChildren().get(0).get() instanceof TerminalCondition) {
			expression = createSimpleRestriction(
					mce.getChildren().get(0),
					null);
		}
		if (mce.getChildren().get(0).get() instanceof BracedCondition) {
			expression = handleBracedCondition(mce, 3);
		}
		return expression;
	}

	/**
	 * Handles possible parts of a {@link BracedCondition}. Such parts can be a
	 * {@link Conjunct}, {@link Disjunct} or {@link NegatedExpression}. If one
	 * of the mentioned parts is found it is handled separately since each can
	 * itself contain again a {@link BracedCondition}.
	 *
	 *
	 * @created 14.09.2011
	 * @param Section<BracedCondition> section
	 * @return OWLClassExpression
	 */
	public static OWLClassExpression handleBracedCondition(Section<?> section, int depth) {

		Set<OWLClassExpression> parts = new HashSet<OWLClassExpression>();

		List<Section<Disjunct>> disjuncts = new ArrayList<Section<Disjunct>>();
		Sections.findSuccessorsOfType(section, Disjunct.class, depth, disjuncts);
		if (disjuncts.size() > 0) {
			for (Section<?> child : disjuncts) {
				OWLClassExpression expression = handleBracedConditionPart(child);
				if (expression != null) {
					parts.add(expression);
				}
			}
			return factory.getOWLObjectUnionOf(parts); // OR
		}

		List<Section<Conjunct>> conjuncts = new ArrayList<Section<Conjunct>>();
		Sections.findSuccessorsOfType(section, Conjunct.class, depth, conjuncts);
		if (conjuncts.size() > 0) {
			for (Section<?> child : conjuncts) {
				OWLClassExpression expression = handleBracedConditionPart(child);
				if (expression != null) {
					parts.add(expression);
				}
			}
			return factory.getOWLObjectIntersectionOf(parts); // AND
		}

		List<Section<NegatedExpression>> neg = new ArrayList<Section<NegatedExpression>>();
		Sections.findSuccessorsOfType(section, NegatedExpression.class, depth, neg);
		if (neg.size() > 0) {
			for (Section<?> child : neg) {
				OWLClassExpression expression = handleBracedConditionPart(child);
				if (expression != null) {
					factory.getOWLObjectComplementOf(expression); // NOT
				}
			}
		}

		Section<?> resriction = Sections.findSuccessor(section, Restriction.class);
		if (resriction != null) {
			return handleRestrictions(resriction); // RESTRICTION
		}

		return null;
	}

	/**
	 * Create out of {@link Restriction} KDOM nodes {@link OWLAxiom} for further
	 * handling.
	 *
	 * @created 10.09.2011
	 */
	public static OWLClassExpression handleRestrictions(Section<?> section) {

		OWLClassExpression currentExpression = null;

		if (section.get() instanceof Restriction) {
			Section<?> mce = Sections.findSuccessor(section, ManchesterClassExpression.class);

			if (mce.getChildren().get(0).get() instanceof TerminalCondition) {
				// simple restriction, no recursion
				currentExpression = createSimpleRestriction(section, null);
			}
			else if (mce.getChildren().get(0).get() instanceof BracedCondition) {
				OWLClassExpression expression = handleBracedCondition(mce.getChildren().get(0), 3);
				if (expression != null) {
					currentExpression = createSimpleRestriction(section, expression);
				}
			}
		}
		else {
			// go to restriction section and call createRestrictions
			Section<?> restriction = Sections.findSuccessor(section, Restriction.class);
			if (restriction != null) {
				currentExpression = handleRestrictions(restriction);
			}
		}
		return currentExpression;
	}

	/**
	 * Create {@link SubClassOf} restrictions axioms from the elements found in
	 * the KDOM. Input is a {@link Restriction} node that either contains an
	 * {@link OWLTermReferenceManchester} or another {@link Restriction}.
	 *
	 * @created 08.09.2011
	 * @param Section section The first restriction node within a
	 *        {@link ListItem}
	 * @param OWLClass father The {@link OWLClass} the restriction belongs to.
	 * @return
	 */
	public static OWLClassExpression createDescriptionExpression(Section<?> section) {

		Section<?> childOfType = section.getChildren().get(0);

		if (childOfType.get() instanceof TerminalCondition) { // Reference||Restriction
			// simple reference detected
			Section<?> subChildOfType = childOfType.getChildren().get(0).getChildren().get(0);

			if (subChildOfType.get() instanceof OWLTermReferenceManchester) {
				return factory.getOWLClass(":" + section.getOriginalText(), pm);
			} // handle restrictions (can be nested, BracketCondition etc.)
			else if (subChildOfType.get() instanceof SomeRestriction
					|| subChildOfType.get() instanceof OnlyRestriction
					|| subChildOfType.get() instanceof ValueRestriction
					|| subChildOfType.get() instanceof MinRestriction
					|| subChildOfType.get() instanceof MaxRestriction
					|| subChildOfType.get() instanceof ExactlyRestriction) {
				OWLClassExpression expression = handleRestrictions(section);
				if (expression != null) {
					return expression;
				}
			}
		}
		else if (childOfType.get() instanceof Conjunct) { // Conjuncts
			return handleBracedCondition(section, 1);
		}
		return null;
	}

	/**
	 * Create {@link DisjointWith} restrictions axioms from the elements found
	 * in the KDOM.
	 *
	 * @created 08.09.2011
	 * @param Section section The first restriction node within a
	 *        {@link ListItem}
	 * @param OWLClass father The {@link OWLClass} the restriction belongs to.
	 * @return
	 */
	public static Collection<OWLAxiom> createDisjointWith(Section<?> section, OWLClass father) {
		Collection<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		if (section.get() instanceof OWLTermReferenceManchester) {
			OWLClass c = factory.getOWLClass(":" + section.getOriginalText(), pm);
			axioms.add(factory.getOWLDisjointClassesAxiom(c, father));
		}
		return axioms;
	}

	/**
	 * Create {@link EquivalentTo} restrictions axioms from the elements found
	 * in the KDOM.
	 *
	 * @created 08.09.2011
	 * @param Section section The first restriction node within a
	 *        {@link ListItem}
	 * @param OWLClass father The {@link OWLClass} the restriction belongs to.
	 * @return
	 */
	public static Collection<OWLAxiom> createEquivalentTo(Section<?> section, OWLClass father) {

		Collection<OWLAxiom> axioms = new ArrayList<OWLAxiom>();

		Type childOfType = section.getChildren().get(0).get();
		if (childOfType instanceof OWLTermReferenceManchester) {
			// simple reference detected
			axioms.add(factory.getOWLSubClassOfAxiom(father, factory.getOWLClass(":"
					+ section.getOriginalText(), pm)));
			return axioms;
		} // handle conjuncts
		else if (childOfType instanceof SomeRestriction || childOfType instanceof OnlyRestriction
				|| childOfType instanceof ValueRestriction) {
			// handle complex restrictions
			OWLClassExpression expression = handleRestrictions(section);
			if (expression != null) {
				axioms.add(factory.getOWLSubClassOfAxiom(father, expression));
			}
		}
		return axioms;
	}

	public static OWLObjectProperty createProperty(Section<?> section) {
		return factory.getOWLObjectProperty(":" + section.getOriginalText(), pm);
	}

	public static OWLAxiom createPropertyDefinition(OWLObjectProperty p) {
		return factory.getOWLDeclarationAxiom(p);
	}

	public static OWLAxiom createCharacteristics(Section<?> section, OWLObjectProperty p) {
		String str = section.getOriginalText();

		if (str.equals(CharacteristicTypes.FUNCTIONAL.getType())) {
			return factory.getOWLFunctionalObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.ASYMMETRIC.getType())) {
			return factory.getOWLAsymmetricObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.INVERSEFUNCTIONAL.getType())) {
			return factory.getOWLInverseFunctionalObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.IRREFLEXIVE.getType())) {
			return factory.getOWLIrreflexiveObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.REFLEXIVE.getType())) {
			return factory.getOWLReflexiveObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.SYMMETRIC.getType())) {
			return factory.getOWLSymmetricObjectPropertyAxiom(p);
		}
		else if (str.equals(CharacteristicTypes.TRANSITIVE.getType())) {
			return factory.getOWLTransitiveObjectPropertyAxiom(p);
		}
		return null;
	}

	public static OWLAxiom createRange(Section<?> section, OWLObjectProperty p) {
		OWLClass cls = factory.getOWLClass(":" + section.getOriginalText(), pm);
		return factory.getOWLObjectPropertyRangeAxiom(p, cls);
	}

	public static OWLAxiom createDomain(Section<?> section, OWLObjectProperty p) {
		OWLClass cls = factory.getOWLClass(":" + section.getOriginalText(), pm);
		return factory.getOWLObjectPropertyDomainAxiom(p, cls);
	}

	public static OWLAxiom createInverseOf(Section<?> section, OWLObjectProperty p) {
		OWLObjectProperty inverse = factory.getOWLObjectProperty(":" + section.getOriginalText(),
				pm);
		return factory.getOWLInverseObjectPropertiesAxiom(p, inverse);
	}

	public static Collection<OWLAxiom> createSubPropertyOf(Section<?> section, OWLObjectProperty p) {
		Collection<OWLAxiom> axioms = new ArrayList<OWLAxiom>();

		OWLObjectProperty subProperty = factory.getOWLObjectProperty(":"
				+ section.getOriginalText(), pm);

		// axioms.add(factory.getOWLDeclarationAxiom(subProperty));
		axioms.add(factory.getOWLSubObjectPropertyOfAxiom(p, subProperty));
		return axioms;
	}

	public static OWLNamedIndividual createIndividual(Section<?> section) {
		Section<?> s = Sections.findSuccessor(section, IndividualFrame.Individual.class);
		return factory.getOWLNamedIndividual(":" + s.getOriginalText(), pm);
	}

	public static OWLAxiom createIndividualAssertion(OWLNamedIndividual i, OWLClass clazz) {
		return factory.getOWLClassAssertionAxiom(clazz, i);
	}

	public static OWLAxiom createAnnotations(Section<?> section, IRI annotatetObject) {

		IRI i = null;
		Section<?> s = Sections.findSuccessor(section, RDFSComment.class);
		if (s == null) {
			s = Sections.findSuccessor(section, RDFSLabel.class);
		}
		if (s == null) return null;
		if (s != null) {
			if (s.get() instanceof RDFSComment) {
				i = OWLRDFVocabulary.RDFS_COMMENT.getIRI();
			}
			else if (s.get() instanceof RDFSLabel) {
				i = OWLRDFVocabulary.RDFS_LABEL.getIRI();
			}
		}

		OWLAnnotation a = null;
		Section<?> annotation = Sections.findSuccessor(s, AnnotationTerm.class);
		Section<?> tag = Sections.findSuccessor(s, AnnotationLanguageTag.class);
		if (tag == null) {
			tag = Sections.findSuccessor(s, AnnotationDatatypeTag.class);
		}

		if (tag != null) {
			a = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(i),
					factory.getOWLLiteral(annotation.getOriginalText(), tag.getOriginalText()));
		}
		else {
			a = factory.getOWLAnnotation(
					factory.getOWLAnnotationProperty(i),
						factory.getOWLLiteral(annotation.getOriginalText()));
		}
		return factory.getOWLAnnotationAssertionAxiom(annotatetObject, a);
	}
}
