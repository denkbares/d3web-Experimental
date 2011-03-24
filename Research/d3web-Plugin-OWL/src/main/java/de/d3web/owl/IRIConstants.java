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
 * Offers some default URIs. For example all URIs of the Task-Ontology's classes
 * are provided.
 *
 * @author Sebastian Furth
 * @created Mar 24, 2011
 */
public interface IRIConstants {

	public final IRI PREFIX = IRI.create("http://ki.informatik.uni-wuerzburg.de/d3web/d3web.owl");

	public final IRI PSSESSION = IRI.create(PREFIX + "#PSSession");

	public final IRI FINDING = IRI.create(PREFIX + "#Finding");

	public final IRI QUESTIONNAIRE = IRI.create(PREFIX + "#Questionnaire");

	public final IRI INPUT = IRI.create(PREFIX + "#Input");

	public final IRI NUMERICINPUT = IRI.create(PREFIX + "#NumericInput");

	public final IRI CHOICEINPUT = IRI.create(PREFIX + "#ChoiceInput");

	public final IRI SOLUTION = IRI.create(PREFIX + "#Solution");

	public final IRI VALUE = IRI.create(PREFIX + "#Value");

	public final IRI CHOICEVALUE = IRI.create(PREFIX + "#ChoiceValue");

	public final IRI NUMERICVALUE = IRI.create(PREFIX + "#NumericValue");

	public interface SolutionValues {

		public final IRI ESTABLISHED = IRI.create(PREFIX + "#Established");

		public final IRI SUGGESTED = IRI.create(PREFIX + "#Suggested");

		public final IRI UNDEFINED = IRI.create(PREFIX + "#Undefined");

		public final IRI EXCLUDED = IRI.create(PREFIX + "#Excluded");

	}
}
