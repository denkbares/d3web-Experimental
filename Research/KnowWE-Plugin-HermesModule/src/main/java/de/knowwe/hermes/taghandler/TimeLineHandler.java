/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.hermes.taghandler;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class TimeLineHandler extends AbstractHTMLTagHandler {

	public TimeLineHandler() {
		super("zeitlinie");
	}

	private static ResourceBundle kwikiBundle = ResourceBundle.getBundle("KnowWE_messages");

	private static final String TIME_SPARQL = "SELECT ?a WHERE { ?t lns:hasTitle ?a . ?t lns:hasImportance ?x . ?t lns:hasStartDate ?y . FILTER ( ?y > \"YEAR\" ^^xsd:double) .}";
	private static final String TIME_AFTER = "nach";

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {

		boolean asList = false;
		if (values.containsKey("renderType") && values.get("renderType").equals("list")) {
			asList = true;
		}

		String yearAfter = getIntAsString(-10000, values, TIME_AFTER);
		String querystring = null;
		try {
			querystring = TIME_SPARQL.replaceAll("YEAR", yearAfter);
		}
		catch (Exception e) {
			return "Illegal query String: " + querystring + "<br />" + " no valid parameter for: "
					+ TIME_AFTER;
		}
		try {
			QueryResultTable result = Rdf2GoCore.getInstance().sparqlSelect(querystring);
			return Strings.maskHTML(renderQueryResult(result, values, asList));
		}
		catch (ModelRuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getIntAsString(int defaultValue, Map<String, String> valueMap, String valueFromMap) {
		try {
			return String.valueOf(Integer.parseInt(valueMap.get(valueFromMap)));
		}
		catch (NumberFormatException nfe) {
			return String.valueOf(defaultValue);
		}
	}

	private String renderQueryResult(QueryResultTable resultTable, Map<String, String> params, boolean asList) {
		// List<String> bindings = result.getBindingNames();
		StringBuffer buffy = new StringBuffer();
		ClosableIterator<QueryRow> result = resultTable.iterator();
		try {
			while (result.hasNext()) {
				QueryRow row = result.next();
				List<String> names = resultTable.getVariables();
				for (String string : names) {
					Node n = row.getValue(string);
					buffy.append(Strings.decodeURL(n.toString()) + "<br>");
				}

			}
		}
		catch (ModelRuntimeException e) {
			return kwikiBundle.getString("KnowWE.owl.query.evalualtion.error") + ":"
					+ e.getMessage();
		}

		return buffy.toString();
	}

}
