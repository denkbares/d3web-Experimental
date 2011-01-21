/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.we.core.semantic.rdf2go.example;

import java.util.Map;

import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class DumpHandler extends AbstractHTMLTagHandler {

	public DumpHandler() {
		super("DumpModel");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> values, String web) {
		
		Rdf2GoCore.getInstance().dumpModel();

		if (Rdf2GoCore.USE_MODEL == Rdf2GoCore.SESAME) {
			Rdf2GoCore.getInstance().dumpNamespaces();
		}
		Rdf2GoCore.getInstance().dumpStatementcache();
		Rdf2GoCore.getInstance().dumpDuplicates();

		return KnowWEUtils.maskHTML(Rdf2GoCore.getInstance().renderedSparqlSelect(
				"select ?Subject ?Predicate ?Object where { ?Subject ?Predicate ?Object }"));
	}

}