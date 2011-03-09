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

package de.d3web.we.hermes.taghandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.semantic.ISemanticCore;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.semantic.sparql.DefaultSparqlRenderer;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class ListIndividualsHandler extends AbstractHTMLTagHandler {

	private static final String markup = "listIndividuals";

	public ListIndividualsHandler() {
		super(markup);
	}

	private static final String SPARQL_START = "SELECT ?x WHERE { ?x rdf:type CLASS .";
	private static final String SPARQL_END = "} ORDER BY ASC(?x)";

	@Override
	public String renderHTML(String topic, UserContext user,
			Map<String, String> values, String web) {

		String className = values.get("class");
		ISemanticCore sc = SemanticCoreDelegator.getInstance();

		// Removes all entries which are no properties
		Map<String, String> properties = new HashMap<String, String>(values);
		properties.remove("class");
		properties.remove(markup);
		properties.remove(markup.toLowerCase());
		properties.remove("_cmdline");
		properties.remove("kdomid");

		String sparql_mid = "";
		for (Entry<String, String> entry : properties.entrySet()) {
			sparql_mid += " ?x lns:";
			sparql_mid += entry.getKey() + " <";
			URI propURI = sc.getUpper().getHelper().createlocalURI(entry.getValue());
			sparql_mid += propURI.toString() + ">.";
		}

		if (className == null) {
			return "No class given for list class members tag!";
		}

		URI classURI = sc.getUpper().getHelper().createlocalURI(className);

		String querystring = SPARQL_START.replaceAll("CLASS", "<" + classURI.toString() + ">");
		querystring += sparql_mid + SPARQL_END;

		TupleQueryResult result = SPARQLUtil.executeTupleQuery(querystring);

		return KnowWEUtils.maskHTML(DefaultSparqlRenderer.getInstance().renderResults(result,
				true));
	}
}
