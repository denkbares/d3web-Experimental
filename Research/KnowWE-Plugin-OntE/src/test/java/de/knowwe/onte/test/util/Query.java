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

/**
 * 
 * SPARQL-Queries used in the tests.
 * 
 * @author Sebastian Furth
 * @created Apr 1, 2011
 */
public class Query {

	/* ------------------ CLASS AND INDIVIDUAL QUERIES ------------------ */

	/** Tests: [def Alexander the Great type:: Person] */
	public static String ALEXANDERPERSON =
			"ASK { <" + Vocabulary.ALEXANDER + "> rdf:type <" + Vocabulary.PERSON + "> . }";

	/** Tests: [def Person isA:: Class] */
	public static String PERSONCLASS =
			"ASK { <" + Vocabulary.PERSON + "> rdf:type rdfs:Class . }";

	/** Tests: [Alexander the Great deathPlace:: Babylon] */
	public static String ALEXANDERDEATHPLACE =
			"ASK { <" + Vocabulary.ALEXANDER + "> <" + Vocabulary.DEATHPLACE + "> <"
					+ Vocabulary.BABYLON + "> . }";

	/* ------------------ OBJECTPROPERTY QUERIES ------------------ */

	/** Tests: [def deathPlace type:: ObjectProperty] */
	public static String DEATHPLACE =
			"ASK { <" + Vocabulary.DEATHPLACE + "> rdf:type owl:ObjectProperty . }";

	/** Tests: [deathPlace domain:: Person] */
	public static String DEATHPLACEDOMAIN =
			"ASK { <" + Vocabulary.DEATHPLACE + "> rdfs:domain <" + Vocabulary.PERSON + "> . }";

	/** Tests: [deathPlace range:: Location] */
	public static String DEATHPLACERANGE =
			"ASK { <" + Vocabulary.DEATHPLACE + "> rdfs:range <" + Vocabulary.LOCATION + "> . }";

	/** Tests: [isRelatedTo type:: ObjectProperty] */
	public static String ISRELATEDTO =
			"ASK { <" + Vocabulary.ISRELATEDTO + "> rdf:type owl:ObjectProperty . }";

	/** Tests: [deathPlace subPropertyOf:: isRelatedTo] */
	public static String DEATHPLACESUBPROPERTY =
			"ASK { <" + Vocabulary.DEATHPLACE + "> rdfs:subPropertyOf <" + Vocabulary.ISRELATEDTO
					+ "> . }";

	/* ------------------ DATATYPEPROPERTY QUERIES ------------------ */

	/** Tests: [def yearOfDeath type:: DatatypeProperty] */
	public static String YEAROFDEATH =
			"ASK { <" + Vocabulary.YEAROFDEATH + "> rdf:type owl:DatatypeProperty . }";

	/** Tests: [Alexander the Great yearOfDeath:: 323bc] */
	public static String ALEXANDERYEAROFDEATH =
			"ASK { <" + Vocabulary.ALEXANDER + "> <" + Vocabulary.YEAROFDEATH + "> \"323bc\" . }";

	/* ------------------ THIS QUERIES ------------------ */

	/** Tests: [def this type:: Historical Essay] */
	public static String THISTYPE =
			"ASK { <" + Vocabulary.THIS + "> rdf:type <" + Vocabulary.HISTORICALESSAY + "> . }";

	/** Tests: [this describes:: Alexander the Great] */
	public static String THISDESCRIBES =
			"ASK { <" + Vocabulary.THIS + "> <" + Vocabulary.DESCRIBES + "> <"
					+ Vocabulary.ALEXANDER + "> . }";

	/* ------------------ CLASS HIERARCHY QUERIES ------------------ */

	public static String CONCEPTOFHISTORYPERSON =
			"ASK { <" + Vocabulary.PERSON + "> rdfs:subClassOf <" + Vocabulary.CONCEPTOFHISTORY
					+ "> . }";

	public static String CONCEPTOFHISTORYLOCATION =
			"ASK { <" + Vocabulary.LOCATION + "> rdfs:subClassOf <" + Vocabulary.CONCEPTOFHISTORY
					+ "> . }";

	public static String PERSONKING =
			"ASK { <" + Vocabulary.KING + "> rdfs:subClassOf <" + Vocabulary.PERSON + "> . }";

	public static String LOCATIONISLAND =
			"ASK { <" + Vocabulary.ISLAND + "> rdfs:subClassOf <" + Vocabulary.LOCATION + "> . }";

	public static String LOCATIONCITY =
			"ASK { <" + Vocabulary.CITY + "> rdfs:subClassOf <" + Vocabulary.LOCATION + "> . }";

	/*******************************************************************
	 * ------------------ QUERIES FOR UPDATED ARTICLE ------------------
	 *******************************************************************/
	public static class Update {

		/** Tests: [def Alexander the Little type:: Person] */
		public static String ALEXANDERLITTLEPERSON =
				"ASK { <" + Vocabulary.ALEXANDERLITTLE + "> rdf:type <" + Vocabulary.PERSON
						+ "> . }";

		/** Tests: [Alexander the Little deathPlace:: Babylon] */
		public static String ALEXANDERLITTLEDEATHPLACE =
				"ASK { <" + Vocabulary.ALEXANDERLITTLE + "> <" + Vocabulary.DEATHPLACE + "> <"
						+ Vocabulary.BABYLON + "> . }";

		/** Tests: [deathPlace range:: Person] */
		public static String DEATHPLACERANGE =
				"ASK { <" + Vocabulary.DEATHPLACE + "> rdfs:range <" + Vocabulary.PERSON + "> . }";

		/** Tests: [deathPlace domain:: Location] */
		public static String DEATHPLACEDOMAIN =
				"ASK { <" + Vocabulary.DEATHPLACE + "> rdfs:domain <" + Vocabulary.LOCATION
						+ "> . }";

		/** Tests: [def deathPlace type:: DatatypeProperty] */
		public static String DEATHPLACE =
				"ASK { <" + Vocabulary.DEATHPLACE + "> rdf:type owl:DatatypeProperty . }";

		/** Tests: [Alexander the Little yearOfDeath:: 2011] */
		public static String ALEXANDERYEAROFDEATH =
				"ASK { <" + Vocabulary.ALEXANDERLITTLE + "> <" + Vocabulary.YEAROFDEATH
						+ "> \"2011\" . }";

		/** Tests: [def this type:: Person] */
		public static String THISTYPE =
				"ASK { <" + Vocabulary.THIS + "> rdf:type <" + Vocabulary.PERSON + "> . }";

		/** Tests: [this describes:: Alexander the Great] */
		public static String THISDESCRIBES =
				"ASK { <" + Vocabulary.THIS + "> <" + Vocabulary.DESCRIBES + "> <"
						+ Vocabulary.ALEXANDERLITTLE + "> . }";

		public static String PERSONQUEEN =
				"ASK { <" + Vocabulary.QUEEN + "> rdfs:subClassOf <" + Vocabulary.PERSON + "> . }";

	}

}
