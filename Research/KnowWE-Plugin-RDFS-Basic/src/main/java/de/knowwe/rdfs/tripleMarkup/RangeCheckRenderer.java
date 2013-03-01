/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.tripleMarkup;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.DefaultMessageRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.ObjectPropertyDefinitionMarkup;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.tripleMarkup.TripleMarkupContent.SimpleTurtlePredicate;
import de.knowwe.rdfs.util.RDFSUtil;

class RangeCheckRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {
		Section<KnowledgeUnit> triple = Sections.findAncestorOfType(section,
				KnowledgeUnit.class);

		Section<SimpleTurtlePredicate> predicate = Sections.findSuccessor(triple,
				SimpleTurtlePredicate.class);

		Section<SimpleTurtleObjectRef> object = Sections.findSuccessor(triple,
				SimpleTurtleObjectRef.class);

		Section<SimpleTurtleSubject> subject = Sections.findSuccessor(triple,
				SimpleTurtleSubject.class);

		String predName = predicate.getText().trim();

		// IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(predName);

		Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
				new TermIdentifier(predName));
		String domainClassName = null;
		String rangeClassName = null;
		boolean warningRange = false;
		boolean warningDomain = false;
		if (info != null && object != null
				&& RDFSUtil.isTermCategory(predicate,
						RDFSTermCategory.ObjectProperty)) {

			if (info instanceof Map) {
				Set keyset = ((Map) info).keySet();
				for (Object key : keyset) {
					if (key.equals(
							ObjectPropertyDefinitionMarkup.RDFS_DOMAIN_KEY)) {
						Object o = ((Map) info).get(key);
						if (o instanceof String) {
							domainClassName = (String) o;
						}
						else {
							Logger.getLogger(this.getClass().getName()).severe(
									"Value in ObjectDefitionInfo-Map was not a String");
						}

					}
					if (key.equals(
							ObjectPropertyDefinitionMarkup.RDFS_RANGE_KEY)) {
						Object o = ((Map) info).get(key);
						if (o instanceof String) {
							rangeClassName = (String) o;
						}
						else {
							Logger.getLogger(this.getClass().getName()).severe(
									"Value in ObjectDefitionInfo-Map was not a String");
						}
					}
				}
			}
			if (rangeClassName != null) {
				URI rangeClassURI = RDFSUtil.getURI(IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new TermIdentifier(rangeClassName)).iterator().next());
				URI objectURI = RDFSUtil.getURI(object);
				String queryRange = "ASK { <" + objectURI + "> <" + RDF.type + "> <"
						+ rangeClassURI + "> .}";
				warningRange = !Rdf2GoCore.getInstance().sparqlAskExcludeStatementForSection(
						queryRange, triple);
			}

			if (domainClassName != null) {
				URI domainClassURI = RDFSUtil.getURI(IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new TermIdentifier(domainClassName)).iterator().next());

				URI subjectURI = RDFSUtil.getURI(subject);

				String queryDomain = "ASK { <" + subjectURI + "> <" + RDF.type
						+ "> <"
						+ domainClassURI + "> .}";
				warningDomain = !Rdf2GoCore.getInstance().sparqlAskExcludeStatementForSection(
						queryDomain, triple);
			}

		}

		if (warningRange) {
			DefaultMessageRenderer.WARNING_RENDERER.preRenderMessage(
					new Message(Message.Type.WARNING,
							"Triple object does not match range definition"),
					user, null, string);
		}
		if (warningDomain) {

			DefaultMessageRenderer.WARNING_RENDERER.preRenderMessage(
					new Message(Message.Type.WARNING,
							"Triple subject does not match domain definition"),
					user, null, string);
		}

		DelegateRenderer.getInstance().render(section, user, string);

		if (warningRange) {

			DefaultMessageRenderer.WARNING_RENDERER.postRenderMessage(
					new Message(Message.Type.WARNING,
							""), user, null, string);
		}
		if (warningDomain) {

			DefaultMessageRenderer.WARNING_RENDERER.postRenderMessage(
					new Message(Message.Type.WARNING,
							""), user, null, string);
		}

	}
}