/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.onte.test.util;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 * The vocabulary used in the testing of the Generations Ontology.
 *
 * @see http://www.co-ode.org/ontologies/
 * @author Stefan Mark
 * @created 23.09.2011
 */
public class OWLGenerationVocabulary {

	public static final String ARTICLENAME = "OntE-Test-Generation-Article";

	private static final OWLAPIConnector connector;
	private static final OWLDataFactory factory;
	private static final PrefixManager pm;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		pm = new DefaultPrefixManager(connector.getGlobalBaseIRI().toString());
	}


	/* Individuals */
	public static OWLNamedIndividual MATT = factory.getOWLNamedIndividual("Matt", pm);
	public static OWLNamedIndividual GEMMA = factory.getOWLNamedIndividual("Gemma", pm);
	public static OWLNamedIndividual MATTHEW = factory.getOWLNamedIndividual("Matthew", pm);
	public static OWLNamedIndividual PETER = factory.getOWLNamedIndividual("Peter", pm);
	public static OWLNamedIndividual WILLIAM = factory.getOWLNamedIndividual("William", pm);
	public static OWLNamedIndividual MALESEX = factory.getOWLNamedIndividual("MaleSex", pm);
	public static OWLNamedIndividual FEMALESEX = factory.getOWLNamedIndividual("FemaleSex", pm);

	/* Classes */
	public static OWLClass MAN = factory.getOWLClass("Man", pm);
	public static OWLClass WOMAN = factory.getOWLClass("Woman", pm);
	public static OWLClass MOTHER = factory.getOWLClass("Mother", pm);
	public static OWLClass PARENT = factory.getOWLClass("Parent", pm);
	public static OWLClass FEMALE = factory.getOWLClass("Female", pm);
	public static OWLClass GRANDFATHER = factory.getOWLClass("Grandfather", pm);
	public static OWLClass BROTHER = factory.getOWLClass("Brother", pm);
	public static OWLClass FATHER = factory.getOWLClass("Father", pm);
	public static OWLClass MALE = factory.getOWLClass("Male", pm);
	public static OWLClass SEX = factory.getOWLClass("Sex", pm);
	public static OWLClass GRANDMOTHER = factory.getOWLClass("GrandMother", pm);

	public static OWLClass SON = factory.getOWLClass("Son", pm);
	public static OWLClass PERSON = factory.getOWLClass("Person", pm);
	public static OWLClass GRANDPARENT = factory.getOWLClass("GrandParent", pm);
	public static OWLClass DAUGHTER = factory.getOWLClass("Daughter", pm);

	public static OWLClass SISTER = factory.getOWLClass("Sister", pm);
	public static OWLClass OFFSPRING = factory.getOWLClass("OffSpring", pm);
	public static OWLClass SIBLING = factory.getOWLClass("Sibling", pm);

	/* Properties */
	public static OWLObjectProperty HASSIBLING = factory.getOWLObjectProperty("hasSibling", pm);
	public static OWLObjectProperty HASCHILD = factory.getOWLObjectProperty("hasChild", pm);
	public static OWLObjectProperty HASSEX = factory.getOWLObjectProperty("hasSex", pm);
	public static OWLObjectProperty HASPARENT = factory.getOWLObjectProperty("hasParent", pm);

	/* Individuals descriptions */
	public static OWLAxiom MALESEX_TYPE = factory.getOWLClassAssertionAxiom(SEX, MALESEX);
	public static OWLAxiom GEMMA_TYPE = factory.getOWLClassAssertionAxiom(PERSON, GEMMA);
	public static OWLAxiom GEMMA_FACTS = factory.getOWLObjectPropertyAssertionAxiom(HASSEX, GEMMA,
			FEMALESEX);
	public static OWLAxiom MATT_TYPE = factory.getOWLClassAssertionAxiom(PERSON, MATT);
	public static OWLAxiom MATT_SAMEAS = factory.getOWLSameIndividualAxiom(MATT, MATTHEW);
	public static OWLAxiom MATT_FACTS = factory.getOWLObjectPropertyAssertionAxiom(HASPARENT, MATT,
			PETER);

	/* Properties descriptions */
	public static OWLAxiom HASSIBLING_SYMMETRIC = factory.getOWLSymmetricObjectPropertyAxiom(HASSIBLING);
	public static OWLAxiom HASSEX_FUNCTIONAL = factory.getOWLFunctionalObjectPropertyAxiom(HASSEX);
	public static OWLAxiom HASSEX_SYMMETRIC = factory.getOWLSymmetricObjectPropertyAxiom(HASSEX);

	public static OWLAxiom HASSEX_RANGE = factory.getOWLObjectPropertyRangeAxiom(HASSEX, SEX);
	public static OWLAxiom HASPARENT_INVERSE = factory.getOWLInverseObjectPropertiesAxiom(
			HASPARENT, HASCHILD);
	public static OWLAxiom HASCHILD_INVERSE = factory.getOWLInverseObjectPropertiesAxiom(HASCHILD,
			HASPARENT);
}
