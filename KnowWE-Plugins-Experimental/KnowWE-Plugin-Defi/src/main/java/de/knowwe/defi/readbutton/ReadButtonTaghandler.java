/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.readbutton;

import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.logger.DefiPageRateLogLine;
import de.knowwe.defi.table.DefineTableMarkup;
import de.knowwe.defi.table.TableUtils;
import de.knowwe.kdom.renderer.ReRenderSectionMarkerRenderer;

/**
 * With the readbutton, the user can rate a lesson. If the rated value is lower
 * than or equal with the threshold, there is the possibility to talk about it
 * with a therapist. After the user rated, the readbutton will appear in gray.
 * 
 * @author dupke
 */
public class ReadButtonTaghandler extends AbstractTagHandler {

	private static final int DEFAULT_NUMBER = 4;
	private static final String ERROR_MISSING_ID = "Das Attribut 'id' fehlt.";
	private static final String ERROR_DOUBLE_ID = "Ein Readbutton mit dieser id existiert bereits.";
	private static final String BUTTON_DISCUSS = ">> Ja, mit dem Berater dar&uuml;ber sprechen <<";
	private static final String BUTTON_DONT_DISCUSS = ">> Nein, danke <<";
	private static final String BUTTON_ANSWERED = "Ihre R&uuml;ckmeldung wurde erfasst. Vielen Dank!";

	/** tagattribute names **/
	private static final String THRESHOLD = "threshold";
	private static final String NUMBER = "number";
	private static final String LABEL = "label_"; // e.g. label_1
	private static final String VALUE = "value_"; // e.g. value_2
	private static final String ADDLINK = "addLink";
	private static final String LINKTEXT = "linkText";
	private static final String ID = "id";
	private static final String CONDITIONS = "conditions";

	public ReadButtonTaghandler() {
		super("readbutton");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		ReadButtonRenderer renderer = new ReadButtonRenderer(parameters);
		ReRenderSectionMarkerRenderer rerenderer = new ReRenderSectionMarkerRenderer(renderer);
		rerenderer.render(section, userContext, result);
	}


	private class ReadButtonRenderer implements Renderer {

		private final Map<String, String> parameters;

		private ReadButtonRenderer(Map<String, String> parameters) {
			this.parameters = parameters;
		}

		@Override
		public void render(Section<?> section, UserContext userContext, RenderResult result) {

			StringBuilder builder = new StringBuilder();
			String id, closed = "Nein";
			String[] values;
			int number, threshold, checkedValue = -1;
			String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
					"defi.berater");

			// only asserted user can see readbuttons.
			if (!userContext.userIsAsserted()) {
				return;
			}

			// check for errors
			if (parameters.containsKey(ID)) {
				id = parameters.get(ID);
			}
			else {
				result.appendHtml("<span class='warning'>" + ERROR_MISSING_ID + "</span>");
				return;
			}
			// CHECK DOUBLE ID
			if (!ReadbuttonUtilities.checkID(id, userContext.getTitle())) {
				result.appendHtml("<span class='warning'>" + ERROR_DOUBLE_ID +
						"</span>");
				return;
			}

			String[] conditions = {};
			if (parameters.containsKey(CONDITIONS)) conditions = parameters.get(CONDITIONS).split("\\s+");
			boolean conditionsFulfilled = true;
			for (String condition : conditions) {
				Section<DefineTableMarkup> conditionTable = TableUtils.findTableDefintion(condition);
				if (conditionTable == null) {
					result.appendHtml("<span class='warning'>Unbekannte ID für Bedingung: " + condition + "</span>");
				}

				String conditionValue = TableUtils.getStoredContentForTable(
						TableUtils.findTableEntry(condition, userContext.getUserName()));
				if (conditionValue == null || conditionValue.isEmpty()) {
					conditionsFulfilled = false;
					break;
				}
			}

			// get attributes
			if (parameters.containsKey(NUMBER)) number = Integer.parseInt(parameters.get(NUMBER));
			else number = DEFAULT_NUMBER;

			if (parameters.containsKey(THRESHOLD)) threshold = Integer.parseInt(parameters.get(THRESHOLD));
			else threshold = (int) Math.floor(number / 2);


			values = getValues(number, parameters);

			// search for readbutton in log
			DefiPageRateLogLine readbutton = ReadbuttonUtilities.getReadbutton(id,
					userContext.getUserName());
			if (readbutton != null) {
				checkedValue = Integer.parseInt(readbutton.getRealvalue());
				closed = readbutton.getClosed();
			}

