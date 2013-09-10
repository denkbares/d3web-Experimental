/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.owl;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author sebastian
 * @created Apr 8, 2011
 */
public enum Vocabulary {

	PSSESSION("PSSession"),
	FINDING("Finding"),
	QUESTIONNAIRE("Questionnaire"),
	INPUT("Input"),
	NUMERICINPUT("NumericInput"),
	CHOICEINPUT("ChoiceInput"),
	SOLUTION("Solution"),
	VALUE("Value"),
	CHOICEVALUE("ChoiceValue"),
	NUMERICVALUE("NumericValue"),
	HASINPUT("hasInput"),
	HASVALUE("hasValue"),
	ISSTOREDBY("isStoredBy");

	public static final String BASEURI = "http://is.informatik.uni-wuerzburg.de/d3web/d3web.owl#";

	private final IRI iri;

	Vocabulary(String iriString) {
		this.iri = IRI.create(BASEURI + iriString);
	}

	public IRI getIRI() {
		return this.iri;
	}

}
