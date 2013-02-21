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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;

/**
 * 
 * @author jochenreutelshofer
 * @created 28.11.2012
 */
public class ShowHierarchyRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {

		string.appendHTML("<pre>");

		String startConceptString = DefaultMarkupType.getAnnotation(section,
				ShowHierarchyMarkup.START_CONCEPT);
		String concept = null;
		if (startConceptString != null) {
			concept = startConceptString.trim();
		}

		String relationString = DefaultMarkupType.getAnnotation(section,
				ShowHierarchyMarkup.RELATION_ANNOTATION_KEY);
		String relation = null;
		if (relationString != null) {
			relation = relationString.trim();
		}

		if (relation != null && concept != null) {
			appendLink(concept, string);
			string.append("\n");
			renderSubconceptsOf(concept, relation, 1, string);

		}
		string.appendHTML("</pre>");
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param concept
	 * @param relation
	 * @param string
	 */
	private void renderSubconceptsOf(String concept, String relation, int depth, RenderResult string) {
		Collection<Section<? extends SimpleDefinition>> defs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new TermIdentifier(concept));
		String query = "SELECT ?z WHERE { ?z lns:"
				+ relation
				+ " <"
				+ RDFSUtil.getURI(defs.iterator().next())
				+ ">  .}";

		List<String> subconcepts = new ArrayList<String>();
		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);
		while (result.hasNext()) {
			QueryRow row = result.next();

			String zURI = row.getValue("z").toString();
			String z = zURI.substring(zURI.indexOf("=") + 1);

			Collection<Section<? extends SimpleDefinition>> termDefinitions = null;
			try {
				termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new TermIdentifier(URLDecoder.decode(z.replaceAll("\\+", " "), "UTF-8")));
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (termDefinitions.size() > 0) {
				Section<? extends SimpleDefinition> def = termDefinitions.iterator().next();
				subconcepts.add(def.get().getTermName(def));
			}

		}

		Collections.sort(subconcepts);

		for (String name : subconcepts) {
			string.append(createDashes(depth) + " ");
			appendLink(name, string);
			string.append("\n");
			renderSubconceptsOf(name, relation, depth + 1, string);
		}

	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param name
	 * @return
	 */
	private void appendLink(String name, RenderResult result) {
		Collection<Section<? extends SimpleDefinition>> defs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new TermIdentifier(name));
		URI uri = RDFSUtil.getURI(defs.iterator().next());

		result.appendHTML("<a href='" + uri.toString() + "'>");
		result.append(name);
		result.appendHTML("</a>");
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param depth
	 * @return
	 */
	private String createDashes(int depth) {
		String s = "";

		for (int i = 0; i < depth; i++) {
			s += "-";
		}
		return s;
	}

	private static String createSparqlURI(String name) {
		name = name.replaceAll(" ", "+");
		if (name.contains("+") || name.contains(".")) {
			String localNamespace = Rdf2GoCore.getInstance().getLocalNamespace();

			return "<" + localNamespace + name + ">";
		}

		try {
			return "lns:" + URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
