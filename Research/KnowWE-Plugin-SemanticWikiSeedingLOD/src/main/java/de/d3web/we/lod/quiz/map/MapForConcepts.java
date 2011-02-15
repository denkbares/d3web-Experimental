package de.d3web.we.lod.quiz.map;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.knowwe.semantic.sparql.SPARQLUtil;

public class MapForConcepts {

	// Google Maps API Key for http://hermeswiki.informatik.uni-wuerzburg.de
	public static final String apiKey = "ABQIAAAAb3JzCPOo-PmQupF8WKTY_BQhTDteWOscIBEFxr5sPfw40-jPhhS0zVcy-utMHpbsLwjf1yApcwxvXg";

	private static final String LOCATIONS_FOR_TOPIC = "SELECT  ?long ?lat WHERE { <URI> lns:hasLatitude ?lat . <URI> lns:hasLongitude ?long .}";

	/**
	 * Creates a google map with different concepts on it, which are used to
	 * evaluate the birthplace of a person.
	 *
	 * @param concepts concepts to be displayed
	 * @param solution the right concept which is asked in the quiz.
	 * @return html quiz code.
	 */
	public static String showMapForConcepts(List<String> concepts, String solution) {

		double longitude = 0;
		double latitude = 0;

		ArrayList<Placemark> marks = new ArrayList<Placemark>();

		for (String concept : concepts) {

			OwlHelper helper = SemanticCoreDelegator.getInstance().getUpper().getHelper();
			String querystring = LOCATIONS_FOR_TOPIC.replaceAll("URI",
					helper.createlocalURI(concept).toString());
			TupleQueryResult queryResult = SPARQLUtil.executeTupleQuery(querystring);
			Collection<? extends Placemark> placemark = buildPlacemarksForLocation(queryResult);

			if (placemark != null && placemark.size() > 0) {
				Placemark p = placemark.iterator().next();
				longitude = p.getLongitude();
				latitude = p.getLatitude();
			}
			if (longitude != 0 && latitude != 0) {
				marks.add(new Placemark(concept, latitude, longitude));
			}
		}
		String output = "";
		String divId = (Math.random() + "_map").substring(4);
		output += "<div id=\"" + divId
				+ "\" style=\"width: 600px; height: 400px\"/>";
		output += getJavaScript(marks, solution, divId);
		output += "</div>";
		return output;
	}

	/**
	 * Builds placemarks for locations.
	 *
	 * @param result a sparql query result.
	 * @return placemarks.
	 */
	private static Collection<? extends Placemark> buildPlacemarksForLocation(
			TupleQueryResult result) {
		List<Placemark> placemarks = new ArrayList<Placemark>();
		if (result == null) return placemarks;
		try {
			while (result.hasNext()) {

				BindingSet set = result.next();
				String latString = set.getBinding("lat").getValue()
						.stringValue();
				String longString = set.getBinding("long").getValue()
						.stringValue();
				try {
					latString = URLDecoder.decode(latString, "UTF-8");
					longString = URLDecoder.decode(longString, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				double latitude = Double.parseDouble(latString.replaceAll(",",
						"."));
				double longitude = Double.parseDouble(longString.replaceAll(
						",", "."));

				placemarks.add(new Placemark(null, latitude, longitude, ""));

			}
		}
		catch (QueryEvaluationException e) {
			return null;
		}
		return placemarks;
	}

	/**
	 * Generates the javascript code for the placemarks. Also puts in some
	 * onclicks for the different locations, to be used in the quiz.
	 *
	 * @param placemarks different placemarks.
	 * @param solution solution location.
	 * @param divID mapdiv.
	 * @return code.
	 */
	private static String getJavaScript(List<Placemark> placemarks, String solution, String divID) {

		String output = "";
		output += "<script src=\"http://maps.google.com/maps?file=api&v=2&key=" + apiKey
				+ "&sensor=false\" type=\"text/javascript\"> </script>";
		output += "<script type=\"text/javascript\">\n";

		double latAvg = 0;
		double longiAvg = 0;

		for (Placemark p : placemarks) {
			latAvg += p.getLatitude();
			longiAvg += p.getLongitude();
		}

		latAvg = latAvg / placemarks.size();
		longiAvg = longiAvg / placemarks.size();

		int i = 0;
		String pre = "";
		String messages = "";
		String locs = "";

		double margin = 0;

		messages = "var messages = [";

		for (Placemark p : placemarks) {

			String latitude = Double.toString(p.getLatitude());
			String longitude = Double.toString(p.getLongitude());

			// Zoom
			double tempLat = Math.abs(latAvg - p.getLatitude());
			double tempLong = Math.abs(longiAvg - p.getLongitude());

			// Pythagoras
			double c = Math.sqrt((tempLat * tempLat) + (tempLong * tempLong));

			if (c > margin) {
				margin = c;
			}

			if (p.title.equals(solution)) {
				messages += "\"Ihre Wahl: <b>" + p.title
						+ "</b> war <b>richtig</b>!\"";
			}
			else {
				messages += "\"Ihre Wahl: <b>" + p.title
						+ "</b> war leider <b>falsch</b>.\"";
			}

			locs += "var point = new GLatLng(" + latitude + ", " + longitude
					+ ");";
			locs += "map.addOverlay(createMarker(point," + i + "));";

			if (i == placemarks.size() - 1) {
				pre += "function createMarker(point, number) {"
						+ "var marker = new GMarker(point);";
				pre += messages + "];";
				pre += "marker.value = number;";
				pre += "GEvent.addListener(marker, \"click\", function() {"
						+ "var myHtml = messages[number];"
						+ "map.openInfoWindowHtml(point, myHtml);"
						+ "});"
						+ "return marker;"
						+ "}";
			}
			messages += ",";
			i++;
		}


		int zoom = 4;

		// Values from testing.

		if (margin < 12.65) {
			zoom = 5;
		}
		if (margin < 6.15) {
			zoom = 6;
		}
		if (margin < 1.16) {
			zoom = 8;
		}
		if (margin < 0.7) {
			zoom = 9;
		}
		if (margin < 0.3) {
			zoom = 10;
		}

		pre += "if (GBrowserIsCompatible()) {"
					+ "var map = new GMap2(document.getElementById(\""
				+ divID
				+ "\"));"
					+ "map.setCenter(new GLatLng("
					+ latAvg
					+ ","
					+ longiAvg
					+ "),"
					+ zoom
					+ ");"
					+ "map.addControl(new GSmallMapControl(),new GControlPosition(G_ANCHOR_TOP_RIGHT));}";
		// Mousewheel Zoom
		pre += "map.enableScrollWheelZoom();";

		output += pre + locs + "</script>";
		return output;
	}
}
