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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.CardinalityRestriction;
import de.knowwe.kdom.manchester.types.DataPropertyExpression;
import de.knowwe.kdom.manchester.types.NonTerminalList;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.kdom.manchester.types.OneOfBracedCondition;
import de.knowwe.kdom.manchester.types.OnlyRestriction;
import de.knowwe.kdom.manchester.types.Restriction;
import de.knowwe.kdom.manchester.types.SelfRestriction;
import de.knowwe.kdom.manchester.types.SomeRestriction;
import de.knowwe.kdom.manchester.types.ValueRestriction;
import de.knowwe.onte.owl.terminology.URIUtil;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.util.ManchesterSyntaxKeywords;

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

	/**
	 * Returns the a {@link OWLDeclarationAxiom} for a {@link OWLEntity} object.
	 * An entity could be a {@link OWLClass}, {@link OWLDataProperty},
	 * {@link OWLObjectProperty} or {@link OWLIndividual}.
	 * 
	 * @created 27.09.2011
	 * @param OWLEntity entity
	 * @return OWLAxiom
	 */
	public static OWLAxiom getOWLAPIEntityDeclaration(Section<? extends Type> section, Class<?> c) {
		OWLEntity entity = getOWLAPIEntity(section, c);
		return getOWLAPIEntityDeclaration(entity);
	}

	/**
	 * Returns the a {@link OWLDeclarationAxiom} for a {@link OWLEntity} object.
	 * An entity could be a {@link OWLClass}, {@link OWLDataProperty},
	 * {@link OWLObjectProperty} or {@link OWLIndividual}.
	 * 
	 * @created 27.09.2011
	 * @param OWLEntity entity
	 * @return OWLAxiom
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
		// should never happen
		throw new Error("OWL API entity conversion for " + entity + " not supported.");
	}

	/**
	 * Returns the correct {@link OWLEntity} object for a given
	 * {@link OWLEntity}. An entity could be a {@link OWLClass},
	 * {@link OWLDataProperty}, {@link OWLObjectProperty} or
	 * {@link OWLIndividual}.
	 * 
	 * @created 27.09.2011
	 * @param Section<? extends Type> section
	 * @param Class c
	 * @return OWLEntity
	 */
	public static OWLEntity getOWLAPIEntity(Section<? extends Type> section, Class<?> c) {
		return getOWLAPIEntity(section.getOriginalText(), c);
	}

	/**
	 * Returns the correct {@link OWLEntity} object for a given
	 * {@link OWLEntity}. An entity could be a {@link OWLClass},
	 * {@link OWLDataProperty}, {@link OWLObjectProperty} or
	 * {@link OWLIndividual}.
	 * 
	 * @created 27.09.2011
	 * @param Section<? extends Type> section
	 * @param Class c
	 * @return OWLEntity
	 */
	public static OWLEntity getOWLAPIEntity(String concept, Class<?> c) {

		if (concept.equalsIgnoreCase(URIUtil.THING)) {
			return factory.getOWLThing();
		}

		if (c.equals(OWLObjectProperty.class)) {
			return factory.getOWLObjectProperty(concept, pm);
		}
		else if (c.equals(OWLDataProperty.class)) {
			return factory.getOWLDataProperty(concept, pm);
		}
		else if (c.equals(OWLClass.class)) {
			return factory.getOWLClass(concept, pm);
		}
		else if (c.equals(OWLIndividual.class) || c.equals(OWLNamedIndividual.class)) {
			return factory.getOWLNamedIndividual(concept, pm);
		}
		// should never happen
		throw new Error("OWL API entity conversion for " + c + " not supported.");
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
	public static Map<OWLClassExpression, Section<? extends Type>> createDescriptionExpression(Section<ManchesterClassExpression> section, Collection<Message> messages) {

		ManchesterClassExpression type = section.get();
		Map<OWLClassExpression, Section<? extends Type>> exp = new HashMap<OWLClassExpression, Section<? extends Type>>();

		// A description may either be NonTerminalList ...
		if (type.isNonTerminalList(section)) {
			List<Section<NonTerminalList>> xjunctions = type.getNonTerminalListElements(section);
			Map<OWLClassExpression, Section<? extends Type>> set = new HashMap<OWLClassExpression, Section<? extends Type>>();
			for (Section<NonTerminalList> child : xjunctions) {
				set.putAll(createDescriptionExpression(Sections.findSuccessor(child,
						ManchesterClassExpression.class), messages));
			}
			exp.putAll(set);
			return exp;
		}

		// ... or a OneOfCurlyBracket ...
		if (type.isOneOfCurlyBracket(section)) {
			Map<OWLObject, Section<? extends Type>> set = new HashMap<OWLObject, Section<? extends Type>>();
			Section<OneOfBracedCondition> one = type.getOneOfCurlyBracket(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(one,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("OneOfCurlyBracket is empty!"));
				return null;
			}

			set.putAll(createDescriptionExpression(mce, messages));

			Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
			for (OWLObject c : set.keySet()) {
				individuals.add(factory.getOWLNamedIndividual(((OWLClass) c).getIRI()));
			}
			exp.put(factory.getOWLObjectOneOf(individuals), mce);
			return exp;
		}

		// ... or a BracedCondition ...
		if (type.isBraced(section)) {
			Section<? extends NonTerminalCondition> braced = type.getBraced(section);
			Section<ManchesterClassExpression> mce = Sections.findSuccessor(braced,
					ManchesterClassExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("Bracket is empty!"));
				return null;
			}

			exp.putAll(createDescriptionExpression(mce, messages));
			return exp;
		}

		// ... or a Conjunct ...
		if (type.isConjunction(section)) {
			List<Section<? extends NonTerminalCondition>> xjunctions = new ArrayList<Section<? extends NonTerminalCondition>>();
			xjunctions = type.getConjuncts(section);
			Map<OWLClassExpression, Section<? extends Type>> set = new HashMap<OWLClassExpression, Section<? extends Type>>();

			for (Section<?> child : xjunctions) {
				set.putAll(createDescriptionExpression(Sections.findSuccessor(child,
						ManchesterClassExpression.class), messages));
			}
			if (!set.isEmpty()) {
				Set<OWLClassExpression> tmp = new HashSet<OWLClassExpression>();
				tmp.addAll(set.keySet());
				exp.put(factory.getOWLObjectIntersectionOf(tmp), section);
			}
			return exp;
		}

		// ... or a Disjuncts ...
		if (type.isDisjunction(section)) {
			List<Section<? extends NonTerminalCondition>> xjunctions = new ArrayList<Section<? extends NonTerminalCondition>>();
			xjunctions = type.getDisjuncts(section);
			Map<OWLClassExpression, Section<? extends Type>> set = new HashMap<OWLClassExpression, Section<? extends Type>>();
			for (Section<?> child : xjunctions) {
				set.putAll(createDescriptionExpression(Sections.findSuccessor(child,
						ManchesterClassExpression.class), messages));
			}
			if (!set.isEmpty()) {
				Set<OWLClassExpression> tmp = new HashSet<OWLClassExpression>();
				tmp.addAll(set.keySet());
				exp.put(factory.getOWLObjectUnionOf(tmp), section);
			}
			return exp;
		}

		// ... or a Negation ...
		if (type.isNegation(section)) {
			Section<?> neg = type.getNegation(section);

			Map<OWLObject, Section<? extends Type>> set = new HashMap<OWLObject, Section<? extends Type>>();
			set.putAll(createDescriptionExpression(Sections.findSuccessor(neg,
					ManchesterClassExpression.class), messages));
			if (set.size() > 0) {
				exp.put(factory.getOWLObjectComplementOf(
						(OWLClassExpression) set.keySet().iterator().next()), section);
			}
			return exp;
		}

		// ... or simply a TerminalCondition
		if (type.isTerminal(section)) {
			Section<?> terminal = type.getTerminal(section);
			OWLClassExpression oce = handleTerminals(terminal, messages);
			if (oce != null) {
				exp.put(oce, section);
			}
		}
		return exp;
	}

	/**
	 * Handles all the possible {@link TerminalCondition} of the
	 * {@link ManchesterClassExpression}. If you add a new
	 * {@link TerminalCondition}, please update the following lines accordingly.
	 * 
	 * @created 30.09.2011
	 * @param Section<? extends TerminalCondition> section
	 * @return OWLClassExpression
	 */
	private static OWLClassExpression handleTerminals(Section<?> section, Collection<Message> messages) {

		Section<Restriction> restrictionSection = Sections.findSuccessor(section, Restriction.class);
		Restriction r = restrictionSection.get();

		// A TerminalCondition can either be a SomeRestriction ...
		if (r.isSomeRestriction(restrictionSection)) {
			Section<SomeRestriction> some = r.getSomeRestriction(restrictionSection);
			Section<ObjectPropertyExpression> ope = some.get().getObjectProperty(some);
			Section<ManchesterClassExpression> mce = some.get().getManchesterClassExpression(some);

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope, OWLObjectProperty.class);

			Map<OWLClassExpression, Section<? extends Type>> exp = createDescriptionExpression(mce,
					messages);
			if (!exp.isEmpty()) {
				return factory.getOWLObjectSomeValuesFrom(p,
						exp.keySet().iterator().next());
			}
		}

		// ... or a OnlyRestriction ...
		if (r.isOnlyRestriction(restrictionSection)) {
			Section<OnlyRestriction> only = r.getOnlyRestriction(restrictionSection);
			Section<ObjectPropertyExpression> ope = only.get().getObjectProperty(only);
			Section<ManchesterClassExpression> mce = only.get().getManchesterClassExpression(only);

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope, OWLObjectProperty.class);

			Map<OWLClassExpression, Section<? extends Type>> exp = createDescriptionExpression(mce,
					messages);
			if (!exp.isEmpty()) {
				return factory.getOWLObjectAllValuesFrom(p,
						exp.keySet().iterator().next());
			}
		}

		// ... or a SelfRestriction ...
		if (r.isSelfRestriction(restrictionSection)) {
			Section<SelfRestriction> self = r.getSelfRestriction(restrictionSection);
			Section<ObjectPropertyExpression> ope = self.get().getObjectProperty(self);

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope, OWLObjectProperty.class);
			return factory.getOWLObjectHasSelf(p);
		}

		// ... or a ValueRestriction ...
		if (r.isValueRestriction(restrictionSection)) {
			Section<ValueRestriction> only = r.getValueRestriction(restrictionSection);
			Section<ObjectPropertyExpression> ope = only.get().getObjectProperty(only);
			Section<ManchesterClassExpression> mce = only.get().getManchesterClassExpression(only);

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope, OWLObjectProperty.class);
			OWLEntity entity = getOWLAPIEntity(mce, OWLIndividual.class);

			return factory.getOWLObjectHasValue(p, (OWLNamedIndividual) entity);
		}

		// ... or a CardinalityRestriction ...
		// ... or a MinRestriction|MaxRestriction|ExactlyRestriction ...
		if (r.isCardinalityRestriction(restrictionSection)) {

			Section<CardinalityRestriction> cardSection = r.getCardinalityRestriction(restrictionSection);
			CardinalityRestriction cr = cardSection.get();

			Section<ObjectPropertyExpression> ope = cr.getObjectProperty(cardSection);

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope, OWLObjectProperty.class);
			Integer digit = cr.getDigit(cardSection);

			OWLClassExpression exp = null;
			if (cr.hasOptionalRestriction(cardSection)) {
				Section<Restriction> optional = cr.getOptionalRestriction(cardSection);
				if (optional.get().isTermReference(optional)) {
					exp = handleTerminals(cardSection, messages);
				}
				else {
					// FIXME not yet tested
					Section<ManchesterClassExpression> mce = Sections.findSuccessor(cardSection,
							ManchesterClassExpression.class);

					exp = createDescriptionExpression(mce, messages).keySet().iterator().next();
				}
			}

			if (cr.isMaxRestriction(cardSection)) {
				return factory.getOWLObjectMaxCardinality(digit, p, exp);
			}
			if (cr.isMinRestriction(cardSection)) {
				return factory.getOWLObjectMinCardinality(digit, p, exp);
			}
			if (cr.isExactlyRestriction(cardSection)) {
				return factory.getOWLObjectExactCardinality(digit, p, exp);
			}
		}

		// ... or a OWLTermReference ...
		if (r.isTermReference(restrictionSection)) {
			OWLEntity entity = getOWLAPIEntity(r.getTermReference(restrictionSection),
					OWLClass.class);
			return (OWLClass) entity;
		}
		messages.add(Messages.syntaxError("Unknown TerminalCondition found!"));
		return null; // ... or 'This should never happen' :)
	}

	/**
	 * 
	 * 
	 * @created 04.10.2011
	 * @param Section<ManchesterClassExpression> section
	 * @return
	 */
	public static Set<OWLObjectProperty> createObjectPropertyExpression(Section<ManchesterClassExpression> section) {
		ManchesterClassExpression type = section.get();
		Set<OWLObjectProperty> exp = new HashSet<OWLObjectProperty>();

		// A ObjectProeprtyExpression may either be NonTerminalList ...
		if (type.isNonTerminalList(section)) {
			List<Section<NonTerminalList>> xjunctions = type.getNonTerminalListElements(section);

			Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

			for (Section<NonTerminalList> child : xjunctions) {
				set.addAll(createObjectPropertyExpression(Sections.findSuccessor(child,
						ManchesterClassExpression.class)));
			}
			exp.addAll(set);
			return exp;
		}
		// ... or simply a TerminalCondition
		if (type.isTerminal(section)) {
			Section<? extends TerminalCondition> terminal = type.getTerminal(section);

			Section<Restriction> restrictionSection = Sections.findSuccessor(terminal,
					Restriction.class);
			Restriction r = restrictionSection.get();

			if (r.isObjectProperty(restrictionSection)) {
				OWLEntity entity = getOWLAPIEntity(r.getObjectProperty(restrictionSection),
								OWLObjectProperty.class);
				exp.add((OWLObjectProperty) entity);
			}
		}
		return exp;
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
	 * 
	 * 
	 * @created 28.09.2011
	 * @param section
	 * @param p
	 * @return
	 */
	public static OWLAxiom createCharacteristics(Section<?> section, OWLObjectProperty p, Collection<Message> messages) {
		String str = section.getOriginalText();

		if (str.equals(ManchesterSyntaxKeywords.FUNCTIONAL.getKeyword())) {
			return factory.getOWLFunctionalObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.ASYMMETRIC.getKeyword())) {
			return factory.getOWLAsymmetricObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.INVERSE_FUNCTIONAL.getKeyword())) {
			return factory.getOWLInverseFunctionalObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.IRREFLEXIVE.getKeyword())) {
			return factory.getOWLIrreflexiveObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.REFLEXIVE.getKeyword())) {
			return factory.getOWLReflexiveObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.SYMMETRIC.getKeyword())) {
			return factory.getOWLSymmetricObjectPropertyAxiom(p);
		}
		else if (str.equals(ManchesterSyntaxKeywords.TRANSITIVE.getKeyword())) {
			return factory.getOWLTransitiveObjectPropertyAxiom(p);
		}
		messages.add(Messages.syntaxError("Characteristic is unknown"));
		return null;
	}

	public static OWLAxiom createRange(OWLObjectProperty p, OWLClassExpression exp) {
		return factory.getOWLObjectPropertyRangeAxiom(p, exp);
	}

	public static OWLAxiom createDomain(OWLObjectProperty p, OWLClassExpression exp) {
		return factory.getOWLObjectPropertyDomainAxiom(p, exp);
	}

	public static OWLAxiom createInverseOf(OWLObjectProperty p, OWLObjectProperty exp) {
		return factory.getOWLInverseObjectPropertiesAxiom(p, exp);
	}

	public static OWLAxiom createSubPropertyOf(OWLObjectProperty p, OWLObjectProperty subProperty) {
		return factory.getOWLSubObjectPropertyOfAxiom(p, subProperty);
	}

	public static OWLAxiom createSubPropertyChain(OWLObjectProperty p, List<Section<ObjectPropertyExpression>> objectProperties) {

		List<OWLObjectPropertyExpression> owlObjectProperties = new ArrayList<OWLObjectPropertyExpression>();
		for (Section<ObjectPropertyExpression> ope : objectProperties) {
			owlObjectProperties.add((OWLObjectProperty) getOWLAPIEntity(ope,
					OWLObjectProperty.class));
		}
		return factory.getOWLSubPropertyChainOfAxiom(owlObjectProperties, p);
	}

	public static OWLAxiom createDisjointWith(OWLObjectProperty p1, OWLObjectProperty p2) {
		return factory.getOWLDisjointObjectPropertiesAxiom(p1, p2);
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
			if (c.isOWLThing()) {
				return factory.getOWLClassAssertionAxiom(factory.getOWLThing(), i);
			}
			return factory.getOWLClassAssertionAxiom(c, i);
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
	public static OWLAxiom createFact(Section<?> section, OWLIndividual i, Collection<Message> messages) {
		Section<ObjectPropertyExpression> ope = Sections.findSuccessor(section,
				ObjectPropertyExpression.class);
		Section<OWLTermReferenceManchester> ref = Sections.findSuccessor(section,
				OWLTermReferenceManchester.class);
		if (ope != null && ref != null) {
			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
					OWLObjectProperty.class);
			OWLNamedIndividual ind = (OWLNamedIndividual) getOWLAPIEntity(ref,
					OWLIndividual.class);
			return factory.getOWLObjectPropertyAssertionAxiom(p, i, ind);
		}
		messages.add(Messages.syntaxError("ObjectProperty or Termreference missing!"));
		return null;
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
	public static OWLAxiom createAnnotations(Section<Annotation> section, IRI annotatetObject, Collection<Message> messages) {
		OWLAnnotation a = createOWLAnnotation(section, messages);
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
	public static OWLAnnotation createOWLAnnotation(Section<Annotation> section, Collection<Message> messages) {
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

			if (annotationType.getTerm(section) == null) {
				messages.add(Messages.syntaxError("annotationterm not found!"));
				return null;
			}
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
		messages.add(Messages.syntaxError(
				"undefined annotation type found, please specify {rdfs:label, rdfs:comment}!"));
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
	 * @param Set<OWLAnnotation> annotations A set of optional annotations
	 * @param Collection<KDOMReportMessage> messages A list containing messages
	 *        due to the handling of the KDOM
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameClasses(Section<MiscFrame> section, Set<OWLAnnotation> annotations, Collection<Message> messages) {

		MiscFrame type = section.get();
		List<Section<OWLTermReferenceManchester>> references = Sections.findSuccessorsOfType(
				section, OWLTermReferenceManchester.class);

		if (references.isEmpty()) {
			messages.add(Messages.syntaxError("list of classes not found!"));
			return null;
		}

		Set<OWLClassExpression> parts = new HashSet<OWLClassExpression>();

		for (Section<OWLTermReferenceManchester> r : references) {
			OWLEntity clazz = getOWLAPIEntity(r, OWLClassExpression.class);
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
	 * @param Set<OWLAnnotation> annotations A set of optional annotations
	 * @param Collection<KDOMReportMessage> messages A list containing messages
	 *        due to the handling of the KDOM
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameIndividuals(Section<MiscFrame> section, Set<OWLAnnotation> annotations, Collection<Message> messages) {

		MiscFrame type = section.get();
		List<Section<OWLTermReferenceManchester>> references = Sections.findSuccessorsOfType(
				section, OWLTermReferenceManchester.class);

		Set<OWLNamedIndividual> parts = new HashSet<OWLNamedIndividual>();

		if (references.isEmpty()) {
			messages.add(Messages.syntaxError("list of individuals not found!"));
			return null;
		}

		for (Section<OWLTermReferenceManchester> r : references) {
			parts.add((OWLNamedIndividual) getOWLAPIEntity(r, OWLNamedIndividual.class));
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
	 * @param Set<OWLAnnotation> annotations A set of optional annotations
	 * @param Collection<KDOMReportMessage> messages A list containing messages
	 *        due to the handling of the KDOM
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameObjectProperties(Section<MiscFrame> section, Set<OWLAnnotation> annotations, Collection<Message> messages) {

		MiscFrame type = section.get();
		List<Section<ObjectPropertyExpression>> references = Sections.findSuccessorsOfType(
				section, ObjectPropertyExpression.class);

		if (references.isEmpty()) {
			messages.add(Messages.syntaxError("list of object properties not found!"));
			return null;
		}

		Set<OWLObjectProperty> parts = new HashSet<OWLObjectProperty>();

		for (Section<ObjectPropertyExpression> r : references) {
			parts.add((OWLObjectProperty) getOWLAPIEntity(r, OWLObjectProperty.class));
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
	 * @param Set<OWLAnnotation> annotations A set of optional annotations
	 * @param Collection<KDOMReportMessage> messages A list containing messages
	 *        due to the handling of the KDOM
	 * @return {@link OWLAxiom}
	 */
	public static OWLAxiom createMiscFrameDataProperties(Section<MiscFrame> section, Set<OWLAnnotation> annotations, Collection<Message> messages) {

		MiscFrame type = section.get();
		List<Section<DataPropertyExpression>> references = Sections.findSuccessorsOfType(
				section, DataPropertyExpression.class);

		if (references.isEmpty()) {
			messages.add(Messages.syntaxError("list of data properties not found!"));
			return null;
		}

		Set<OWLDataProperty> parts = new HashSet<OWLDataProperty>();

		for (Section<DataPropertyExpression> r : references) {
			parts.add((OWLDataProperty) getOWLAPIEntity(r, OWLDataProperty.class));
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
