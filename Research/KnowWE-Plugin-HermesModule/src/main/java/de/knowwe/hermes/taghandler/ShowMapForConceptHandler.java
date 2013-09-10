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

import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.hermes.maps.Placemark;
import de.knowwe.hermes.util.TimeEventSPARQLUtils;

public class ShowMapForConceptHandler extends AbstractHTMLTagHandler {

	public ShowMapForConceptHandler() {
		super("showMapForConcept");
	}

	@Override
	public void renderHTML(String web, String topic,
			UserContext user, Map<String, String> values, RenderResult result) {

		String concept = topic;
		String givenConcept = values.get("concept");
		if (givenConcept != null) {
			concept = givenConcept;
		}

		List<Placemark> placemarks = TimeEventSPARQLUtils
				.findLocationsOfTimeEventsInvolvingConcept(concept);

		String output = "";
		String divId = (Math.random() + "_map").substring(4);
		output += "<div id=\"" + divId
				+ "\" style=\"width: 600px; height: 400px\"/>";
		// List<Placemark> placemarks = new ArrayList<Placemark>();
		// placemarks.add(new Placemark("London", 51.3, 0));
		output += getJavaScript(placemarks, divId);
		output += "</div>";
		result.appendHtml(output);
	}

	private String getJavaScript(List<Placemark> placemarks, String divID) {

		if (placemarks.isEmpty()) {
			return "no placemarks to render";
		}
		String output = "";
		output += "<script src=\"http://maps.google.com/maps?file=api&v=2&key="
				+ ShowMapHandler.apiKey
				+ "&sensor=true_or_false\" type=\"text/javascript\"> </script>";
		output += "<script type=\"text/javascript\">\n";
		output += "if (GBrowserIsCompatible()) {"
				+ "var map = new GMap2(document.getElementById(\"" + divID
				+ "\"));";
		output += "map.setCenter(new GLatLng("
				+ placemarks.get(0).getLatitude() + ","
				+ placemarks.get(0).getLongitude() + "), 6);" + "}";
		output += "map.setMapType(G_SATELLITE_MAP);";
		output += "map.addControl(new GSmallMapControl());";

		for (Placemark p : placemarks) {
			// String escapedDescription = p.getDescription().replaceAll("\"",
			// "''");
			// escapedDescription = escapedDescription.replaceAll("\n", "<br>");
			String htmlTextForInfoWindows = "<h4>" + p.getTitle() + "</h4>";
			// htmlTextForInfoWindows += "<div>" + escapedDescription +
			// "</div>";
			String latitude = Double.toString(p.getLatitude());
			String longitude = Double.toString(p.getLongitude());

			output += "var point = new GLatLng(" + latitude + ", " + longitude
					+ ");";
			output += "var marker = new GMarker(point);";
			output += "marker.bindInfoWindowHtml(\"" + htmlTextForInfoWindows
					+ "\");";
			output += "map.addOverlay(marker);";
		}
		output += "</script>";
		return output;
	}
}
