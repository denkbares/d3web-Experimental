/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.rdfs.vis.markup;

import java.util.Collection;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.utils.LinkToTermDefinitionProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 24.04.2013
 */
public class IncrementalCompilerLinkToTermDefinitionProvider implements LinkToTermDefinitionProvider {

	@Override
	public String getLinkToTermDefinition(String name, String masterArticle) {
		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				new Identifier(name));
		String targetArticle = name;
		if (termDefinitions.size() > 0) {
			targetArticle = termDefinitions.iterator().next().getTitle();
		}

		return createBaseURL() + "?page=" + targetArticle;
	}

	// @Override
	// public String createSparqlURI(String name, Rdf2GoCore repository, String
	// masterArticle) {
	// if (name == null) return null;
	// Collection<Section<? extends SimpleDefinition>> definitions =
	// IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
	// new Identifier(name));
	// if (definitions.size() > 0) {
	// Section<? extends SimpleDefinition> def = definitions.iterator().next();
	// return "<" + RDFSUtil.getURI(def) + ">";
	// }
	// name = name.replaceAll(" ", "+");
	// if (name.contains("+") || name.contains(".")) {
	// String localNamespace = repository.getLocalNamespace();
	//
	// return "<" + localNamespace + name + ">";
	// }
	//
	// try {
	// return "lns:" + URLDecoder.decode(name, "UTF-8");
	// }
	// catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * 
	 * @created 29.11.2012
	 * @return
	 */
	public String createBaseURL() {
		return Environment.getInstance().getWikiConnector().getBaseUrl() + "Wiki.jsp";
	}

}
