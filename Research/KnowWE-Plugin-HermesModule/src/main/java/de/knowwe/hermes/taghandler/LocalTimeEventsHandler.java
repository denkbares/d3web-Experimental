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

package de.knowwe.hermes.taghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.hermes.TimeStamp;
import de.knowwe.rdf2go.Rdf2GoCore;

public class LocalTimeEventsHandler extends AbstractHTMLTagHandler {

	public LocalTimeEventsHandler() {
		super("lokaleZeitlinie");
	}

	private static final String TIME_SPARQL = "SELECT  ?t ?title ?imp ?desc ?y WHERE {  ?t rdfs:isDefinedBy ?to . ?to ns:hasTopic TOPIC . ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:hasImportance ?imp . ?t lns:hasDateDescription ?y .}";
	private static final String TIME_AFTER = "nach";

	// ?t lns:isDefinedBy ?to . ?to lns:hasTopic TOPIC .
	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {

		String yearAfter = getIntAsString(-10000, values, TIME_AFTER);
		String querystring = null;
		try {
			querystring = TIME_SPARQL.replaceAll("TOPIC", "<"
					+ Rdf2GoCore.getInstance().createlocalURI(topic).toString() + ">");
		}
		catch (Exception e) {
			return "Illegal query String: " + querystring + "<br />" + " no valid parameter for: "
					+ TIME_AFTER;
		}

		QueryResultTable qResultTable = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		ClosableIterator<QueryRow> qResult = qResultTable.iterator();

		return KnowWEUtils.maskHTML(renderQueryResult(qResult, values));

	}

	private String getIntAsString(int defaultValue, Map<String, String> valueMap, String valueFromMap) {
		try {
			return String.valueOf(Integer.parseInt(valueMap.get(valueFromMap)));
		}
		catch (NumberFormatException nfe) {
			return String.valueOf(defaultValue);
		}
	}

	private String renderQueryResult(ClosableIterator<QueryRow> result, Map<String, String> params) {
		// List<String> bindings = result.getBindingNames();
		StringBuffer buffy = new StringBuffer();
		try {
			buffy.append("<ul>");
			boolean found = false;
			TreeMap<TimeStamp, String> queryResults = new TreeMap<TimeStamp, String>();
			while (result.hasNext()) {
				found = true;
				QueryRow row = result.next();
				try {
					String importance = URLDecoder.decode(row.getValue("imp").toString(), "UTF-8");
					if (importance.equals("(1)")) {

						String title = URLDecoder.decode(row.getValue("title").toString(), "UTF-8");
						String timeString = URLDecoder.decode(row.getValue("y").toString(), "UTF-8");
						TimeStamp timeStamp = new TimeStamp(timeString);
						String timeDescr = timeStamp.getDescription();
						queryResults.put(timeStamp, "<li>" + timeDescr + ": " + title + "</li>");
					}
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// Set<String> names = set.getBindingNames();
				// for (String string : names) {
				// Binding b = set.getBinding(string);
				// Value event = b.getValue();
				// buffy.append(URLDecoder.decode(event.toString(), "UTF-8")
				// + "<br>");
				// }
			}
			for (String s : queryResults.values()) {
				buffy.append(s);
			}
			if (!found) buffy.append("no results found");
			buffy.append("</ul>");
		}
		catch (ModelRuntimeException e) {
			return "error";
		}
		return buffy.toString();
	}
}
