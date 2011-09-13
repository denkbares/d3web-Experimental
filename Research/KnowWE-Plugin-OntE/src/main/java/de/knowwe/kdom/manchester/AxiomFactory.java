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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.TerminalCondition;
import de.knowwe.kdom.manchester.frames.clazz.ClassFrame;
import de.knowwe.kdom.manchester.frames.individual.IndividualFrame;
import de.knowwe.kdom.manchester.frames.objectproperty.CharacteristicTypes;
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

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		pm = new DefaultPrefixManager(Rdf2GoCore.basens);
		ontology = connector.getOntology();
	}

	public static OWLClass createClass(Section<?> section) {
		Section<?> s = Sections.findSuccessor(section, ClassFrame.OWLClass.class);
		return factory.getOWLClass(":" + s.getOriginalText(), pm);
	}

	public static OWLAxiom createOWLClass(OWLClass clazz) {
		return factory.getOWLDeclarationAxiom(clazz);
	}

	/**
	 * Create subClassOf restrictions axioms from the elements found in the
	 * KDOM. Input is a {@link Restriction} node that either contains an
	 * {@link OWLTermReferenceManchester} or another {@link Restriction}.
	 *
	 * @created 08.09.2011
	 * @param Section section The first restriction node within a
	 *        {@link ListItem}
	 * @param OWLClass father The {@link OWLClass} the restriction belongs to.
	 * @return
	 */
	public static OWLAxiom createSubClassOf(Section<?> section, OWLClass father) {

		Type childOfType = section.getChildren().get(0).get();
		if (childOfType instanceof OWLTermReferenceManchester) {
			// simple reference detected
			return factory.getOWLSubClassOfAxiom(father, factory.getOWLClass(":"
					+ section.getOriginalText(), pm));
		}
		else if (childOfType instanceof SomeRestriction || childOfType instanceof OnlyRestriction
				|| childOfType instanceof ValueRestriction) {
			// restriction found NOTE: can itself contain restrictions !!!
			Section<?> ope = Sections.findSuccessor(section, ObjectPropertyExpression.class);
			Section<?> mce = Sections.findSuccessor(section, ManchesterClassExpression.class);
			OWLObjectProperty objectProperty = factory.getOWLObjectProperty(":"
					+ ope.getOriginalText(), pm);

			OWLClass clazz = null;
			OWLClassExpression exp = null;

			if (mce.getChildren().get(0).get() instanceof TerminalCondition) {
				// simple restriction, get OWLTermReference and go on
				clazz = factory.getOWLClass(":" + mce.getChildren().get(0).getOriginalText(), pm);
			}
			else {
				// complex restriction with brackets etc.
			}

			if (clazz != null) {
				if (childOfType instanceof SomeRestriction) {
					exp = factory.getOWLObjectSomeValuesFrom(objectProperty, clazz);
				}
				else if (childOfType instanceof OnlyRestriction) {
					exp = factory.getOWLObjectAllValuesFrom(objectProperty, clazz);
				}
				// else if(t instanceof ValueRestriction) {
				//
				// }
				if (exp != null) {
					// return factory.getOWLSubClassOfAxiom(father, exp);
				}
			}
		}
		return null;
	}

	public static OWLAxiom createDisjointWith(Section<?> section, OWLClass father) {
		if (section.get() instanceof OWLTermReferenceManchester) {
			OWLClass c = factory.getOWLClass(":" + section.getOriginalText(), pm);
			return factory.getOWLDisjointClassesAxiom(c, father);
		}
		return null;
	}

	public static OWLAxiom createEquivalentTo(Section<?> section, OWLClass father) {
		if (section.get() instanceof OWLTermReferenceManchester) {
			OWLClass c = factory.getOWLClass(":" + section.getOriginalText(), pm);
			return factory.getOWLEquivalentClassesAxiom(c, father);
		}
		return null;
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
