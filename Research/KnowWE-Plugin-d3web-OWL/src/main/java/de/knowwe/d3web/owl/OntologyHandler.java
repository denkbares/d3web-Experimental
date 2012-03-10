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
package de.knowwe.d3web.owl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.OntologyProvider;
import de.d3web.owl.Vocabulary;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Attaches an OWL ontology to a d3web knowledge base.
 * 
 * 
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class OntologyHandler extends D3webSubtreeHandler<OntologyProviderType> {

	private final String STOREKEY = "Ontology-Provider-Store-Key";

	@Override
	public Collection<Message> create(Article article, Section<OntologyProviderType> section) {
		KnowledgeBase kb = getKB(article);
		if (kb == null) {
			return Messages.asList(Messages.error(
					"Unable to load knowledge base from article: " + article.getTitle()));
		}

		String content = DefaultMarkupType.getContent(section);
		String sourcePath = DefaultMarkupType.getAnnotation(section,
				OntologyProviderType.ANNOTATION_SRC);

		boolean hasContent = content != null && !content.trim().isEmpty();
		boolean hasSourcePath = sourcePath != null && !sourcePath.trim().isEmpty();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		// no data available
		if (!hasSourcePath && !hasContent) {
			return Messages.asList(Messages.syntaxError(
					"There is neither a path to an ontology file nor content which can be attached."));
		}
		// take the content as ontology
		else if (!hasSourcePath && hasContent) {
			try {
				ontology = createOntologyFromContent(content, manager);
			}
			catch (OWLOntologyCreationException e) {
				return Messages.asList(Messages.syntaxError(
						"The provided content doesn't represent a valid ontolgy!"));
			}
		}
		// take the specified attachment as ontology
		else if (hasSourcePath) {
			try {
				ontology = getOntologyAttachment(sourcePath, section, manager);
			}
			catch (OWLOntologyCreationException e) {
				return Messages.asList(Messages.syntaxError(
						"The provided file doesn't contain a valid ontolgy!"));
			}
			catch (IOException io) {
				return Messages.asList(Messages.error(
						"Unexpected IOException while loading of ontology file: "
								+ sourcePath + "\n"
								+ io.getLocalizedMessage()));
			}
			if (ontology == null) {
				return Messages.asList(Messages.error("Attachment \"" + sourcePath
						+ "\" doesn't exist."));
			}

		}

		// Check if the vocabulary of the d3web task ontology is present
		if (!checkOntology(ontology)) {
			return Messages.asList(Messages.syntaxError(
					"The provided ontology doesn't meet the requirements of a valid d3web task-ontology. Check the vocabulary."));
		}

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// Save the ontology in RDFXML-Format
			manager.setOntologyFormat(ontology, new DefaultOntologyFormat());
			manager.saveOntology(ontology, bos);
			OntologyProvider provider = new OntologyProvider(bos.toByteArray());
			kb.getKnowledgeStore().addKnowledge(OntologyProvider.KNOWLEDGE_KIND, provider);
			// Save provider for incremental compilation
			KnowWEUtils.storeObject(article, section, STOREKEY, provider);
		}
		catch (OWLOntologyStorageException e) {
			return Messages.asList(Messages.error(
					"An error occured while saving the ontology to the knowledge base: "
							+ e.getLocalizedMessage()));
		}

		// content and attachment was defined.
		// Warn the user that the content has been ignored!
		if (hasContent && hasSourcePath) {
			return Messages.asList(Messages.warning("both src and content is specified, the content has been ignored."));
		}

		// all right, no errors and warnings
		return Collections.emptyList();
	}

	private boolean checkOntology(OWLOntology ontology) {
		OWLOntologyUtil util = new OWLOntologyUtil(ontology);
		for (Vocabulary v : Vocabulary.values()) {
			if (util.getOWLClassFor(v.getIRI()) == null
					&& util.getOWLPropertyFor(v.getIRI()) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void destroy(Article article, Section<OntologyProviderType> s) {
		OntologyProvider provider =
				(OntologyProvider) s.getSectionStore().getObject(article, STOREKEY);
		if (provider != null) {
			KnowledgeBase kb = getKB(article);
			if (kb != null) {
				kb.getKnowledgeStore().removeKnowledge(OntologyProvider.KNOWLEDGE_KIND,
						provider);
			}
		}
	}

	private OWLOntology createOntologyFromContent(String content, OWLOntologyManager manager) throws OWLOntologyCreationException {
		// TODO: Check whether getBytes works as expected, otherwise add
		// encoding
		InputStream is = new ByteArrayInputStream(content.getBytes());
		return manager.loadOntologyFromOntologyDocument(is);
	}

	private OWLOntology getOntologyAttachment(String sourcePath, Section<OntologyProviderType> section, OWLOntologyManager manager) throws OWLOntologyCreationException, IOException {
		String sourceFile;
		String sourceArticle;
		if (sourcePath.contains("/")) {
			int index = sourcePath.indexOf('/');
			sourceFile = sourcePath.substring(index + 1);
			sourceArticle = sourcePath.substring(0, index);
		}
		else {
			sourceFile = sourcePath.trim();
			sourceArticle = section.getArticle().getTitle();
		}
		// do the search
		Collection<ConnectorAttachment> attachments =
				Environment.getInstance().getWikiConnector().getAttachments();
		for (ConnectorAttachment attachment : attachments) {
			if (!attachment.getFileName().equalsIgnoreCase(sourceFile)) continue;
			if (!attachment.getParentName().equalsIgnoreCase(sourceArticle)) continue;
			return manager.loadOntologyFromOntologyDocument(attachment.getInputStream());
		}
		// no appropriate attachment found
		return null;
	}
}
