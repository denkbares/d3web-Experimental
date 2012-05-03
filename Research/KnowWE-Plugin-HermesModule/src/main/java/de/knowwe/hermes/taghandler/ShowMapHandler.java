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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.hermes.maps.Placemark;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ShowMapHandler extends AbstractHTMLTagHandler {

	// Google Maps API Key for http://hermeswiki.informatik.uni-wuerzburg.de
	public static final String apiKey = "ABQIAAAAb3JzCPOo-PmQupF8WKTY_BQhTDteWOscIBEFxr5sPfw40-jPhhS0zVcy-utMHpbsLwjf1yApcwxvXg";

	private static final String LOCATIONS_FOR_TOPIC = "SELECT  ?long ?lat WHERE { <URI> lns:hasLatitude ?lat . <URI> lns:hasLongitude ?long .}";

	public ShowMapHandler() {
		super("showMap");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {
		double latitude = 0;
		double longitude = 0;
		double zoom = 5;

		if (values.containsKey("longitude")) {
			try {
				longitude = Double.parseDouble(values.get("longitude"));
			}
			catch (NumberFormatException nfe) {
				// do nothing
			}
		}
		if (values.containsKey("latitude")) {
			try {
				latitude = Double.parseDouble(values.get("latitude"));
			}
			catch (NumberFormatException nfe) {
				// do nothing
			}
		}
		if (values.containsKey("zoom")) {
			try {
				zoom = Double.parseDouble(values.get("zoom"));
			}
			catch (NumberFormatException nfe) {
				// do nothing
			}
		}

		if (longitude == 0 && latitude == 0) {
			String concept = topic;
			if (values.containsKey("concept")) {
				concept = values.get("concept");
			}
			String querystring = LOCATIONS_FOR_TOPIC.replaceAll("URI",
					Rdf2GoCore.getInstance().createlocalURI(concept).toString());
			QueryResultTable queryResultTable = Rdf2GoCore.getInstance().sparqlSelect(querystring);
			ClosableIterator<QueryRow> queryResult = queryResultTable.iterator();
			Collection<? extends Placemark> placemark = buildPlacemarksForLocation(queryResult);
			if (placemark != null && placemark.size() > 0) {
				Placemark p = placemark.iterator().next();
				longitude = p.getLongitude();
				latitude = p.getLatitude();
			}

		}

		String output = "";
		String divId = (Math.random() + "_map").substring(4);
		output += "<div id=\"" + divId
				+ "\" style=\"width: 600px; height: 400px\"/>";
		// List<Placemark> placemarks = new ArrayList<Placemark>();
		// placemarks.add(new Placemark("London", 51.3, 0));
		output += getJavaScript(latitude, longitude, zoom, divId);
		output += "</div>";
		return output;
	}

	private static Collection<? extends Placemark> buildPlacemarksForLocation(ClosableIterator<QueryRow> result) {
		List<Placemark> placemarks = new ArrayList<Placemark>();
		if (result == null) return placemarks;
		try {
			while (result.hasNext()) {

				QueryRow row = result.next();
				String latString = row.getValue("lat").toString();
				String longString = row.getValue("long").toString();
				latString = Strings.decodeURL(latString);
				longString = Strings.decodeURL(longString);
				double latitude = Double.parseDouble(latString.replaceAll(",", "."));
				double longitude = Double.parseDouble(longString.replaceAll(",", "."));

				placemarks.add(new Placemark(null, latitude, longitude, ""));

			}
		}
		catch (ModelRuntimeException e) {
			return null;
		}
		return placemarks;
	}

	private String getJavaScript(double latitude, double longitude, double zoom, String divID) {
		String output = "";
		output += "<script src=\"http://maps.google.com/maps?file=api&v=2&key=" + apiKey
				+ "&sensor=false\" type=\"text/javascript\"> </script>";
		output += "<script type=\"text/javascript\">\n";
		output += "if (GBrowserIsCompatible()) {"
				+ "var map = new GMap2(document.getElementById(\"" + divID
				+ "\"));" + "map.setCenter(new GLatLng(" + latitude + ","
				+ longitude + ")," + zoom + ");" + "}";
		output += "var point = new GLatLng(" + latitude + ", " + longitude + ");";
		output += "map.addOverlay(new GMarker(point));";
		output += "</script>";
		return output;
	}

	private String getJavaScript(List<Placemark> placemarks, String divID) {
		String output = "";
		output += "<script src=\"http://maps.google.com/maps?file=api&v=2&key=abcdefg&sensor=true_or_false\" type=\"text/javascript\"> </script>";
		output += "<script type=\"text/javascript\">\n";
		output += "if (GBrowserIsCompatible()) {"
				+ "var map = new GMap2(document.getElementById(\"" + divID
				+ "\"));" + "map.setCenter(new GLatLng(51.3, 0), 5);" + "}";

		for (Placemark p : placemarks) {
			String latitude = Double.toString(p.getLatitude());
			String longitude = Double.toString(p.getLongitude());

			output += "var point = new GLatLng(" + latitude + ", " + longitude + ");";
			output += "map.addOverlay(new GMarker(point));";
		}
		output += "</script>";
		return output;
	}
}
