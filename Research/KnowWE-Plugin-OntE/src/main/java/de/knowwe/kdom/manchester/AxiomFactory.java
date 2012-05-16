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
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.compile.utils.ImportedOntologyManager;
import de.knowwe.kdom.manchester.frame.MiscFrame;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.BraceElement;
import de.knowwe.kdom.manchester.types.CardinalityRestriction;
import de.knowwe.kdom.manchester.types.DataPropertyExpression;
import de.knowwe.kdom.manchester.types.DataRangeExpression;
import de.knowwe.kdom.manchester.types.DataRestriction;
import de.knowwe.kdom.manchester.types.DatatypeRestriction;
import de.knowwe.kdom.manchester.types.Facet;
import de.knowwe.kdom.manchester.types.FacetRestriction;
import de.knowwe.kdom.manchester.types.Literal;
import de.knowwe.kdom.manchester.types.NonTerminalList;
import de.knowwe.kdom.manchester.types.OWLTermReferenceManchester;
import de.knowwe.kdom.manchester.types.ObjectPropertyExpression;
import de.knowwe.kdom.manchester.types.OneOfBracedCondition;
import de.knowwe.kdom.manchester.types.OnlyRestriction;
import de.knowwe.kdom.manchester.types.PropertyExpression;
import de.knowwe.kdom.manchester.types.Restriction;
import de.knowwe.kdom.manchester.types.SelfRestriction;
import de.knowwe.kdom.manchester.types.SomeRestriction;
import de.knowwe.kdom.manchester.types.ValueRestriction;
import de.knowwe.onte.owl.terminology.URIUtil;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 07.09.2011
 */
public class AxiomFactory {

	private static final OWLAPIConnector connector;
	private static final OWLDataFactory factory;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
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

	public static OWLAxiom getOWLAPIEntityDeclaration(String termIdentifier, Class<?> c) {
		OWLEntity entity = getOWLAPIEntity(termIdentifier, c);
		return getOWLAPIEntityDeclaration(entity);
	}

