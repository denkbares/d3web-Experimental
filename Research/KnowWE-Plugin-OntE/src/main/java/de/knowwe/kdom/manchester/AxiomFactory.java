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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.d3web.core.session.blackboard.Fact;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.CharacteristicTypes;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.DataPropertyExpression;
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
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 *
 *
 * @author Stefan Mark
 * @created 07.09.2011
 */
public class AxiomFactory {

	private static final OWLAPIConnector connector;
	private static final PrefixManager pm;
	private static final OWLDataFactory factory;
	private static final AxiomStorageSubtree storage;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		pm = new DefaultPrefixManager(Rdf2GoCore.basens);
		storage = AxiomStorageSubtree.getInstance();
	}

	public static OWLObjectProperty topProperty() {
		return factory.getOWLTopObjectProperty();
	}

	/**
	 * Create a {@link OWLClass} declaration axiom.
	 *
	 * @created 14.09.2011
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public static OWLAxiom createOWLClass(OWLClass clazz) {
		return factory.getOWLDeclarationAxiom(clazz);
	}
	/**
	 * Returns the correct {@link OWLEntity} object for a given
	 * {@link OWLEntity}. An entity could be a {@link OWLClass},
	 * {@link OWLDataProperty}, {@link OWLObjectProperty} or
	 * {@link OWLIndividual}.
	 *
	 * @created 27.09.2011
	 * @param entity
	 * @return
	 */
	public static OWLAxiom getOWLAPIEntityDeclaration(OWLEntity entity) {
		if (entity instanceof OWLObjectProperty) {
			OWLObjectProperty p = (OWLObjectProperty) entity;
			return factory.getOWLDeclarationAxiom(p);
		}
		else if (entity instanceof OWLDataProperty) {
			OWLDataProperty p = (OWLDataProperty) entity;
			return factory.getOWLDeclarationAxiom(p);
		}
		else if (entity instanceof OWLClass) {
			OWLClass c = entity.asOWLClass();
			return factory.getOWLDeclarationAxiom(c);
		}
		throw new Error("OWL API entity conversion for " + entity + " not supported.");
	}

	/**
	 * Returns the correct {@link OWLEntity} object for a given
	 * {@link OWLEntity}. An entity could be a {@link OWLClass},
	 * {@link OWLDataProperty}, {@link OWLObjectProperty} or
	 * {@link OWLIndividual}.
	 *
	 * @created 27.09.2011
	 * @param entity
	 * @return
	 */
	public static OWLEntity getOWLAPIEntity(Section<? extends Type> section, Class<?> c) {

		if (c.equals(OWLObjectProperty.class)) {
			return factory.getOWLObjectProperty(section.getOriginalText(), pm);
		}
		else if (c.equals(OWLDataProperty.class)) {
			return factory.getOWLDataProperty(section.getOriginalText(), pm);
		}
		else if (c.equals(OWLClass.class)) {
			return factory.getOWLClass(section.getOriginalText(), pm);
		}
		else if (c.equals(OWLIndividual.class)) {
			return factory.getOWLNamedIndividual(section.getOriginalText(), pm);
		}
		// should never happen
		throw new Error("OWL API entity conversion for " + c + " not supported.");
	}

	/**
	 * Returns the correct {@link OWLEntity} object for a given
	 * {@link OWLEntity}. An entity could be a {@link OWLClass},
	 * {@link OWLDataProperty}, {@link OWLObjectProperty} or
	 * {@link OWLIndividual}.
	 *
	 * @created 27.09.2011
	 * @param entity
	 * @return
	 */
	public static OWLEntity getOWLAPIEntity(OWLEntity entity) {
		if (entity instanceof OWLObjectProperty) {
			return factory.getOWLObjectProperty(IRI.create(entity.toStringID()));
		}
		else if (entity instanceof OWLDataProperty) {
			return factory.getOWLDataProperty(IRI.create(entity.toStringID()));
		}
		else if (entity instanceof OWLClass) {
			return factory.getOWLClass(IRI.create(entity.toStringID()));
		}
		else if (entity instanceof OWLIndividual) {
			return factory.getOWLNamedIndividual(IRI.create(entity.toStringID()));
		}
		// should never happen
		throw new Error("OWL API entity conversion for " + entity + " not supported.");
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
			expression = handleBracedCondition(mce.getChildren().get(0), 2);
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

		List<Section<ManchesterClassExpression>> nodes = new ArrayList<Section<ManchesterClassExpression>>();
		Sections.findSuccessorsOfType(section, ManchesterClassExpression.class, depth, nodes);

		boolean isDisjunct = false, isConjunct = false, isComplement = false;
		List<Section<? extends NonTerminalCondition>> xjunctions = new ArrayList<Section<? extends NonTerminalCondition>>();


		// check for disjunct, conjunct or complement or oneOf
		if (nodes.size() > 0) {
			Section<ManchesterClassExpression> mce = nodes.get(0);
			CompositeCondition cc = mce.get();
			if (cc.isDisjunction(mce)) {
				isDisjunct = true;
				xjunctions = cc.getDisjuncts(mce);
			}
			if (cc.isConjunction(mce)) {
				isConjunct = true;
				xjunctions = cc.getConjuncts(mce);
			}
			if (cc.isNegation(mce)) {
				isComplement = true;
				OWLClassExpression expression = handleBracedConditionPart(mce.getChildren().get(0));
				if (expression != null) {
					return factory.getOWLObjectComplementOf(expression); // NOT
				}
			}
			if (mce.get().hasOneOf(mce)) { // ONE OF
				List<Section<OWLTermReferenceManchester>> refs = mce.get().getOneOfs(mce);
				for (Section<OWLTermReferenceManchester> r : refs) {
					OWLClassExpression expression = handleBracedConditionPart(r);
					if (expression != null && isComplement == false) {
						parts.add(expression);
					}
				}

				Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
				for (OWLClassExpression c : parts) {
					individuals.add(factory.getOWLNamedIndividual(((OWLClass) c).getIRI()));
				}
				return factory.getOWLObjectOneOf(individuals); // ONE OF
			}
		}

		for (Section<?> child : xjunctions) {
			OWLClassExpression expression = handleBracedConditionPart(child);
			if (expression != null && isComplement == false) {
				parts.add(expression);
			}
		}
		if (isDisjunct) {
			return factory.getOWLObjectUnionOf(parts); // OR
		}
		if (isConjunct) {
			return factory.getOWLObjectIntersectionOf(parts); // AND
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
	 * @param Section section
	 * @created 10.09.2011
	 */
	public static OWLClassExpression handleRestrictions(Section<?> section) {

		OWLClassExpression currentExpression = null;

		if (section.get() instanceof Restriction) {
			Section<?> mce = Sections.findSuccessor(section, ManchesterClassExpression.class);

			if (mce != null) {
				if (mce.getChildren().get(0).get() instanceof TerminalCondition) {
					// simple restriction, no recursion
					currentExpression = createSimpleRestriction(section, null);
				}
				else if (mce.getChildren().get(0).get() instanceof BracedCondition) {
					OWLClassExpression expression = handleBracedCondition(mce.getChildren().get(0),
							2);
					if (expression != null) {
						currentExpression = createSimpleRestriction(section, expression);
					}
				}
			}
			else {
				mce = Sections.findSuccessor(section, OWLTermReferenceManchester.class);
				if(mce != null) { //OWLTermReferenceManchester
					OWLClass clazz = factory.getOWLClass(":"
							+ mce.getOriginalText(),
							pm);
					storage.addAxiom(factory.getOWLDeclarationAxiom(clazz));
					currentExpression = clazz;
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
	 * Create description axioms from the elements found in the KDOM. This is
	 * rather a complex issue, so be careful when changing the following lines.
	 * The Input is a {@link ManchesterClassExpression} that contains all the
	 * necessary information about its children, e.g. Conjuncts, Disjuncts,
	 * ListElements or TerminalConditions.
	 *
	 * @created 08.09.2011
	 * @param Section<ManchesterClassExpression> section
	 * @return A set of {@link OWLClassExpression} for further processing.
	 */
	public static Set<OWLClassExpression> createDescriptionExpression(Section<ManchesterClassExpression> section) {

		Section<?> child = section.getChildren().get(0);
		CompositeCondition cc = (ManchesterClassExpression) section.get();
		Set<OWLClassExpression> exp = new HashSet<OWLClassExpression>();

		// Handle MinTwoList, NonEmptyList



		// Handle Conjuncts, Disjuncts
		if (cc.isConjunction(section) || cc.isDisjunction(section)) {
			OWLClassExpression expression = handleBracedCondition(section, 1);
			if (expression != null) {
				exp.add(expression);
			}
		}

		// for (Section<?> child : section.getChildren()) {
		// Section<?> childOfType = child;
		// if (childOfType.get() instanceof TerminalCondition) {
		// // look two levels below the TerminalCondition and
		// // decide what to to next
		// Section<?> subChildOfType =
		// childOfType.getChildren().get(0).getChildren().get(0);
		//
		// // just a simple reference -> handle it
		// if (subChildOfType.get() instanceof OWLTermReferenceManchester) {
		// exp.add(factory.getOWLClass(":" + subChildOfType.getOriginalText(),
		// pm));
		// }
		// // restriction found -> handle it
		// else if (subChildOfType.get() instanceof SomeRestriction
		// || subChildOfType.get() instanceof OnlyRestriction
		// || subChildOfType.get() instanceof ValueRestriction
		// || subChildOfType.get() instanceof MinRestriction
		// || subChildOfType.get() instanceof MaxRestriction
		// || subChildOfType.get() instanceof ExactlyRestriction) {
		// OWLClassExpression expression = handleRestrictions(subChildOfType);
		// if (expression != null) {
		// exp.add(expression);
		// }
		// }
		// return exp;
		// }
		// else if (childOfType.get() instanceof MinTwoList) {
		// OWLClassExpression expression = handleBracedCondition(childOfType,
		// 3);
		// if (expression != null) {
		// exp.add(expression);
		// }
		// }
		//
		// // check if the MCE is a Conjunct or Disjunct
		// CompositeCondition cc = (ManchesterClassExpression) section.get();
		// if (cc.isConjunction(section) || cc.isDisjunction(section)) {
		// OWLClassExpression expression = handleBracedCondition(section, 1);
		// if (expression != null) {
		// exp.add(expression);
		// }
		// }
		// }
		return exp;
	}
	/**
	 *
	 *
	 * @created 28.09.2011
	 * @param section
	 * @param p
	 * @return
	 */
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
		return null; // FIXME return KDOMReportMessage
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
	/**
	 *
	 *
	 * @created 15.09.2011
	 * @param section
	 * @param i
	 * @return
	 */
	public static OWLAxiom createNamedIndividualAxiom(OWLClassExpression e, OWLIndividual i) {
		if (e != null && e.isClassExpressionLiteral()) {
			OWLClass c = e.asOWLClass();
			if (!c.isOWLThing()) return factory.getOWLClassAssertionAxiom(c, i);
		}
		return null;
	}
	/**
	 * Create a {@link OWLSameIndividualAxiom} between two {@link OWLIndividual}
	 *
	 * @created 28.09.2011
	 * @param OOWLIndividual one
	 * @param OWLIndividual two
	 * @return OWLSameIndividualAxiom
	 */
	public static OWLAxiom createSameIndividualsAxiom(OWLIndividual one, OWLIndividual two) {
		return factory.getOWLSameIndividualAxiom(one, two);
	}
	/**
	 * Create a {@link OWLDifferentIndividualsAxiom} between two
	 * {@link OWLIndividual}.
	 *
	 * @created 28.09.2011
	 * @param OOWLIndividual one
	 * @param OWLIndividual two
	 * @return OWLDifferentIndividualsAxiom
	 */
	public static OWLAxiom createDifferentFromIndividualsAxiom(OWLIndividual one, OWLIndividual two) {
		return factory.getOWLDifferentIndividualsAxiom(one, two);
	}
	/**
	 * Create Facts axioms out of the {@link Fact} KDOM nodes.
	 *
	 * @created 27.09.2011
	 * @param Section<?> section
	 * @param OWLIndividual i
	 * @return
	 */
	public static OWLAxiom createFact(Section<?> section, OWLIndividual i) {
		Section<ObjectPropertyExpression> ope = Sections.findSuccessor(section,
				ObjectPropertyExpression.class);
		Section<OWLTermReferenceManchester> ref = Sections.findSuccessor(section,
				OWLTermReferenceManchester.class);
		if (ope != null && ref != null) {
			OWLObjectProperty p = factory.getOWLObjectProperty(":" + ope.getOriginalText(), pm);
			OWLNamedIndividual ind = factory.getOWLNamedIndividual(":" + ref.getOriginalText(), pm);
			return factory.getOWLObjectPropertyAssertionAxiom(p, i, ind);
		}
		return null; // FIXME return KDOMReportMessage
	}
	/**
	 * Creates out of the found {@link Annotations} in the KDOM correct
	 * {@link OWLAnnotation} axioms. Those {@link OWLAxiom} can than be added to
	 * the ontology.
	 *
	 * @created 15.09.2011
	 * @param Section<Annotation> section A {@link Annotation} section
	 * @param IRI annotatetObject The resource the {@link Annotation} belongs
	 *        to.
	 * @return
	 */
	public static OWLAxiom createAnnotations(Section<Annotation> section, IRI annotatetObject) {

		OWLAnnotation a = createOWLAnnotation(section);
		if (a != null) {
			return factory.getOWLAnnotationAssertionAxiom(annotatetObject, a);
		}
		return null;
	}

	/**
	 *
	 *
	 * @created 29.09.2011
	 * @param Section<Annotation> section
	 * @return
	 */
	public static OWLAnnotation createOWLAnnotation(Section<Annotation> section) {
		IRI annotationIRI = null;
		Annotation annotationType = section.get();
		if (annotationType.isLabel(section)) {
			annotationIRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();
		}
		else if (annotationType.isComment(section)) {
			annotationIRI = OWLRDFVocabulary.RDFS_COMMENT.getIRI();
		}
		if (annotationIRI != null) {

			String term = "";
			String tag = "";

			term = annotationType.getTerm(section).getOriginalText();

			// check for optional tags (language, data type)
			if (annotationType.hasLanguageTag(section)) {
				tag = annotationType.getLanguage(section).getOriginalText();
			}
			else if (annotationType.hasDatatypeTag(section)) {
				tag = annotationType.getDatatype(section).getOriginalText();
			}
			return createOWLAnnotation(annotationIRI, term, tag);
		}
		return null;
	}
	/**
	 * Creates {@link OWLAnnotation}.
	 *
	 * @created 29.09.2011
	 * @param property
	 * @param value
	 * @param tag
	 * @return
	 */
	public static OWLAnnotation createOWLAnnotation(IRI property, String value, String tag) {

		if (property != null && value != null) {
			if (tag != null && !tag.isEmpty()) {
				return factory.getOWLAnnotation(
						factory.getOWLAnnotationProperty(property),
						factory.getOWLLiteral(value, tag));
			}
			else {
				return factory.getOWLAnnotation(
						factory.getOWLAnnotationProperty(property),
						factory.getOWLLiteral(value));
			}
		}
		return null;
	}

	/**
	 * Create the axioms for the EquivalentClasses and DisjointClasses frame of
	 * the Manchester OWL syntax.
	 *
	 * @created 22.09.2011
	 * @param Section<MiscFrame> section The section containing the information
	 *        about the {@link MiscFrame}
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameClasses(Section<MiscFrame> section, Set<OWLAnnotation> annotations) {

		MiscFrame type = section.get();
		List<Section<OWLTermReferenceManchester>> references = Sections.findSuccessorsOfType(
				section, OWLTermReferenceManchester.class);

		Set<OWLClassExpression> parts = new HashSet<OWLClassExpression>();

		for (Section<OWLTermReferenceManchester> r : references) {
			OWLClass clazz = factory.getOWLClass(":" + r.getOriginalText(), pm);
			storage.addAxiom(factory.getOWLDeclarationAxiom(clazz));
		}

		if (type.isDisjointClasses(section)) {
			return factory.getOWLDisjointClassesAxiom(parts, annotations);
		}
		else if (type.isEquivalentClasses(section)) {
			return factory.getOWLEquivalentClassesAxiom(parts, annotations);
		}
		return null;
	}

	/**
	 * Create the axioms for the SameIndividual and DifferentIndividuals frame
	 * of the Manchester OWL syntax.
	 *
	 * @created 22.09.2011
	 * @param Section<MiscFrame> section The section containing the information
	 *        about the {@link MiscFrame}
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameIndividuals(Section<MiscFrame> section, Set<OWLAnnotation> annotations) {

		MiscFrame type = section.get();
		List<Section<OWLTermReferenceManchester>> references = Sections.findSuccessorsOfType(
				section, OWLTermReferenceManchester.class);

		Set<OWLNamedIndividual> parts = new HashSet<OWLNamedIndividual>();

		for (Section<OWLTermReferenceManchester> r : references) {
			parts.add(factory.getOWLNamedIndividual(":" + r.getOriginalText(), pm));
		}

		if (type.isDifferentIndividuals(section)) {
			return factory.getOWLDifferentIndividualsAxiom(parts, annotations);
		}
		else if (type.isSameIndividuals(section)) {
			return factory.getOWLSameIndividualAxiom(parts, annotations);
		}
		return null;
	}

	/**
	 * Create the axioms for the EquivalentObjectProperties and
	 * DisjointObjectProperties frame of the Manchester OWL syntax.
	 *
	 * @created 22.09.2011
	 * @param Section<MiscFrame> section The section containing the information
	 *        about the {@link MiscFrame}
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameObjectProperties(Section<MiscFrame> section, Set<OWLAnnotation> annotations) {

		MiscFrame type = section.get();
		List<Section<ObjectPropertyExpression>> references = Sections.findSuccessorsOfType(
				section, ObjectPropertyExpression.class);

		Set<OWLObjectProperty> parts = new HashSet<OWLObjectProperty>();

		for (Section<ObjectPropertyExpression> r : references) {
			parts.add(factory.getOWLObjectProperty(":" + r.getOriginalText(), pm));
		}

		if (type.isDisjointProperties(section)) {
			return factory.getOWLDisjointObjectPropertiesAxiom(parts, annotations);
		}
		else if (type.isEquivalentProperties(section)) {
			return factory.getOWLEquivalentObjectPropertiesAxiom(parts, annotations);
		}
		return null;
	}

	/**
	 * Create the axioms for the EquivalentDatatypeProperties and
	 * DisjointDatatypeProperties frame of the Manchester OWL syntax.
	 *
	 * @created 22.09.2011
	 * @param Section<MiscFrame> section The section containing the information
	 *        about the {@link MiscFrame}
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameDataProperties(Section<MiscFrame> section, Set<OWLAnnotation> annotations) {

		MiscFrame type = section.get();
		List<Section<DataPropertyExpression>> references = Sections.findSuccessorsOfType(
				section, DataPropertyExpression.class);

		Set<OWLDataProperty> parts = new HashSet<OWLDataProperty>();

		for (Section<DataPropertyExpression> r : references) {
			parts.add(factory.getOWLDataProperty(":" + r.getOriginalText(), pm));
		}

		if (type.isDisjointProperties(section)) {
			return factory.getOWLDisjointDataPropertiesAxiom(parts, annotations);
		}
		else if (type.isEquivalentProperties(section)) {
			return factory.getOWLEquivalentDataPropertiesAxiom(parts, annotations);
		}
		return null;
	}
}