			// render button
			boolean enabled = checkedValue == -1 && conditionsFulfilled;
			builder.append("<div class='readbutton' id='rb_").append(id).append("'><table>");
			builder.append(addRadiobuttons(number, values, checkedValue, "rb_" + id, enabled));
			builder.append(addLabels(number, parameters));
			builder.append("</table>");
			if (checkedValue == -1) {
				builder.append("<input type='button' value='OK' onClick=\"sendReadbutton('")
						.append(id)
						.append("', ")
						.append(threshold)
						.append(");return false\" />");
			}
			else {
				if (checkedValue <= threshold && closed.equals("Nein")) builder.append(addLink(
						parameters,
						userContext.getUserName(), berater));
				else builder.append("<p>" + BUTTON_ANSWERED + "</p>");
			}
			builder.append("</div>");

			result.appendHtml(builder.toString());
		}

		/**
		 * Get HTML-output for radiobuttons.
		 *
		 * @param checkedValue If nothing is checked, set checkedValue = -1
		 */
		private String addRadiobuttons(int number, String[] values, int checkedValue, String name, boolean enabled) {
			StringBuilder radiobuttons = new StringBuilder();
			int width = 100 / number;

			if (enabled) radiobuttons.append("<tr class='enabled'>");
			else radiobuttons.append("<tr class='disabled'>");
			for (int i = 1; i <= number; i++) {
				radiobuttons.append("<td style='width:")
						.append(width)
						.append("%'><input type='radio' value='")
						.append(values[i - 1])
						.append("' ");
				if (!enabled) radiobuttons.append("disabled ");
				if (checkedValue == i) radiobuttons.append("checked ");
				radiobuttons.append("name='").append(name).append("' />").append(values[i - 1]).append("</td>");
			}
			radiobuttons.append("</tr>");

			return radiobuttons.toString();
		}

		/**
		 * Get HTML-output for labels.
		 */
		private String addLabels(int number, Map<String, String> parameters) {
			StringBuilder labels = new StringBuilder();

			labels.append("<tr>");
			for (int i = 1; i <= number; i++) {
				if (parameters.containsKey(LABEL + i)) {
					labels.append("<td>")
							.append(parameters.get(LABEL + i))
							.append("</td>");
				}
				else labels.append("<td></td>");
			}
			labels.append("</tr>");

			return labels.toString();
		}

		/**
		 * Get HTML-output for the link.
		 */
		private String addLink(Map<String, String> parameters, String username, String berater) {
			StringBuilder linkBuilder = new StringBuilder();
			String link, linkText;
			String id = parameters.get(ID);

			if (parameters.containsKey(ADDLINK)) link = parameters.get(ADDLINK);
			else link = "";

			if (link.startsWith("[") && link.endsWith("]")) {
				link = link.substring(1, link.length() - 1).trim();
				link = Environment.getInstance().getWikiConnector().getBaseUrl()
						+ "Wiki.jsp?page=" + link;
			}

			if (parameters.containsKey(LINKTEXT)) linkText = parameters.get(LINKTEXT);
			else linkText = link;
			linkBuilder.append("<div class='linkBorder'><p>Möchten Sie das Thema mit dem Berater besprechen?</p>");
			linkBuilder.append("<p class='discuss'><a href='' onclick=\"readbuttonDiscuss('")
					.append(id)
					.append("');newChat('")
					.append(berater)
					.append("', '")
					.append(username)
					.append("');return false\">")
					.append(BUTTON_DISCUSS)
					.append("</a>");
			linkBuilder.append(" - <a href='#' onclick=\"readbuttonCloseLink('")
					.append(id)
					.append("');return false\">")
					.append(BUTTON_DONT_DISCUSS)
					.append("</a></p>");
			linkBuilder.append("<a href='")
					.append(link)
					.append("' target='_blank'>")
					.append(linkText)
					.append("</a></div>");

			return linkBuilder.toString();
		}

		private String[] getValues(int number, Map<String, String> parameters) {
			String[] values = new String[number];

			for (int i = 1; i <= number; i++) {
				if (parameters.containsKey(VALUE + i)) values[i - 1] = parameters.get(VALUE + i);
				else values[i - 1] = String.valueOf(i);
			}

			return values;
		}
	}


}