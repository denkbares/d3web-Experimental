/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.knowwe.rdfs.inspect;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.taghandler.TagHandlerTypeContent;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.ontology.sparql.SparqlResultRenderer;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.tools.ToolSet;
import de.knowwe.tools.ToolUtils;

public class PropertyUseTagHandler extends AbstractTagHandler {

	// Parameter used in the request
	public static final String OBJECTNAME = "objectname";

	private static DefaultMarkupRenderer defaultMarkupRenderer =
			new DefaultMarkupRenderer();

	public PropertyUseTagHandler() {
		super("propertyuse");
	}

	@Override
	public final void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {

		String content = renderContent(section, userContext, parameters);
		Section<TagHandlerTypeContent> tagNameSection = Sections.successor(section,
				TagHandlerTypeContent.class);
		String sectionID = section.getID();
		ToolSet tools = ToolUtils.getTools(tagNameSection, userContext);

		RenderResult buffer = new RenderResult(userContext);
		String cssClassName = "type_" + section.get().getName();
		defaultMarkupRenderer.renderDefaultMarkupStyled(
				getTagName(), content, sectionID, cssClassName, tools, userContext,
				buffer);
		result.appendJSPWikiMarkup(buffer);
	}

	private String renderContent(Section<?> section, UserContext userContext, Map<String, String> parameters) {

		RenderResult buffy = new RenderResult(userContext);
		Map<String, String> urlParameters = userContext.getParameters();

		// First try the URL-Parameter, if null try the TagHandler-Parameter.
		String externalForm = null;
		if (urlParameters.get(OBJECTNAME) != null) {
			externalForm = Strings.decodeURL(urlParameters.get(OBJECTNAME));
		}
		else if (parameters.get(OBJECTNAME) != null) {
			externalForm = Strings.decodeURL(parameters.get(OBJECTNAME));
		}

		Collection<Section<? extends SimpleReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
				Identifier.fromExternalForm(externalForm));

		if (termReferences != null && termReferences.size() > 0) {

			buffy.appendHtml("Relations for property <b>" + externalForm
					+ "</b>:<br>");

			URI propURI = RDFSUtil.getURI(termReferences.iterator().next());

			String query = "SELECT ?x ?z WHERE { ?x <" + propURI.toString()
					+ "> ?z .}";
			QueryResultTable classMembersTable = Rdf2GoCore.getInstance().sparqlSelect(
					query);
			buffy.append(SparqlResultRenderer.getInstance().getSparqlRenderResult(classMembersTable,
					userContext, section));

		}
		return buffy.toStringRaw();
	}
}
