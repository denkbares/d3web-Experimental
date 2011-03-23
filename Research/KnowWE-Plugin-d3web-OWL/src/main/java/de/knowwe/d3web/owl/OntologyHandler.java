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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.Ontology;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.report.SimpleMessageError;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.MessageUtils;
import de.d3web.we.wikiConnector.ConnectorAttachment;

/**
 * Attaches an OWL ontology to a d3web knowledge base.
 *
 *
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class OntologyHandler extends D3webSubtreeHandler<OntologyProviderType> {

	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<OntologyProviderType> section) {
		KnowledgeBase kb = getKB(article);
		if (kb == null) {
			return MessageUtils.asList(new SimpleMessageError(
					"Unable to load knowledge base from article: " + article.getTitle()));
		}

		String content = DefaultMarkupType.getContent(section);
		String sourcePath = DefaultMarkupType.getAnnotation(section,
				OntologyProviderType.ANNOTATION_SRC);

		boolean hasContent = content != null && !content.trim().isEmpty();
		boolean hasSourcePath = sourcePath != null && !sourcePath.trim().isEmpty();

		OWLOntology ontology = null;

		// no data available
		if (!hasSourcePath && !hasContent) {
			return MessageUtils.syntaxErrorAsList(
					"There is neither an path to an ontology file nor content which can be attached.");
		}
		// take the content as ontology
		else if (!hasSourcePath && hasContent) {
			try {
				ontology = createOntologyFromContent(content);
			}
			catch (OWLOntologyCreationException e) {
				return MessageUtils.syntaxErrorAsList(
						"The provided content doesn't represent a valid ontolgy!");
			}
		}
		// take the specified attachment as ontology
		else if (hasSourcePath) {
			try {
				ontology = getOntologyAttachment(sourcePath, section);
			}
			catch (OWLOntologyCreationException e) {
				return MessageUtils.syntaxErrorAsList(
						"The provided file doesn't contain a valid ontolgy!");
			}
			catch (IOException io) {
				return MessageUtils.asList(new SimpleMessageError(
						"Unexpected IOException while loading of ontology file: "
								+ sourcePath + "\n"
								+ io.getLocalizedMessage()));
			}
			if (ontology == null) {
				return MessageUtils.asList(new SimpleMessageError("Attachment \"" + sourcePath
						+ "\" doesn't exist."));
			}
		}

		// add the ontology to the knowledge base
		kb.getKnowledgeStore().addKnowledge(Ontology.KNOWLEDGE_KIND, new Ontology(ontology));

		// delete ontology in the OWLManager. We don't need it there!
		manager.removeOntology(ontology);

		// content and attachment was defined.
		// Warn the user that the content has been ignored!
		if (hasContent && hasSourcePath) {
			return MessageUtils.asList(new KDOMWarning() {

				@Override
				public String getVerbalization() {
					return "both src and content is specified, the content has been ignored.";
				}
			});
		}

		// all right, no errors and warnings
		return Collections.emptyList();
	}

	private OWLOntology createOntologyFromContent(String content) throws OWLOntologyCreationException {
		// TODO: Check whether getBytes as expected, otherwise add encoding
		InputStream is = new ByteArrayInputStream(content.getBytes());
		return manager.loadOntologyFromOntologyDocument(is);
	}

	private OWLOntology getOntologyAttachment(String sourcePath, Section<OntologyProviderType> section) throws OWLOntologyCreationException, IOException {
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
				KnowWEEnvironment.getInstance().getWikiConnector().getAttachments();
		for (ConnectorAttachment attachment : attachments) {
			if (!attachment.getFileName().equalsIgnoreCase(sourceFile)) continue;
			if (!attachment.getParentName().equalsIgnoreCase(sourceArticle)) continue;
			return manager.loadOntologyFromOntologyDocument(attachment.getInputStream());
		}
		// no appropriate attachment found
		return null;
	}
}
