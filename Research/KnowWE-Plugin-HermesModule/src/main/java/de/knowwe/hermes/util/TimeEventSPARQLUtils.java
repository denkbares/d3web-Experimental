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

package de.knowwe.hermes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.strings.Strings;
import de.knowwe.hermes.TimeEvent;
import de.knowwe.hermes.maps.Placemark;
import de.knowwe.rdf2go.Rdf2GoCore;

public class TimeEventSPARQLUtils {

	// private static final String TIME_SPARQL =
	// "SELECT  ?title ?imp ?desc ?y  WHERE { ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:hasImportance ?imp . ?t lns:hasStartDate ?y . FILTER ( ?y > \"YEARFROM\" ^^xsd:double . ?y < \"YEARTO\" ^^xsd:double) .}";
	private static final String TIME_SPARQL = "SELECT  ?t ?title ?topic ?imp ?desc ?encodedTime ?y ?kdomid ?topic WHERE {  ?t rdfs:isDefinedBy ?to . ?to ns:hasTopic ?topic . ?to ns:hasNode ?kdomid . ?t lns:hasDateDescription ?encodedTime . ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:hasImportance ?imp . ?t lns:hasStartDate ?y . FILTER ( ?y > \"YEARFROM\" ^^xsd:double ) . FILTER ( ?y < \"YEARTO\" ^^xsd:double) .}";
	// private static final String TIME_SPARQL =
	// "SELECT ?title ?imp ?desc ?y WHERE { ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:hasImportance ?imp . ?t lns:hasStartDate ?y . FILTER ( ?y > \"YEARFROM\" ^^xsd:double ) . FILTER ( ?y < \"YEARTO\" ^^xsd:double) .}";
	// private static final String TIME_SPARQL =
	// "SELECT ?t ?title ?desc ?imp WHERE {  ?t lns:hasTitle ?title . ?t lns:hasDescription ?desc . ?t lns:hasImportance ?imp  . ?t lns:hasStartDate ?y . FILTER ( ?y > \"YEARFROM\" ^^xsd:double ) . FILTER ( ?y < \"YEARTO\" ^^xsd:double) .}}";

	private static final String CONCEPT_SPARQL = "SELECT  ?t ?title ?topic ?imp ?desc ?encodedTime ?y ?kdomid ?topic WHERE {  ?t rdfs:isDefinedBy ?to . ?to ns:hasTopic ?topic . ?to ns:hasNode ?kdomid . ?t lns:hasDateDescription ?encodedTime . ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:hasImportance ?imp . ?t lns:involves CONCEPT .}";
	private static final String LOCATIONS_FOR_EVENTS_FOR_TOPIC_SPARQL = "SELECT  ?long ?lat ?title ?desc WHERE {  ?t rdfs:isDefinedBy ?to . ?to ns:hasTopic TOPIC . ?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:takesPlaceAt ?loc . ?loc lns:hasLatitude ?lat . ?loc lns:hasLongitude ?long .}";
	private static final String LOCATIONS_FOR_TOPIC_SPARQL = "SELECT  ?long ?lat ?loc WHERE { TOPIC lns:mentions ?loc . ?loc lns:hasLatitude ?lat . ?loc lns:hasLongitude ?long .}";
	private static final String PLACEMARK_SPARQL = "SELECT  ?long ?lat ?title ?topic ?desc WHERE {?t lns:hasDescription ?desc . ?t lns:hasTitle ?title . ?t lns:involves CONCEPT . ?t lns:takesPlaceAt ?loc . ?loc lns:hasLatitude ?lat . ?loc lns:hasLongitude ?long .}";
	private static final String SOURCE_SPARQL = "SELECT ?source WHERE { <*URI*> lns:hasSource ?source .}";

	public static List<TimeEvent> findTimeEventsFromTo(int yearFrom, int yearTo) {

		String querystring = null;

		querystring = TIME_SPARQL.replaceAll("YEARFROM", Integer.toString(yearFrom));
		querystring = querystring.replaceAll("YEARTO", Integer.toString(yearTo));

		QueryResultTable result = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		return buildTimeEvents(result);
	}

	public static List<TimeEvent> findTimeEventsInvolvingConcept(
			String conceptName) {

		String querystring = null;

		querystring = CONCEPT_SPARQL.replaceAll("CONCEPT", "lns:" + conceptName);

		QueryResultTable result = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		return buildTimeEvents(result);
	}

	// private static final String TEXTORIGIN_SPARQL =
	// "SELECT ?textOrigin WHERE { ?t lns:hasNode ?textOrigin .  ?t lns:hasTitle TITLE .}";

