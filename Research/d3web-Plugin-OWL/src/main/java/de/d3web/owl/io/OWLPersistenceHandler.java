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
package de.d3web.owl.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.OntologyProvider;

/**
 * Persistence handler for owl ontologies attached to the KnowledgeBase. The
 * attached ontology will be read into a byte[]. This byte[] will be used to
 * provide various InputStreams. These InputStreams can be used to create new
 * ontology instances for each session.
 *
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class OWLPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {
		OntologyProvider provider = knowledgeBase.getKnowledgeStore().getKnowledge(
				OntologyProvider.KNOWLEDGE_KIND);
		if (provider != null) {
			try {
				OWLOntology ontology = provider.createOntologyInstance();
				OWLOntologyManager manager = ontology.getOWLOntologyManager();
				manager.setOntologyFormat(ontology, new DefaultOntologyFormat());
				manager.saveOntology(ontology, stream);
			}
			catch (OWLOntologyStorageException e) {
				logger.severe("Unexpected error while saving ontology!" + e.getLocalizedMessage());
			}
		}
		else {
			logger.severe("OntologyProvider is null, nothing was saved!");
		}
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		// We have exactly one ontology in the knowledge base
		return 1;
	}

	@Override
	public void read(KnowledgeBase knowledgeBase, InputStream stream, ProgressListener listerner) throws IOException {
		// We save the ontology as byte[], as we have to create new instances of
		// the ontology in each session! These instances can only be created
		// from InputStreams.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int i;
		while ((i = stream.read()) != -1) {
			bos.write(i);
		}
		knowledgeBase.getKnowledgeStore().addKnowledge(OntologyProvider.KNOWLEDGE_KIND,
					new OntologyProvider(bos.toByteArray()));
	}

}
