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
package de.knowwe.compile.test.util;

import org.ontoware.rdf2go.model.node.URI;

/**
 * 
 * SPARQL-Queries used in the tests.
 * 
 * @author Sebastian Furth
 * @created June 17, 2011
 */
public class Query {

	public static String createQuery(URI subject, URI property, URI object) {
		return "ASK { <" + subject + "> <" + property + "> <" + object + "> . }";
	}

	/* ------------------ INITIAL QUERIES ------------------ */

	/** Tests: def Jochen wohntIn:: Wuerzburg */
	public static String JOCHENWUERZBURG = createQuery(Vocabulary.JOCHEN, Vocabulary.WOHNTIN,
			Vocabulary.WUERZBURG);

	/** Tests: { Jochen istEin:: Assi} */
	public static String JOCHENASSI = createQuery(Vocabulary.JOCHEN, Vocabulary.ISTEIN,
			Vocabulary.ASSI);

	/** Tests: { Peter istEin:: Assi} */
	public static String PETERASSI = createQuery(Vocabulary.PETER, Vocabulary.ISTEIN,
			Vocabulary.ASSI);

	/** Tests: { Assi subclassof:: Person} */
	public static String ASSIPERSON = createQuery(Vocabulary.ASSI, Vocabulary.SUBCLASSOF,
			Vocabulary.PERSON);

	/** Tests: { Peter wohntIn:: Wuerzburg} */
	public static String PETERWUERZBURG = createQuery(Vocabulary.PETER, Vocabulary.WOHNTIN,
			Vocabulary.WUERZBURG);

	/** Tests: { Reinhard wohntIn:: Wuerzburg} */
	public static String REINHARDWUERZBURG = createQuery(Vocabulary.REINHARD, Vocabulary.WOHNTIN,
			Vocabulary.WUERZBURG);

	/*******************************************************************
	 * ------------------ QUERIES FOR UPDATED ARTICLE ------------------
	 *******************************************************************/
	public static class Update {

		/** Tests: { Jochen istEin:: Assistent} */
		public static String JOCHENASSISTENT = createQuery(Vocabulary.JOCHEN, Vocabulary.ISTEIN,
				Vocabulary.ASSISTENT);

		/** Tests: { Peter istEin:: Assistent} */
		public static String PETERASSISTENT = createQuery(Vocabulary.PETER, Vocabulary.ISTEIN,
				Vocabulary.ASSISTENT);

		/** Tests: {Assistent subclassof:: Person} */
		public static String ASSISTENTPERSON = createQuery(Vocabulary.ASSISTENT,
				Vocabulary.SUBCLASSOF,
				Vocabulary.PERSON);

	}

}