	/**
	 * Returns the a {@link OWLDeclarationAxiom} for a {@link OWLEntity} object.
	 * An entity could be a {@link OWLClass}, {@link OWLDataProperty},
	 * {@link OWLObjectProperty} or {@link OWLNamedIndividual}.
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
		else if (entity instanceof OWLDatatype) {
			return factory.getOWLDeclarationAxiom(entity);
		}
		else if (entity instanceof OWLNamedIndividual) {
			return factory.getOWLDeclarationAxiom(entity);
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
		return getOWLAPIEntity(section.getText(), c);
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

		PrefixManager pm;

		// check conflict: imported term and local term definition
		// if violation found, prior local term

		TermIdentifier termIdentifier = new TermIdentifier(concept);
		if (IncrementalCompiler.getInstance().getTerminology().isImportedObject(termIdentifier)
				&& !IncrementalCompiler.getInstance().getTerminology().isLocalObject(termIdentifier)) {
			// resolve import section, which section imported the term
			Section<? extends AbstractType> importFrame = ImportManager.resolveImportSection(termIdentifier);
			// resolve IRI from above section
			IRI iri = ImportedOntologyManager.getInstance().getImportIRIFromSection(importFrame);

			String iriStr = iri.toString();
			if (!iriStr.endsWith("/")) {
				iriStr += "#";
			}
			pm = new DefaultPrefixManager(iriStr);
		}
		else {
			pm = new DefaultPrefixManager(
					OWLAPIConnector.getGlobalInstance().getGlobalBaseIRI().toString());
		}

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
		else if (c.equals(OWLDatatype.class)) {
			return factory.getOWLDatatype(concept, pm);
		}
		// should never happen
		throw new Error("OWL API entity conversion for " + c + " not supported.");
	}

	public static OWLDeclarationAxiom getClassDeclaration(OWLEntity owlEntity) {
		return factory.getOWLDeclarationAxiom(owlEntity);
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

	private static OWLClassExpression handleDataExpressionTerminals(Section<?> section, Collection<Message> messages) {

		Section<DataRestriction> restrictionSection = Sections.findSuccessor(section,
				DataRestriction.class);
		DataRestriction r = restrictionSection.get();

		// A TerminalCondition can either be a SomeRestriction ...
		if (r.isSomeRestriction(restrictionSection)) {
			Section<SomeRestriction> some = r.getSomeRestriction(restrictionSection);
			Section<PropertyExpression> pe = some.get().getObjectProperty(some);
			Section<DataRangeExpression> dre = some.get().getDataRangeExpression(some);

			if (some.get().isDataPropertyExpression(some)) {
				OWLDataProperty p = (OWLDataProperty) getOWLAPIEntity(pe,
						OWLDataProperty.class);

				Map<OWLDataRange, Section<? extends Type>> exp = createDataRangeExpression(
						dre, messages);
				if (!exp.isEmpty()) {
					return factory.getOWLDataSomeValuesFrom(p,
							exp.keySet().iterator().next());
				}
			}
		}

		// ... or a OnlyRestriction ...
		if (r.isOnlyRestriction(restrictionSection)) {
			Section<OnlyRestriction> only = r.getOnlyRestriction(restrictionSection);
			Section<PropertyExpression> pe = only.get().getObjectProperty(only);
			Section<DataRangeExpression> dre = only.get().getDataRangeExpression(only);

			OWLDataProperty p = (OWLDataProperty) getOWLAPIEntity(pe, OWLDataProperty.class);

			Map<OWLDataRange, Section<? extends Type>> exp = createDataRangeExpression(dre,
					messages);
			if (!exp.isEmpty()) {
				return factory.getOWLDataAllValuesFrom(p,
						exp.keySet().iterator().next());
			}
		}

		// ... or a ValueRestriction ...
		if (r.isValueRestriction(restrictionSection)) {
			Section<ValueRestriction> value = r.getValueRestriction(restrictionSection);

			Section<PropertyExpression> pe = value.get().getObjectProperty(value);
			Section<ManchesterClassExpression> mce = value.get().getManchesterClassExpression(value);

			OWLDataProperty p = (OWLDataProperty) getOWLAPIEntity(pe, OWLDataProperty.class);
			return factory.getOWLDataHasValue(p, factory.getOWLLiteral(mce.getText()));
		}

		// ... or a CardinalityRestriction ...
		// ... or a MinRestriction|MaxRestriction|ExactlyRestriction ...
		if (r.isCardinalityRestriction(restrictionSection)) {

			Section<CardinalityRestriction> cardSection = r.getCardinalityRestriction(restrictionSection);
			CardinalityRestriction cr = cardSection.get();

			Section<PropertyExpression> pe = cr.getObjectProperty(cardSection);

			OWLDataProperty p = (OWLDataProperty) getOWLAPIEntity(pe, OWLDataProperty.class);
			Integer digit = cr.getDigit(cardSection);

			OWLDataRange exp = null;
			if (cr.hasOptionalDataRestriction(cardSection)) {
				// Section<DataRestriction> optional =
				// cr.getOptionalDataRestriction(cardSection);

				// TODO :
				// exp = handleDataExpressionTerminals(optional, messages);
			}

			if (cr.isMaxRestriction(cardSection)) {
				return factory.getOWLDataMaxCardinality(digit, p, exp);
			}
			if (cr.isMinRestriction(cardSection)) {
				return factory.getOWLDataMinCardinality(digit, p, exp);
			}
			if (cr.isExactlyRestriction(cardSection)) {
				return factory.getOWLDataExactCardinality(digit, p, exp);
			}
		}

		// ... or a OWLTermReference ...

		messages.add(Messages.syntaxError("Unknown DataTerminalCondition found!"));
		return null; // ... or 'This should never happen' :)
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
		if (restrictionSection == null) {
			return handleDataExpressionTerminals(section, messages);
		}
		else {
			Restriction r = restrictionSection.get();

			// A TerminalCondition can either be a SomeRestriction ...
			if (r.isSomeRestriction(restrictionSection)) {
				Section<SomeRestriction> some = r.getSomeRestriction(restrictionSection);
				Section<PropertyExpression> ope = some.get().getObjectProperty(some);
				Section<ManchesterClassExpression> mce = some.get().getManchesterClassExpression(
						some);

				if (some.get().isObjectPropertyExpression(some)) {
					OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
							OWLObjectProperty.class);
					Map<OWLClassExpression, Section<? extends Type>> exp = createDescriptionExpression(
							mce,
							messages);
					if (!exp.isEmpty()) {
						return factory.getOWLObjectSomeValuesFrom(p,
								exp.keySet().iterator().next());
					}
				}
			}

			// ... or a OnlyRestriction ...
			if (r.isOnlyRestriction(restrictionSection)) {
				Section<OnlyRestriction> only = r.getOnlyRestriction(restrictionSection);
				Section<PropertyExpression> ope = only.get().getObjectProperty(only);
				Section<ManchesterClassExpression> mce = only.get().getManchesterClassExpression(
						only);

				OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
						OWLObjectProperty.class);

				Map<OWLClassExpression, Section<? extends Type>> exp = createDescriptionExpression(
						mce,
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

				OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
						OWLObjectProperty.class);
				return factory.getOWLObjectHasSelf(p);
			}

			// ... or a ValueRestriction ...
			if (r.isValueRestriction(restrictionSection)) {
				Section<ValueRestriction> value = r.getValueRestriction(restrictionSection);

				Section<PropertyExpression> ope = value.get().getObjectProperty(value);
				Section<ManchesterClassExpression> mce = value.get().getManchesterClassExpression(
						value);

				// check with type of expressions
				if (value.get().isObjectPropertyExpression(value)) {
					OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
							OWLObjectProperty.class);
					OWLEntity entity = getOWLAPIEntity(mce, OWLIndividual.class);
					return factory.getOWLObjectHasValue(p, (OWLNamedIndividual) entity);
				}
				else if (value.get().isDataPropertyExpression(value)) {
					OWLDataProperty p = (OWLDataProperty) getOWLAPIEntity(ope,
							OWLDataProperty.class);
					return factory.getOWLDataHasValue(p,
							factory.getOWLLiteral(mce.getText()));
				}
			}

			// ... or a CardinalityRestriction ...
			// ... or a MinRestriction|MaxRestriction|ExactlyRestriction ...
			if (r.isCardinalityRestriction(restrictionSection)) {
				Section<CardinalityRestriction> cardSection = r.getCardinalityRestriction(restrictionSection);
				CardinalityRestriction cr = cardSection.get();

				Section<PropertyExpression> ope = cr.getObjectProperty(cardSection);
				OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
						OWLObjectProperty.class);
				Integer digit = cr.getDigit(cardSection);

				OWLClassExpression exp = null;
				if (cr.hasOptionalRestriction(cardSection)) {
					Section<Restriction> optional = cr.getOptionalRestriction(cardSection);
					if (optional.get().isTermReference(optional)) {
						exp = handleTerminals(cardSection, messages);
					}
					else {
						Section<ManchesterClassExpression> mce = Sections.findSuccessor(
								cardSection,
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
		}
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
	 * 
	 * 
	 * @created 04.10.2011
	 * @param Section<ManchesterClassExpression> section
	 * @return
	 */
	public static Set<OWLDataProperty> createDataPropertyExpression(Section<ManchesterClassExpression> section) {
		ManchesterClassExpression type = section.get();
		Set<OWLDataProperty> exp = new HashSet<OWLDataProperty>();

		// A ObjectProeprtyExpression may either be NonTerminalList ...
		if (type.isNonTerminalList(section)) {
			List<Section<NonTerminalList>> xjunctions = type.getNonTerminalListElements(section);

			Set<OWLDataProperty> set = new HashSet<OWLDataProperty>();

			for (Section<NonTerminalList> child : xjunctions) {
				set.addAll(createDataPropertyExpression(Sections.findSuccessor(child,
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
						OWLDataProperty.class);
				exp.add((OWLDataProperty) entity);
			}
		}
		return exp;
	}

	/**
	 * Create description axioms from the elements found in the KDOM. This is
	 * rather a complex issue, so be careful when changing the following lines.
	 * The Input is a {@link DataRangeExpression} that contains all the
	 * necessary information about its children, e.g. Conjuncts, Disjuncts,
	 * ListElements or TerminalConditions.
	 * 
	 * @created 08.09.2011
	 * @param Section<ManchesterClassExpression> section
	 * @return A set of {@link OWLDataRange} for further processing.
	 */
	public static Map<OWLDataRange, Section<? extends Type>> createDataRangeExpression(Section<DataRangeExpression> section, Collection<Message> messages) {

		DataRangeExpression type = section.get();
		Map<OWLDataRange, Section<? extends Type>> exp = new HashMap<OWLDataRange, Section<? extends Type>>();

		// A description may either be NonTerminalList ...
		if (type.isNonTerminalList(section)) {
			List<Section<NonTerminalList>> xjunctions = type.getNonTerminalListElements(section);
			Map<OWLDataRange, Section<? extends Type>> set = new HashMap<OWLDataRange, Section<? extends Type>>();
			for (Section<NonTerminalList> child : xjunctions) {
				set.putAll(createDataRangeExpression(Sections.findSuccessor(child,
						DataRangeExpression.class), messages));
			}
			exp.putAll(set);
			return exp;
		}

		// ... or a OneOfCurlyBracket ...
		if (type.isOneOfCurlyBracket(section)) {
			Map<OWLObject, Section<? extends Type>> set = new HashMap<OWLObject, Section<? extends Type>>();
			Section<OneOfBracedCondition> one = type.getOneOfCurlyBracket(section);
			Section<DataRangeExpression> mce = Sections.findSuccessor(one,
					DataRangeExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("OneOfCurlyBracket is empty!"));
				return null;
			}

			set.putAll(createDataRangeExpression(mce, messages));

			Set<OWLLiteral> individuals = new HashSet<OWLLiteral>();
			for (OWLObject c : set.keySet()) {
				// only string or complete iri?
				individuals.add(factory.getOWLLiteral(c.toString()));
			}
			exp.put(factory.getOWLDataOneOf(individuals), mce);
			return exp;
		}

		// ... or a BracedCondition ...
		if (type.isBraced(section)) {
			Section<? extends NonTerminalCondition> braced = type.getBraced(section);
			Section<DataRangeExpression> mce = Sections.findSuccessor(braced,
					DataRangeExpression.class);

			if (mce == null) {
				messages.add(Messages.syntaxError("Bracket is empty!"));
				return null;
			}

			exp.putAll(createDataRangeExpression(mce, messages));
			return exp;
		}

		// ... or a Conjunct ...
		if (type.isConjunction(section)) {
			List<Section<? extends NonTerminalCondition>> xjunctions = new ArrayList<Section<? extends NonTerminalCondition>>();
			xjunctions = type.getConjuncts(section);
			Map<OWLDataRange, Section<? extends Type>> set = new HashMap<OWLDataRange, Section<? extends Type>>();

			for (Section<?> child : xjunctions) {
				set.putAll(createDataRangeExpression(Sections.findSuccessor(child,
						DataRangeExpression.class), messages));
			}
			if (!set.isEmpty()) {
				Set<OWLDataRange> tmp = new HashSet<OWLDataRange>();
				tmp.addAll(set.keySet());
				exp.put(factory.getOWLDataIntersectionOf(tmp), section);
			}
			return exp;
		}

		// ... or a Disjuncts ...
		if (type.isDisjunction(section)) {
			List<Section<? extends NonTerminalCondition>> xjunctions = new ArrayList<Section<? extends NonTerminalCondition>>();
			xjunctions = type.getDisjuncts(section);
			Map<OWLDataRange, Section<? extends Type>> set = new HashMap<OWLDataRange, Section<? extends Type>>();
			for (Section<?> child : xjunctions) {
				set.putAll(createDataRangeExpression(Sections.findSuccessor(child,
						DataRangeExpression.class), messages));
			}
			if (!set.isEmpty()) {
				Set<OWLDataRange> tmp = new HashSet<OWLDataRange>();
				tmp.addAll(set.keySet());
				exp.put(factory.getOWLDataUnionOf(tmp), section);
			}
			return exp;
		}

		// ... or a Negation ...
		if (type.isNegation(section)) {
			Section<?> neg = type.getNegation(section);

			Map<OWLObject, Section<? extends Type>> set = new HashMap<OWLObject, Section<? extends Type>>();
			set.putAll(createDataRangeExpression(Sections.findSuccessor(neg,
					DataRangeExpression.class), messages));
			if (set.size() > 0) {
				exp.put(factory.getOWLDataComplementOf(
						(OWLDataRange) set.keySet().iterator().next()), section);
			}
			return exp;
		}

		// ... or simply a TerminalCondition
		if (type.isTerminal(section)) {
			Section<?> terminal = type.getTerminal(section);
			OWLDataRange oce = handleDataTerminals(terminal, messages);
			if (oce != null) {
				exp.put(oce, section);
			}
		}
		return exp;
	}

	/**
	 * Handles all the possible {@link TerminalCondition} of the
	 * {@link DataRangeExpression}. If you add a new {@link TerminalCondition},
	 * please update the following lines accordingly.
	 * 
	 * @created 30.09.2011
	 * @param Section<? extends TerminalCondition> section
	 * @return OWLDataRange
	 */
	private static OWLDataRange handleDataTerminals(Section<?> section, Collection<Message> messages) {

		Section<DatatypeRestriction> restrictionSection = Sections.findSuccessor(section,
				DatatypeRestriction.class);
		DatatypeRestriction r = restrictionSection.get();

		// A TerminalCondition can either be a PredefinedDatatype (XML) ...
		if (r.isPredefinedDataType(restrictionSection)) {

			Set<OWLFacetRestriction> owlFacetRestrictions = new HashSet<OWLFacetRestriction>();
			Section<BraceElement> brace = Sections.findSuccessor(restrictionSection,
					BraceElement.class);
			if (brace != null) { // optional facets found --> handling

				List<Section<FacetRestriction>> facets = Sections.findSuccessorsOfType(brace,
						FacetRestriction.class);
				for (Section<FacetRestriction> sec : facets) {
					OWLFacetRestriction o = createFacet(sec);
					if (o != null) {
						owlFacetRestrictions.add(o);
					}
				}
			}

			OWLDatatype dt = null;

			if (r.isIntegerDataType(restrictionSection)) {
				dt = factory.getIntegerOWLDatatype();
			}
			if (r.isDoubleDataType(restrictionSection) || r.isDecimalDataType(restrictionSection)) {
				dt = factory.getDoubleOWLDatatype();
			}
			if (r.isBooleanDataType(restrictionSection)) {
				dt = factory.getBooleanOWLDatatype();
			}
			if (r.isFloatDataType(restrictionSection)) {
				dt = factory.getFloatOWLDatatype();
			}
			if (r.isStringDataType(restrictionSection)) {
				dt = factory.getRDFPlainLiteral();
			}

			if (!owlFacetRestrictions.isEmpty()) {
				return factory.getOWLDatatypeRestriction(dt, owlFacetRestrictions);
			}
			else {
				return factory.getOWLDatatypeRestriction(dt);
			}

		}

		// ... or a facet ...
		// if (r.isFacet(restrictionSection)) {
		// Map<Section<? extends AbstractType>, Section<Literal>>
		// facetRestriction = r.getFacets(restrictionSection);
		// }

		// ... or a literal.
		if (r.isLiteral(restrictionSection)) {
			Section<Literal> literal = r.getLiteral(restrictionSection);

			if (literal.get().isFloatingPoint(literal)) {
				// OWLLiteral l =
				// factory.getOWLLiteral(Float.valueOf(literal.getOriginalText()));
				// factory.getOWLDatatypeRestriction(factory.getFloatOWLDatatype(),
				// facetRestrictions)
				// return l;
			}

		}

		messages.add(Messages.syntaxError("Unknown TerminalCondition found!"));
		return null; // ... or 'This should never happen' :)
	}

	private static OWLFacetRestriction createFacet(Section<FacetRestriction> facet) {

		FacetRestriction t = facet.get();

		if (t.hasFacet(facet) && t.hasLiteral(facet)) {

			Section<Facet> facetSection = t.getFacet(facet);
			Facet f = facetSection.get();

			String literal = t.getLiteral(facet).getText();
			OWLDatatype type = resolveLiteralType(t.getLiteral(facet));

			if (f.isLength(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.LENGTH,
						factory.getOWLLiteral(literal, type));
			}
			if (f.isMaxLength(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MAX_LENGTH, factory.getOWLLiteral(
						literal,
						type));
			}
			if (f.isMinLength(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MIN_LENGTH, factory.getOWLLiteral(
						literal,
						type));
			}
			if (f.isMaxInclusive(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MAX_INCLUSIVE,
						factory.getOWLLiteral(literal,
								type));
			}
			if (f.isMinExclusive(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MIN_EXCLUSIVE,
						factory.getOWLLiteral(literal,
								type));
			}
			if (f.isMaxExclusive(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MAX_EXCLUSIVE,
						factory.getOWLLiteral(literal,
								type));
			}
			if (f.isMinInclusive(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.MIN_INCLUSIVE,
						factory.getOWLLiteral(literal,
								type));
			}
			if (f.isLangRange(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.LANG_RANGE, factory.getOWLLiteral(
						literal,
						type));
			}
			if (f.isPattern(facetSection)) {
				return factory.getOWLFacetRestriction(OWLFacet.PATTERN, factory.getOWLLiteral(
						literal,
						type));
			}
		}
		return null;
	}

	private static OWLDatatype resolveLiteralType(Section<Literal> section) {
		Literal l = section.get();

		if (l.isFloatingPoint(section)) {
			return factory.getFloatOWLDatatype();
		}
		else if (l.isDecimal(section)) {
			return factory.getDoubleOWLDatatype();
		}
		else if (l.isInteger(section)) {
			return factory.getIntegerOWLDatatype();
		}
		return factory.getRDFPlainLiteral();
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

	public static OWLAxiom createOWLDisjointUnionOf(OWLClass clazz, Set<OWLClassExpression> expression) {
		return factory.getOWLDisjointUnionAxiom(clazz, expression);
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
		String str = section.getText();

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

	/**
	 * 
	 * 
	 * @created 28.09.2011
	 * @param section
	 * @param p
	 * @return
	 */
	public static OWLAxiom createDataPropertyCharacteristics(Section<?> section, OWLDataProperty p, Collection<Message> messages) {
		String str = section.getText();

		if (str.equals(ManchesterSyntaxKeywords.FUNCTIONAL.getKeyword())) {
			return factory.getOWLFunctionalDataPropertyAxiom(p);
		}
		messages.add(Messages.syntaxError("Characteristic not support! Only FUNCTIONAL possible!"));
		return null;
	}

	public static OWLAxiom createObjectPropertyRange(OWLObjectProperty p, OWLClassExpression exp) {
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

	public static OWLAxiom createEquivalentTo(OWLObjectProperty p1, OWLObjectProperty p2) {
		return factory.getOWLEquivalentObjectPropertiesAxiom(p1, p2);
	}

	public static OWLAxiom createDataPropertyDomain(OWLDataProperty p, OWLClassExpression exp) {
		return factory.getOWLDataPropertyDomainAxiom(p, exp);
	}

	public static OWLAxiom createDataPropertyRange(OWLDataProperty p, OWLDataRange range) {
		return factory.getOWLDataPropertyRangeAxiom(p, range);
	}

	public static OWLAxiom createDataSubPropertyOf(OWLDataProperty p, OWLDataProperty subProperty) {
		return factory.getOWLSubDataPropertyOfAxiom(p, subProperty);
	}

	public static OWLAxiom createDataPropertyDisjointWith(OWLDataProperty p1, OWLDataProperty p2) {
		return factory.getOWLDisjointDataPropertiesAxiom(p1, p2);
	}

	public static OWLAxiom createDataPropertyEquivalentTo(OWLDataProperty p1, OWLDataProperty p2) {
		return factory.getOWLEquivalentDataPropertiesAxiom(p1, p2);
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
		Section<PropertyExpression> ope = Sections.findSuccessor(section,
				PropertyExpression.class);
		Section<OWLTermReferenceManchester> ref = Sections.findSuccessor(section,
				OWLTermReferenceManchester.class);
		Section<Literal> lit = Sections.findSuccessor(section, Literal.class);

		if (ope != null) { // object property fact

			OWLObjectProperty p = (OWLObjectProperty) getOWLAPIEntity(ope,
					OWLObjectProperty.class);

			// check type of property and handle accordingly
			boolean isDataProperty = OWLAPIConnector.getGlobalInstance().getOntology().containsDataPropertyInSignature(
					p.getIRI(), true);

			if (isDataProperty) {
				OWLDataProperty pTmp = (OWLDataProperty) getOWLAPIEntity(ope,
						OWLDataProperty.class);

				String object = (lit != null) ? lit.getText() : (ref != null)
						? ref.getText()
						: "";
				object = object.replaceAll("\"", "");

				OWLDatatype dt = resolveLiteralType(lit);

				return factory.getOWLDataPropertyAssertionAxiom(pTmp, i,
						factory.getOWLLiteral(object, dt));
			}
			else if (ref != null) {
				OWLNamedIndividual ind = (OWLNamedIndividual) getOWLAPIEntity(ref,
						OWLIndividual.class);
				return factory.getOWLObjectPropertyAssertionAxiom(p, i, ind);
			}
			else if (lit != null && isDataProperty == false) {
				messages.add(Messages.syntaxError("ObjectProperty attached with literal value!"));
			}
		}
		else {
			messages.add(Messages.syntaxError("Data-/ ObjectProperty or Termreference missing!"));
		}
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
			term = annotationType.getTerm(section).getText();

			// check for optional tags (language, data type)
			if (annotationType.hasLanguageTag(section)) {
				tag = annotationType.getLanguage(section).getText();
			}
			else if (annotationType.hasDatatypeTag(section)) {
				tag = annotationType.getDatatype(section).getText();
			}
			return createOWLAnnotation(annotationIRI, term, tag);
		}
		messages.add(Messages.syntaxError(
				"undefined annotation type found, please specify {label, comment}!"));
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

		Set<OWLClass> parts = new HashSet<OWLClass>();

		for (Section<OWLTermReferenceManchester> r : references) {
			OWLClass clazz = (OWLClass) getOWLAPIEntity(r, OWLClass.class);
			parts.add(clazz);
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

	/**
	 * 
	 * 
	 * @created 24.09.2011
	 * @param section
	 * @return
	 */
	public static OWLDatatype convertToOWLDatatype(Section<? extends AbstractType> section) {

		String possibleType = section.getText().trim();

		// Datatype ::= datatypeIRI | 'integer' | 'decimal' | 'float' | 'string'
		if (possibleType.equals("integer") || possibleType.equals("int")) {
			return factory.getIntegerOWLDatatype();
		}
		else if (possibleType.equals("boolean")) {
			return factory.getBooleanOWLDatatype();
		}
		else if (possibleType.equals("double") || possibleType.equals("decimal")) {
			return factory.getDoubleOWLDatatype();
		}
		else if (possibleType.equals("float")) {
			return factory.getFloatOWLDatatype();
		}
		return factory.getOWLDatatype(IRI.create(possibleType));
	}

	/**
	 * Defines an custom datatype for the ontology.
	 * 
	 * @created 10.01.2012
	 * @param OWLDatatype d
	 * @param OWLDataRange r
	 * @return
	 */
	public static OWLAxiom createOWLDataTypeEquivalentTo(OWLDatatype d, OWLDataRange r) {
		return factory.getOWLDatatypeDefinitionAxiom(d, r);
	}
}
