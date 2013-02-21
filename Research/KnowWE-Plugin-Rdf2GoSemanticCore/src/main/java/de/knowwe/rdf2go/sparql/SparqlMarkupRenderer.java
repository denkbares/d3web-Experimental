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
package de.knowwe.rdf2go.sparql;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.utils.RenderOptions;
import de.knowwe.rdf2go.sparql.utils.SparqlRenderResult;

public class SparqlMarkupRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult result) {

		String sparqlString = createSparqlString(sec);

		try {
			if (sparqlString.toLowerCase().startsWith("construct")) {
				result.appendHTML("<tt>");
				result.append(sec.getText());
				result.appendHTML("</tt>");
			}
			else {

				Section<SparqlMarkupType> markupSection = Sections.findAncestorOfType(sec,
						SparqlMarkupType.class);

				RenderOptions renderOpts = new RenderOptions(sec.getID());

				// Default values
				String navigationOffset = "1";
				String navigationLimit = "20";
				Map<String, String> sortMap = new LinkedHashMap<String, String>();
				// Get values out of JSON Cookie
				if (getJSONCookieString(sec, user) != null) {
					JSONObject json = new JSONObject(Strings.decodeURL(getJSONCookieString(sec,
							user)));
					if (!json.isNull("navigationOffset")) {
						navigationOffset = json.getString("navigationOffset");
					}
					if (!json.isNull("navigationLimit")) {
						navigationLimit = json.getString("navigationLimit");
					}
					if (!json.isNull("sorting")) {
						JSONArray jsonArray = json.getJSONArray("sorting");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject sortPair = jsonArray.getJSONObject(i);
							@SuppressWarnings("unchecked")
							Iterator<String> it = sortPair.keys();
							String key = it.next();
							sortMap.put(key, sortPair.getString(key));
						}
						renderOpts.setSortingMap(sortMap);
					}
				}

				setRenderOptions(markupSection, renderOpts);

				if (renderOpts.isBorder()) result.appendHTML("<div class='border'>");
				if (renderOpts.isSorting()) sparqlString = modifyOrderByInSparqlString(sortMap,
						sparqlString);

				SparqlRenderResult resultEntry;
				if (renderOpts.isNavigation()) {
					// do not show navigation bar if LIMIT or OFFSET is set in
					// markup
					sparqlString = addOffsetAndLimitToSparqlString(sparqlString, markupSection,
							navigationOffset, navigationLimit);

					QueryResultTable resultSet = Rdf2GoCore.getInstance().sparqlSelect(
							sparqlString);
					resultEntry = SparqlResultRenderer.getInstance().renderQueryResult(
							resultSet,
							renderOpts, user);
					if (!renderOpts.isRawOutput()) {
						renderTableSizeSelector(navigationLimit, sec.getID(), result);
						renderNavigation(navigationOffset, navigationLimit,
								resultEntry.getSize(), sec.getID(), result);
					}

				}
				else {
					QueryResultTable resultSet = Rdf2GoCore.getInstance().sparqlSelect(
							sparqlString);
					resultEntry = SparqlResultRenderer.getInstance().renderQueryResult(
							resultSet, renderOpts, user);
				}
				result.appendHTML(resultEntry.getHTML());
				if (renderOpts.isBorder()) result.appendHTML("</div>");

			}
		}
		catch (ModelRuntimeException e) {
			result.appendHTML("<span class='warning'>"
					+ e.getMessage() + "</span>");
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String addOffsetAndLimitToSparqlString(String sparqlString, Section<SparqlMarkupType> markupSection, String navigationOffset, String navigationLimit) {
		if (!(isLimitSet(markupSection) || (isOffsetSet(markupSection)))) {

			int offsetInt = Integer.parseInt(navigationOffset);
			int limitInt;
			if (!navigationLimit.equals("All")) limitInt = Integer.parseInt(navigationLimit);
			else limitInt = -999;

			StringBuilder sb = new StringBuilder();
			sb.append(" OFFSET " + (offsetInt - 1));
			if (limitInt != -999) sb.append(" LIMIT " + (limitInt - 1));
			sparqlString += sb.toString();
		}
		return sparqlString;
	}

	private void setRenderOptions(Section<SparqlMarkupType> markupSection, RenderOptions renderOpts) {
		renderOpts.setRawOutput(checkAnnotation(markupSection, SparqlMarkupType.RAW_OUTPUT));
		renderOpts.setSorting(checkSortingAnnotation(markupSection,
				SparqlMarkupType.SORTING));
		renderOpts.setZebraMode(checkAnnotation(markupSection, SparqlMarkupType.ZEBRAMODE));
		renderOpts.setBorder(checkAnnotation(markupSection, SparqlMarkupType.BORDER));
		renderOpts.setNavigation(checkAnnotation(markupSection, SparqlMarkupType.NAVIGATION));
	}

	private boolean checkSortingAnnotation(Section<SparqlMarkupType> markupSection, String sorting) {
		String annotationString = DefaultMarkupType.getAnnotation(markupSection,
				sorting);
		return annotationString == null || annotationString.equals("true");
	}

	private boolean checkAnnotation(Section<?> markupSection, String annotationName) {
		String annotationString = DefaultMarkupType.getAnnotation(markupSection,
				annotationName);
		return annotationString != null && annotationString.equals("true");

	}

	private String createSparqlString(Section<?> sec) {
		String sparqlString = sec.getText();
		sparqlString = sparqlString.trim();
		sparqlString = sparqlString.replaceAll("\n", " ");
		sparqlString = sparqlString.replaceAll("\r", "");

		Map<String, String> nameSpaces = Rdf2GoCore.getInstance().getNameSpaces();

		StringBuilder newSparqlString = new StringBuilder();
		StringBuilder pattern = new StringBuilder(" <((");
		boolean first = true;
		for (String nsShort : nameSpaces.keySet()) {
			if (first) first = false;
			else pattern.append("|");
			pattern.append(nsShort);
		}
		pattern.append("):)[^ /]");
		int lastEnd = 0;
		Matcher matcher = Pattern.compile(pattern.toString()).matcher(sparqlString);
		while (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(2);
			String nsLong = nameSpaces.get(matcher.group(2));
			newSparqlString.append(sparqlString.substring(lastEnd, start));
			newSparqlString.append(nsLong);
			lastEnd = end + 1;
		}

		newSparqlString.append(sparqlString.subSequence(lastEnd, sparqlString.length()));
		sparqlString = newSparqlString.toString();

		return sparqlString;
	}

	private String modifyOrderByInSparqlString(Map<String, String> sortOrder, String sparqlString) {
		StringBuilder sb = new StringBuilder(sparqlString);
		if (sortOrder.isEmpty()) {
			return sb.toString();
		}
		String sparqlTempString = sparqlString.toLowerCase();
		int orderBy = sparqlTempString.lastIndexOf("order by");
		int limit = sparqlTempString.indexOf("limit", orderBy);
		int offset = sparqlTempString.indexOf("offset", orderBy);
		int nextStatement;
		if (limit > 0 && offset > 0) nextStatement = (limit < offset) ? limit : offset;
		else if (limit > 0 && offset < 0) nextStatement = limit;
		else if (limit < 0 && offset > 0) nextStatement = offset;
		else nextStatement = -1;

		StringBuilder sbOrder = new StringBuilder();
		Collection<String> keyCollection = sortOrder.keySet();
		Iterator<String> keyIt = keyCollection.iterator();
		Collection<String> valueCollection = sortOrder.values();
		Iterator<String> valIt = valueCollection.iterator();

		while (keyIt.hasNext()) {
			sbOrder.append(" " + valIt.next() + "(?" + keyIt.next() + ")");
		}

		if (orderBy == -1) {
			if (nextStatement == -1) {
				sb.append(" ORDER BY" + sbOrder.toString());
			}
			else {
				sb.replace(nextStatement, nextStatement, " ORDER BY" + sbOrder.toString());
			}
		}
		else {
			if (nextStatement != -1) {
				sb.replace(orderBy, nextStatement, " ORDER BY" + sbOrder.toString());
			}
			else {
				sb.replace(orderBy, sb.length(), " ORDER BY" + sbOrder.toString());
			}
		}

		return sb.toString();
	}

	private void renderTableSizeSelector(String selectedSize, String id, RenderResult result) {

		String[] sizeArray = new String[] {
				"10", "20", "50", "100", "1000", "All" };
		result.appendHTML("<div class='toolBar'>");
		result.appendHTML("<span class=fillText>Show </span>"
				+ "<select id='showLines'"
				+ " onchange=\"KNOWWE.plugin.semantic.actions.refreshSparqlRenderer('"
				+ id + "');\">");
		for (String size : sizeArray) {
			if (size.equals(selectedSize)) {
				result.appendHTML("<option selected='selected' value='" + size + "'>" + size
						+ "</option>");
			}
			else {
				result.appendHTML("<option value='" + size + "'>" + size
						+ "</option>");
			}
		}
		result.appendHTML("</select><span class=fillText> lines </span> ");
		result.appendHTML("<div class='toolSeparator'></div>");
	}

	private void renderNavigation(String from, String selectedSize, int max, String id, RenderResult result) {
		int fromInt = Integer.parseInt(from);
		int selectedSizeInt;
		if (!(selectedSize.equals("All"))) {
			selectedSizeInt = Integer.parseInt(selectedSize);
		}
		else {
			selectedSizeInt = max;
		}
		renderToolbarButton(
				"begin.png", "KNOWWE.plugin.semantic.actions.begin('"
						+ id + "')",
				(fromInt > 1), result);
		renderToolbarButton(
				"back.png", "KNOWWE.plugin.semantic.actions.back('"
						+ id + "')",
				(fromInt > 1), result);
		result.append("<span class=fillText> Lines </span>");
		result.append("<input size=3 id='fromLine' type=\"field\" onchange=\"KNOWWE.plugin.semantic.actions.refreshSparqlRenderer('"
				+ id + "');\" value='"
				+ from + "'>");
		result.append("<span class=fillText> to </span>" + (fromInt + selectedSizeInt - 1));
		renderToolbarButton(
				"forward.png", "KNOWWE.plugin.semantic.actions.forward('"
						+ id + "')",
				!selectedSize.equals("All"), result);
		result.append("</div>");
	}

	private void renderToolbarButton(String icon, String action, boolean enabled, RenderResult builder) {
		int index = icon.lastIndexOf('.');
		String suffix = icon.substring(index);
		icon = icon.substring(0, index);
		if (enabled) {
			builder.append("<a onclick=\"");
			builder.append(action);
			builder.append(";\">");
		}
		builder.append("<span class='toolButton ");
		builder.append(enabled ? "enabled" : "disabled");
		builder.append("'>");
		builder.append("<img src='KnowWEExtension/navigation_icons/");
		builder.append(icon);
		if (!enabled) builder.append("_deactivated");
		builder.append(suffix).append("'></img></span>");
		if (enabled) {
			builder.append("</a>");
		}
	}

	private String getJSONCookieString(Section<?> sec, UserContext user) {
		return getCookie(user, "SparqlRenderer-" + sec.getID(), null);
	}

	private String getCookie(UserContext user, String cookieName, String defaultValue) {
		if (user != null && user.getRequest() != null && user.getRequest().getCookies() != null) {
			for (Cookie cookie : user.getRequest().getCookies()) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return defaultValue;
	}

	private boolean isOffsetSet(Section<SparqlMarkupType> sec) {
		return isConstraintSet(sec, "OFFSET");
	}

	private boolean isLimitSet(Section<SparqlMarkupType> sec) {
		return isConstraintSet(sec, "LIMIT");
	}

	private boolean isConstraintSet(Section<SparqlMarkupType> sec, String cons) {
		if (sec == null) {
			return false;
		}
		String secText = sec.getText();
		// for future changes keep the possibility to switch this to
		// "secText.contains(cons)"
		if ((secText.contains("OFFSET")) || (secText.contains("LIMIT"))) {
			return true;
		}
		return false;
	}

}
