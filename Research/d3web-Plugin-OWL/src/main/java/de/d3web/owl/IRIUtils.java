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
import org.semanticweb.owlapi.model.OWLOntology;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Value;

/**
 * Utility class for creating IRIs for specific objects.
 *
 * @author Sebastian Furth
 * @created Mar 28, 2011
 */
public class IRIUtils {

	/* Ensure non-instantiability */
	private IRIUtils() {
	}

	public static IRI toIRI(String s, OWLOntology ontology) {
		return IRI.create(getOntologyIRI(ontology) + "#" + adapt(s));
	}

	public static IRI toIRI(TerminologyObject object, OWLOntology ontology) {
		return IRI.create(getOntologyIRI(ontology) + "#" + adapt(object.getName()));
	}

	public static IRI toIRI(Value value, OWLOntology ontology) {
		if (value instanceof Rating) {
			return IRI.create(IRIConstants.PREFIX + "#" + adapt(value.toString()));
		}
		return IRI.create(getOntologyIRI(ontology) + "#" + adapt(value.toString()));
	}

	public static IRI toIRI(TerminologyObject object, Value value, OWLOntology ontology) {
		return IRI.create(toIRI(object, ontology) + "=" + adapt(value.toString()));
	}

	public static IRI getOntologyIRI(OWLOntology ontology) {
		return ontology.getOntologyID().getOntologyIRI();
	}

	private static String adapt(String text) {
		return text.replaceAll(" ", "_").toLowerCase();
	}

}
