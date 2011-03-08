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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.session.Session;

/**
 *
 * @author Sebastian Furth
 * @created Mar 8, 2011
 */
public class OWLConnector {

	// Singleton instance of this class
	private final static OWLConnector instance = new OWLConnector();

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());


	// Handles most things related to OWL
	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	// Stores an ontology for each knowledge base
	private final Map<KnowledgeBase, OWLOntology> ontologies = new WeakHashMap<KnowledgeBase, OWLOntology>();


	/**
	 * Private constructor to ensure non-instantiability. This is necessary for
	 * the Singleton-Pattern, which is applied to this class.
	 */
	private OWLConnector() {
	}

	/**
	 * Returns the singleton instance of OWLConnector.
	 *
	 * @created Mar 8, 2011
	 * @return singleton instance of OWLConnector.
	 */
	public static OWLConnector getInstance() {
		return instance;
	}

	/**
	 * Loads and creates an ontology. Therefore the ontology file stored in the
	 * d3web knowledge base will be loaded. The created ontology is stored in a
	 * map with the knowledge base as key.
	 *
	 * @created Mar 8, 2011
	 * @param kb d3web knowledge base containing the ontology.
	 */
	public void initializeOntology(KnowledgeBase kb) {
		if (!ontologies.containsKey(kb)) {
			try {
				Resource ontologyFile = kb.getResource(OWLConnectorConstants.DEFAULTPATH);
				if (ontologyFile == null) {
					throw new FileNotFoundException("The KnowledgeBase \"" + kb.getName()
									+ "\" doesn't contain an ontology file. The path to the ontolgoy has to be: "
									+ OWLConnectorConstants.DEFAULTPATH);
				}

				OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile.getInputStream());
				ontologies.put(kb, ontology);
				logger.info("Successfully loaded Ontology from KB \"" + kb.getName() + "\"");
			}
			catch (OWLOntologyCreationException e) {
				logger.severe("Could not create ontology from KB \"" + kb.getName() + "\": "
						+ e.getLocalizedMessage());
			}
			catch (IOException e) {
				logger.severe("Could not load ontology from KB \"" + kb.getName() + "\": "
						+ e.getLocalizedMessage());
			}
		}
	}

	public void updateFinding(Session session, PropagationEntry finding) {
		OWLOntology ontology = ontologies.get(session.getKnowledgeBase());

		if (finding.hasChanged()) {
			// TODO: Remove old finding
		}

		// TODO: Add new finding
	}

}
