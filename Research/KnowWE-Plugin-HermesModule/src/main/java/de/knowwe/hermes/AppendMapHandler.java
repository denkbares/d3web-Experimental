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

package de.knowwe.hermes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.Environment;
import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.hermes.maps.Placemark;
import de.knowwe.hermes.taghandler.ShowMapHandler;
import de.knowwe.rdf2go.Rdf2GoCore;

public class AppendMapHandler implements PageAppendHandler {

	private static final String group = "Editoren";
	private static final String SPARQL_PLACE = "select ?x ?lat ?long where {?x lns:hasLatitude ?lat . ?x lns:hasLongitude ?long }";

	@Override
	public void append(String web, String topic, UserContext user, RenderResult result) {

		if (Environment.getInstance().getWikiConnector().userIsMemberOfGroup(
				group, user.getRequest())) {

			String content = Environment.getInstance().getArticle(web, topic).getRootSection().getText();

			List<Placemark> l = getPlacemarks(content);

			if (l.size() >= 2) {
				String map = createMap(l);
				result.appendHtml(map);
			}
		}
	}

	@Override
	public boolean isPre() {
		return false;
	}

	private static List<Placemark> getPlacemarks(String content) {
		List<Placemark> l = new ArrayList<Placemark>();

		QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(SPARQL_PLACE);
		ClosableIterator<QueryRow> result = resultTable.iterator();

		Pattern pattern = Pattern.compile("\\[[^\\{\\[\\]]+\\]");
		Matcher matcher = pattern.matcher(content);

		ArrayList<String> matches = new ArrayList<String>();

		while (matcher.find()) {
			String name = matcher.group();

			matches.add(name.substring(1, name.length() - 1));
		}

		if (!matches.isEmpty()) {
			try {
				while (result.hasNext()) {

					// new implementation with rdf2go
					// QueryRow row = result.next();
					// String currentPlace = row.getValue("x").toString();
					// if
					// (matches.contains(currentPlace.substring(currentPlace.indexOf("#")+1)))
					// {
					// String latitude = row.getValue("lat").toString();
					// String longitude = row.getValue("long").toString();
					//
					// latitude = latitude.replace(",", ".");
					// longitude = longitude.replace(",", ".");
					// l.add(new Placemark(currentPlace,
					// Double.parseDouble(latitude),
					// Double.parseDouble(longitude)));
					// }

					// use SemanticCore:
					QueryRow row = result.next();

					String currentPlace = Strings.decodeURL(row.getValue("x").toString());
					System.out.println(currentPlace);
					if (matches.contains(currentPlace.substring(currentPlace.indexOf("#") + 1))) {
						System.out.println("TREFFER");
						String latitude = row.getValue("lat").toString();
						String longitude = row.getValue("long").toString();
						latitude = latitude.replace(",", ".");
						longitude = longitude.replace(",", ".");
						l.add(new Placemark(currentPlace, Double.parseDouble(latitude),
								Double.parseDouble(longitude)));
					}
				}
			}
			catch (ModelRuntimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return l;
	}

	// Gibt String der Google-Maps-Karte mit allen Punkten und Flächen der
	// aktuellen Seite
	private static String createMap(List<Placemark> list) {
		String result = "";
		if (!list.isEmpty()) {
			result = ""
					+ "<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;key="
					+ ShowMapHandler.apiKey
					+ "\" type=\"text/javascript\"></script><script type=\"text/javascript\">\r\n"
					+ "var map;\r\n";

			for (Placemark p : list) {
				result += "var marker_"
						+ p.getTitle().replaceAll("[äÄöÖüÜ]", "_").substring(
								p.getTitle().indexOf("#") + 1)
						+ " = new GMarker(new GLatLng(" + p.getLatitude() + ","
						+ p.getLongitude() + "));\r\n";
			}

			result += "function loadMap() {\n";

			result += "if (GBrowserIsCompatible()) {\r\n"
					+ "map = new GMap2(document.getElementById(\"map_canvas\"));\r\n"
					+ "var bounds = new GLatLngBounds();";

			for (Placemark p : list) {
				String title = p.getTitle().replaceAll("[äÄöÖüÜ]", "_").substring(
						p.getTitle().indexOf("#") + 1);

				String bubble = "<b>" + "<a href='Wiki.jsp?page=" + title + "'>" + title
						+ "</a></b>";
				if (p.getDescription() != null) {
					bubble += "<br/>" + p.getDescription().replaceAll("\r\n", "<br/>");
				}
				result += "\r\n" + "map.addOverlay(marker_" + title + ");\r\n"
						+ "GEvent.addListener(marker_" + title + ", 'click', function() {marker_"
						+ title
						+ ".openInfoWindowHtml(\"" + bubble + "\");});\r\n"
						+ "bounds.extend(new GLatLng(" + p.getLatitude() + ","
						+ p.getLongitude() + "));";

			}

			result += "\r\n"
					// +" map.setZoom(map.getBoundsZoomLevel(bounds));\r\n"
					+ "var clat = (bounds.getNorthEast().lat() + bounds.getSouthWest().lat())/2;\r\n"
					+ "var clng = (bounds.getNorthEast().lng() + bounds.getSouthWest().lng())/2;\r\n"
					+ "map.setCenter(new GLatLng(clat,clng),map.getBoundsZoomLevel(bounds)-1);\r\n"
					+ "map.setUIToDefault();} }\r\n"
					+ "window.onload=function initialize() {" + "loadMap();"
					+ "}\n";

			result += "</script><div id=\"map_canvas\" style=\"height: 300px\"></div>";
		}
		return result;
	}

}