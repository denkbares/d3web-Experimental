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
package de.d3web.owl;

import org.semanticweb.owlapi.model.OWLOntology;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;

/**
 * Adapter for OWLAPI's OWLOntology class. This is necessary for specifying it
 * as KnowledgeSlice.
 *
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class Ontology implements KnowledgeSlice {

	public final static KnowledgeKind<Ontology> KNOWLEDGE_KIND =
			new KnowledgeKind<Ontology>("Ontology", Ontology.class);

	private final OWLOntology ontology;

	public Ontology(OWLOntology ontology) {
		if (ontology == null) {
			throw new NullPointerException("The ontology can't be null!");
		}
		this.ontology = ontology;
	}

	/**
	 * Returns the encapsulated OWLOntology.
	 *
	 * @created Mar 23, 2011
	 * @return OWLOntology
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

}
