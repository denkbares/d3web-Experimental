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
package de.knowwe.wisskont.navigation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.Environment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 12.12.2012
 */
public class ParentConceptLinkAppendHandler implements PageAppendHandler {

	@Override
	public void append(String web, String topic, UserContext user, RenderResult result) {

		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, topic);
		List<Section<ConceptMarkup>> conceptMarkups = Sections.findSuccessorsOfType(
				article.getRootSection(), ConceptMarkup.class);
		if (conceptMarkups.size() == 1) {
			Section<IncrementalTermDefinition> def = Sections.findSuccessor(conceptMarkups.get(0),
					IncrementalTermDefinition.class);
			URI uri = RDFSUtil.getURI(def);
			String sparql = "SELECT ?z WHERE { <" + uri + "> lns:unterkonzept ?z.}";
			QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(sparql);

			ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
			if (!resultIterator.hasNext()) {
				return;
			}
			result.appendHtml("<div class='parentBreadcrumb'>");
			result.append("Oberbegriff: ");
			while (resultIterator.hasNext()) {
				QueryRow parentConceptResult = resultIterator.next();
				Node value = parentConceptResult.getValue("z");
				String urlString = value.asURI().toString();

				String termName = "";
				try {
					termName = URLDecoder.decode(
							urlString.substring(Rdf2GoCore.getInstance().getLocalNamespace().length()),
							"UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new Identifier(termName));

				if (termDefinitions.size() > 0) {
					urlString = Rdf2GoCore.getInstance().getLocalNamespace()
							+ termDefinitions.iterator().next().getTitle();
				}

				result.appendHtml("<a href='" + urlString + "'>");
				result.append(termName);
				result.appendHtml("</a>");
				if (resultIterator.hasNext()) result.append(", ");
			}

			result.appendHtml("</div>");
			result.append(System.getProperty("line.separator"));
		}
	}

	@Override
	public boolean isPre() {
		return true;
	}

}
