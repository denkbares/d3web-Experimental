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
package de.knowwe.rdf2go.utils;

import java.util.Collection;

import de.d3web.strings.Identifier;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author jochenreutelshofer
 * @created 24.04.2013
 */
public class PackageCompileLinkToTermDefinitionProvider implements LinkToTermDefinitionProvider {

	@Override
	public String getLinkToTermDefinition(String name, String masterArticle) {
		TerminologyManager terminologyManager = Environment.getInstance().getTerminologyManager(
				Environment.DEFAULT_WEB, masterArticle);
		Collection<Section<?>> termDefinitions = terminologyManager.getTermDefiningSections(new Identifier(
				name));
		String targetArticle = name;
		if (termDefinitions.size() > 0) {
			targetArticle = termDefinitions.iterator().next().getTitle();
		}

		return createBaseURL() + "?page=" + targetArticle;
	}

	/**
	 * 
	 * @created 29.11.2012
	 * @return
	 */
	public String createBaseURL() {
		return Environment.getInstance().getWikiConnector().getBaseUrl() + "Wiki.jsp";
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public String createSparqlURI(String name, Rdf2GoCore repository, String
	// masterArticle) {
	//
	// TerminologyManager terminologyManager =
	// Environment.getInstance().getTerminologyManager(
	// Environment.DEFAULT_WEB, masterArticle);
	// Collection<Section<?>> definitions =
	// terminologyManager.getTermDefiningSections(new Identifier(
	// name));
	// // IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
	// // new Identifier(name));
	// if (definitions.size() > 0) {
	// Iterator<Section<?>> iterator = definitions.iterator();
	// Section<?> def = iterator.next();
	// while (!(def.get() instanceof Term) && iterator.hasNext()) {
	// def = iterator.next();
	// }
	// return "<" + getURI((Section<? extends Term>) def) + ">";
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

	// private static URI getURI(Section<? extends Term> s) {
	// if (s == null) return null;
	//
	// String termName = s.get().getTermName(s);
	//
	// URI uri = null; // getURIForPredefinedConcept(termName);
	//
	// if (uri == null) {
	// String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();
	// String name = Strings.encodeURL(termName);
	// uri = new URIImpl(baseUrl + name);
	// }
	// return uri;
	//
	// }

}
