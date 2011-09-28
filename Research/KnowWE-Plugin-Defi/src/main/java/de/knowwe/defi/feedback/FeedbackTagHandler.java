/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.defi.feedback;

import java.io.File;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;

/**
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class FeedbackTagHandler extends AbstractHTMLTagHandler {

	/**
	 * @param name
	 */
	public FeedbackTagHandler() {
		super("defifeedback");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		StringBuilder html = new StringBuilder();
		if (!user.userIsAsserted()) {
			return "<p class=\"info\">Bitte melden Sie sich an, um die Seite sehen zu können!</p>";
		}

		html.append("<h1>Abschluss-Fragebogen</h1>");

		html.append("Bei den folgenden Fragen geht es um Ihr Resümee des Programms und der Plattform im "
				+ "Allgemeinen und auf bestimmte Teilaspekte bezogen. "
				+ "Bitte Antworten Sie so ehrlich wie möglich und seien Sie ruhig kritisch.");

		html.append("<form action=\"KnowWE.jsp\" method=\"post\">");

		StringBuilder q = new StringBuilder();
		renderQuestion1(q);
		html.append(q);

		String path = KnowWEEnvironment.getInstance().getWikiConnector().getSavePath();
		String filename = user.getUserName().toLowerCase() + ".xml";
		File f = new File(path + filename);

		if (f.exists()) {
			html.append("<p class=\"info\">Sie haben den Feedbackbogen bereits ausgefüllt! Vielen Dank!</p>");
		}
		if (!f.exists()) {
			html.append("<p><input type=\"submit\" value=\"Speichern\"/></p>");
			html.append("<input type=\"hidden\" name=\"action\" value=\"FeedbackSaveAction\" />");
			html.append("<input type=\"hidden\" name=\"KWiki_Topic\" value=\""
					+ topic + "\" />");
		}

		html.append("</form>");

		return html.toString();
	}

	private void renderQuestion1(StringBuilder result) {
		result.append("<table><tbody><tr>");
		result.append("<td colspan=\"2\" class=\"rowhead\">");
		result.append("1) Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.");
		result.append("<input type=\"hidden\" name=\"QFB1\" value=\"1) Die Teilnahme am ICD Forum hat mir insgesamt weitergeholfen.\" />");
		result.append("</td></tr>");
		
		result.append(createRadioButton("Trifft für mich voll zu.", "FB1"));
		result.append(createRadioButton("Trifft für mich zu.", "FB1"));
		result.append(createRadioButton("Unentschieden.", "FB1"));
		result.append(createRadioButton("Trifft für mich eher nicht zu.", "FB1"));
		result.append(createRadioButton("Trifft für mich überhaupt nicht zu.", "FB1"));
		result.append("</tbody></table>");

	}

	private String createRadioButton(String answer, String radioID) {
		return "<tr><td class=\"radio\"><input type=\"radio\" name=\"FB1\" value=\"" + answer
				+ "\">"
				+ "</td>"
				+ "<td class=\"answer\">"
				+ answer
				+ "</td></tr>";
	}

}