	private static List<TimeEvent> buildTimeEvents(QueryResultTable resultTable) {
		// List<String> bindings = result.getBindingNames();
		ClosableIterator<QueryRow> result = resultTable.iterator();

		List<TimeEvent> events = new ArrayList<TimeEvent>();
		try {
			while (result.hasNext()) {

				QueryRow row = result.next();
				String tURI = row.getValue("t").toString();
				String title = row.getValue("title").toString();

				String kdomid = row.getValue("kdomid").toString();
				String topic = row.getValue("topic").toString();

				String imp = row.getValue("imp").asDatatypeLiteral().getValue();

				// Binding textOriginB = set.getBinding("textOrigin");

				String time = row.getValue("encodedTime").toString();
				String desc = row.getValue("desc").toString();

				Set<String> sources = new HashSet<String>();

				String query = SOURCE_SPARQL.replace("*URI*", tURI);
				ClosableIterator<QueryRow> sourcesResult = Rdf2GoCore.getInstance().sparqlSelectIt(
						query.replaceAll("TITLE", title));

				while (sourcesResult.hasNext()) {
					// for some reason every source appears twice in this loop
					// ;p
					// thus using a set
					QueryRow row2 = sourcesResult.next();
					String aSource = row2.getValue("source").toString();
					aSource = Strings.decodeURL(aSource);
					if (aSource != null) {
						sources.add(aSource);
					}
				}

				title = Strings.decodeURL(title);
				imp = Strings.decodeURL(imp);
				time = Strings.decodeURL(time);
				desc = Strings.decodeURL(desc);
				topic = Strings.decodeURL(topic);
				kdomid = Strings.decodeURL(kdomid);

				// Check if Importance is stored incl brackets (old timeevent)
				if (imp.startsWith("(")) {
					imp = imp.substring(1, 2);
				}

				int parseInt = 3;

				try {
					parseInt = Integer.parseInt(imp);
				}
				catch (NumberFormatException e) {
					// TODO
				}
				List<String> resultSources = new ArrayList<String>();
				resultSources.addAll(sources);

				events.add(new TimeEvent(title, desc, parseInt, resultSources,
						time, kdomid, topic));

			}
		}
		catch (ModelRuntimeException e) {
			// return
			// kwikiBundle.getString("KnowWE.owl.query.evalualtion.error")
			// + ":" + e.getMessage();
		}

		// return buffy.toString();

		return events;
	}

	public static List<Placemark> findLocationsOfTimeEventsInvolvingConcept(String concept) {
		String querystring = null;

		querystring = PLACEMARK_SPARQL.replaceAll("CONCEPT", "lns:" + concept);

		QueryResultTable result = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		return buildPlacemarks(result);
	}

	public static List<Placemark> findLocationsOfTimeEventsForTopic(String topic) {
		String querystring = LOCATIONS_FOR_EVENTS_FOR_TOPIC_SPARQL.replaceAll("TOPIC", "<"
				+ Rdf2GoCore.getInstance().createlocalURI(topic).toString() + ">");
		QueryResultTable queryResult = Rdf2GoCore.getInstance().sparqlSelect(
				querystring);
		List<Placemark> result = buildPlacemarks(queryResult);

		querystring = LOCATIONS_FOR_TOPIC_SPARQL.replaceAll("TOPIC", "<"
				+ Rdf2GoCore.getInstance().createlocalURI(topic).toString() + ">");
		queryResult = Rdf2GoCore.getInstance().sparqlSelect(querystring);
		result.addAll(buildPlacemarksForTopic(queryResult));
		return result;
	}

	private static Collection<? extends Placemark> buildPlacemarksForTopic(QueryResultTable resultTable) {
		ClosableIterator<QueryRow> result = resultTable.iterator();
		List<Placemark> placemarks = new ArrayList<Placemark>();
		if (result == null) return placemarks;
		try {
			while (result.hasNext()) {

				QueryRow row = result.next();

				String loc = row.getValue("loc").toString();
				String latString = row.getValue("lat").toString();
				String longString = row.getValue("long").toString();

				loc = Strings.decodeURL(loc);
				latString = Strings.decodeURL(latString);
				longString = Strings.decodeURL(longString);
				loc = removeNamespace(loc);
				double latitude = Double.parseDouble(latString.replaceAll(",", "."));
				double longitude = Double.parseDouble(longString.replaceAll(",", "."));

				placemarks.add(new Placemark(loc, latitude, longitude, ""));

			}
		}
		catch (ModelRuntimeException e) {
			return null;
		}
		return placemarks;
	}

	private static String removeNamespace(String loc) {
		int index = loc.lastIndexOf("#");
		return loc.substring(index + 1);
	}

	private static List<Placemark> buildPlacemarks(QueryResultTable resultTable) {
		// List<String> bindings = result.getBindingNames();

		if (resultTable == null) return new ArrayList<Placemark>(0);

		ClosableIterator<QueryRow> result = resultTable.iterator();
		List<Placemark> placemarks = new ArrayList<Placemark>();
		try {
			while (result.hasNext()) {

				QueryRow row = result.next();

				String desc = row.getValue("desc").toString();
				String title = row.getValue("title").toString();

				String latString = row.getValue("lat").toString();
				String longString = row.getValue("long").toString();

				title = Strings.decodeURL(title);
				desc = Strings.decodeURL(desc);
				latString = Strings.decodeURL(latString);
				longString = Strings.decodeURL(longString);

				double latitude = Double.parseDouble(latString.replaceAll(",", "."));
				double longitude = Double.parseDouble(longString.replaceAll(",", "."));

				placemarks.add(new Placemark(title, latitude, longitude, desc));

			}
		}
		catch (ModelRuntimeException e) {
			// return
			// kwikiBundle.getString("KnowWE.owl.query.evalualtion.error")
			// + ":" + e.getMessage();
		}

		// return buffy.toString();

		return placemarks;
	}
}
