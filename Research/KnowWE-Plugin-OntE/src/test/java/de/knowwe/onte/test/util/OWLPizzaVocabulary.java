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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import de.knowwe.owlapi.OWLAPIConnector;

/**
 * The vocabulary used in the testing of the Pizza ontology.
 *
 * @see http://www.co-ode.org/ontologies/
 *
 * @author Stefan Mark
 * @created 23.09.2011
 */
public class OWLPizzaVocabulary {

	public static final String ARTICLENAME = "OntE-Test-Pizza-Article";

	private static final OWLAPIConnector connector;
	private static final OWLDataFactory factory;
	private static final PrefixManager pm;

	static {
		connector = OWLAPIConnector.getGlobalInstance();
		factory = connector.getManager().getOWLDataFactory();
		pm = new DefaultPrefixManager(connector.getGlobalBaseIRI().toString());
	}


	/* Individuals */
	public static OWLNamedIndividual ITALY = factory.getOWLNamedIndividual("Italy", pm);
	public static OWLNamedIndividual GERMANY = factory.getOWLNamedIndividual("Germany", pm);
	public static OWLNamedIndividual AMERICA = factory.getOWLNamedIndividual("America", pm);
	public static OWLNamedIndividual ENGLAND = factory.getOWLNamedIndividual("England", pm);
	public static OWLNamedIndividual FRANCE = factory.getOWLNamedIndividual("France", pm);
	public static OWLNamedIndividual COUNTRY_IND = factory.getOWLNamedIndividual("Country", pm);

	/* Classes */
	public static OWLClass THING = factory.getOWLThing();
	public static OWLClass NOTHING = factory.getOWLNothing();
	public static OWLClass COUNTRY = factory.getOWLClass("Country", pm);
	public static OWLClass INTERESTING_PIZZA = factory.getOWLClass("InterestingPizza", pm);
	public static OWLClass PIZZABASE = factory.getOWLClass("PizzaBase", pm);
	public static OWLClass PIZZA = factory.getOWLClass("Pizza", pm);
	public static OWLClass NAMEDPIZZA = factory.getOWLClass("NamedPizza", pm);
	public static OWLClass ICECREAM = factory.getOWLClass("IceCream", pm);
	public static OWLClass FOOD = factory.getOWLClass("Food", pm);

	public static OWLClass ITALY_CLAZZ = factory.getOWLClass("Italy", pm);
	public static OWLClass GERMANY_CLAZZ = factory.getOWLClass("Germany", pm);

	/* Properties */
	public static OWLObjectProperty HASBASE = factory.getOWLObjectProperty("hasBase", pm);
	public static OWLObjectProperty HASSPICINESS = factory.getOWLObjectProperty("hasSpiciness", pm);
	public static OWLObjectProperty HASINGREDIENT = factory.getOWLObjectProperty("hasIngredient",
			pm);
	public static OWLObjectProperty ISBASEOF = factory.getOWLObjectProperty("isBaseOf", pm);
	public static OWLObjectProperty ISINGREDIENTOF = factory.getOWLObjectProperty(
			":isIngredientOf", pm);
	public static OWLObjectProperty HASTOPPING = factory.getOWLObjectProperty("hasTopping", pm);

	public static Set<OWLNamedIndividual> DIFF_IND;
	static {
		DIFF_IND = new HashSet<OWLNamedIndividual>();
		DIFF_IND.add(ITALY);
		DIFF_IND.add(GERMANY);
		DIFF_IND.add(AMERICA);
		DIFF_IND.add(FRANCE);
		DIFF_IND.add(ENGLAND);
	}

	/* Individuals descriptions */
	public static OWLAxiom DIFFERENT_IND = factory.getOWLDifferentIndividualsAxiom(DIFF_IND);

	/* Properties descriptions */
	public static OWLAxiom HASBASE_SUB = factory.getOWLSubObjectPropertyOfAxiom(
			HASBASE, HASINGREDIENT);
	public static OWLAxiom HASBASE_FUN = factory.getOWLFunctionalObjectPropertyAxiom(HASBASE);
	public static OWLAxiom HASBASE_INV = factory.getOWLInverseFunctionalObjectPropertyAxiom(HASBASE);
	public static OWLAxiom HASBASE_DOMAIN = factory.getOWLObjectPropertyDomainAxiom(HASBASE, PIZZA);
	public static OWLAxiom HASBASE_RANGE = factory.getOWLObjectPropertyRangeAxiom(HASBASE,
			PIZZABASE);

}
