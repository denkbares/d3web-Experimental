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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryResultTable;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class SparqlMarkupRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder result) {

		String sparqlString = createSparqlString(sec);

		try {
			if (sparqlString.toLowerCase().startsWith("construct")) {
				result.append(Strings.maskHTML("<tt>" + sec.getText() + "</tt>"));
			}
			else {

				Section<SparqlMarkupType> markupSection = Sections.findAncestorOfType(sec,
						SparqlMarkupType.class);

				// Navigation bar is (at the moment) only displayed if
				// explicitly activated in markup
				String navigation = DefaultMarkupType.getAnnotation(markupSection,
						SparqlMarkupType.NAVIGATION);
				if ((navigation != null) && navigation.equals("true")) {
					// do not show navigation bar if LIMIT or OFFSET is set in
					// markup
					if (!(isLimitSet(markupSection) || (isOffsetSet(markupSection)))) {
						int fromLine = getSelectedFromLine(user);
						int showLines = getShowLines(user);
						result.append(renderTableSizeSelector(showLines));
						result.append(renderNavigation(fromLine, showLines));
						sparqlString += addOffsetandLimitToSparqlString(fromLine, showLines);
					}
				}

				String rawOutput = DefaultMarkupType.getAnnotation(markupSection,
						SparqlMarkupType.RAW_OUTPUT);
				QueryResultTable resultSet = Rdf2GoCore.getInstance().sparqlSelect(
						sparqlString);
				result.append(SparqlResultRenderer.getInstance().renderQueryResult(resultSet,
						rawOutput != null && rawOutput.equals("true")));
			}

		}
		catch (ModelRuntimeException e) {
			result.append(Strings.maskHTML("<span class='warning'>"
					+ e.getMessage() + "</span>"));
		}
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

	private String addOffsetandLimitToSparqlString(int offset, int limit) {

		StringBuilder sb = new StringBuilder();
		sb.append(" OFFSET " + offset);
		sb.append(" LIMIT " + limit);

		return sb.toString();
	}

	private String renderTableSizeSelector(int selectedSize) {
		StringBuilder builder = new StringBuilder();

		int[] sizeArray = new int[] {
				10, 20, 50, 100, 1000, 10000 };
		builder.append("<div class='toolBar'>");
		builder.append("<span class=fillText>Show </span>"
				+ "<select id='showLines'"
				+ " onchange=\"KNOWWE.plugin.semantic.actions.refreshSparqlRenderer();\">");
		for (int size : sizeArray) {
			if (size == selectedSize) {
				builder.append("<option selected='selected' value='" + size + "'>"
						+ size + "</option>");
			}
			else {
				builder.append("<option value='" + size + "'>" + size
						+ "</option>");
			}
		}
		builder.append("</select><span class=fillText> lines </span> ");
		builder.append("<div class='toolSeparator'></div>");
		return Strings.maskHTML(builder.toString());
	}

	private Object renderNavigation(int from, int selectedSize) {
		StringBuilder builder = new StringBuilder();

		renderToolbarButton(
				"begin.png", "KNOWWE.plugin.semantic.actions.begin()",
				(from > 1), builder);
		renderToolbarButton(
				"back.png", "KNOWWE.plugin.semantic.actions.back()",
				(from > 1), builder);
		builder.append("<span class=fillText> Lines </span>");
		builder.append("<input size=3 id='fromLine' type=\"field\" onchange=\"KNOWWE.plugin.semantic.actions.refreshSparqlRenderer();\" value='"
				+ from + "'>");
		builder.append("<span class=fillText> to </span>" + (from + selectedSize - 1));
		renderToolbarButton(
				"forward.png", "KNOWWE.plugin.semantic.actions.forward()",
				true, builder);
		builder.append("</div>");
		return Strings.maskHTML(builder.toString());
	}

	private void renderToolbarButton(String icon, String action, boolean enabled, StringBuilder builder) {
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

	private int getSelectedFromLine(UserContext user) {
		return Integer.parseInt(getCookie(user, "FromLine", "1"));
	}

	private int getShowLines(UserContext user) {
		return Integer.parseInt(getCookie(user, "ShowLines", "20"));
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
		if ((secText.contains("OFFSET")) || (secText.contains("LIMIT"))) {
			return true;
		}
		return false;
	}

}
