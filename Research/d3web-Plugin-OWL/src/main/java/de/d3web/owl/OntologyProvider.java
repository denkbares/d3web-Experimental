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

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;

/**
 * Adapter for OWLAPI's OWLOntology class. This is necessary for specifying it
 * as KnowledgeSlice.
 *
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class OntologyProvider implements KnowledgeSlice {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public final static KnowledgeKind<OntologyProvider> KNOWLEDGE_KIND =
			new KnowledgeKind<OntologyProvider>("OntologyProvider", OntologyProvider.class);

	private final byte[] ontology;

	public OntologyProvider(byte[] ontology) {
		if (ontology == null) {
			throw new NullPointerException("The ontology can't be null!");
		}
		this.ontology = ontology;
	}

	/**
	 * Returns a new instance of the encapsulated ontology. This ontology
	 * instance can be used in d3web sessions.
	 *
	 * @created Mar 23, 2011
	 * @return OWLOntology
	 */
	public OWLOntology createOntologyInstance() {
		ByteArrayInputStream bai = new ByteArrayInputStream(ontology);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			return manager.loadOntologyFromOntologyDocument(bai);
		}
		catch (OWLOntologyCreationException e) {
			logger.severe("Ontology could not be created from the provided byte[]: "
					+ e.getLocalizedMessage());
		}
		return null;
	}

}
