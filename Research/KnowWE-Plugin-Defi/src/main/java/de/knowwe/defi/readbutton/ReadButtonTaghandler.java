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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

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

	/**
	 * @param name
	 */
	public ReadButtonTaghandler() {
		super("readbutton");
	}

	/**
	 *
	 */
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder builder = new StringBuilder();
		String id, dataPagename, closed = "Nein";
		String[] values;
		List<String> ids = new LinkedList<String>();
		int number, threshold, checkedValue = -1;
		String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
				"defi.berater");

		// only asserted user can see readbuttons.
		if (!userContext.userIsAsserted()) {
			return KnowWEUtils.maskHTML("");
		}

		// check for errors
		if (parameters.containsKey(ID)) id = parameters.get(ID);
		else return KnowWEUtils.maskHTML("<span class='warning'>" + ERROR_MISSING_ID + "</span>");

		// TODO: ID-Check

		// get attributes
		if (parameters.containsKey(NUMBER)) number = Integer.parseInt(parameters.get(NUMBER));
		else number = DEFAULT_NUMBER;

		if (parameters.containsKey(THRESHOLD)) threshold = Integer.parseInt(parameters.get(THRESHOLD));
		else threshold = (int) Math.floor(number / 2);

		values = getValues(number, parameters);

		// search on "_data"-page for the button
		dataPagename = userContext.getUserName() + "_data";
		ArticleManager mgr = Environment.getInstance().getArticleManager(userContext.getWeb());
		if (Environment.getInstance().getWikiConnector().doesArticleExist(dataPagename)) {
			Section<?> sec = mgr.getArticle(dataPagename).getRootSection();
			List<Section<ReadbuttonType>> rbSecs = Sections.findSuccessorsOfType(sec,
					ReadbuttonType.class);
			for (Section<ReadbuttonType> rbSec : rbSecs) {
				// collect all ids
				ids.add(DefaultMarkupType.getAnnotation(rbSec, "id"));
				// get checked value of the button
				if (id.equals(DefaultMarkupType.getAnnotation(rbSec, "id"))) {
					checkedValue = Integer.parseInt(DefaultMarkupType.getAnnotation(rbSec,
							"realvalue"));
					closed = DefaultMarkupType.getAnnotation(rbSec, "closed");
				}
			}
		}

		// render button
		builder.append("<div class='readbutton' id='rb_" + id + "'><table>");
		builder.append(addRadiobuttons(number, values, checkedValue, "rb_" + id));
		builder.append(addLabels(number, parameters));
		builder.append("</table>");
		if (checkedValue == -1) builder.append("<input type='button' value='OK' onClick=\"sendReadbutton('"
				+ id + "', " + threshold + ");return false\" />");
		else {
			if (checkedValue <= threshold && closed.equals("Nein")) builder.append(addLink(
					parameters,
					userContext.getUserName(), berater));
			else builder.append("<p>" + BUTTON_ANSWERED + "</p>");
		}
		builder.append("</div>");

		return KnowWEUtils.maskHTML(builder.toString());
	}

	/**
	 * Get HTML-output for radiobuttons.
	 * 
	 * @param checkedValue If nothing is checked, set checkedValue = -1
	 */
	private String addRadiobuttons(int number, String[] values, int checkedValue, String name) {
		StringBuilder radiobuttons = new StringBuilder();
		int width = 100 / number;

		if (checkedValue == -1) radiobuttons.append("<tr class='enabled'>");
		else radiobuttons.append("<tr class='disabled'>");
		for (int i = 1; i <= number; i++) {
			radiobuttons.append("<td style='width:" + width + "%'><input type='radio' value='"
					+ values[i - 1] + "' ");
			if (checkedValue > -1) radiobuttons.append("disabled ");
			if (checkedValue == i) radiobuttons.append("checked ");
			radiobuttons.append("name='" + name + "' />" + values[i - 1] + "</td>");
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
			if (parameters.containsKey(LABEL + i)) labels.append("<td>" + parameters.get(LABEL + i)
					+ "</td>");
			else labels.append("<td></td>");
		}
		labels.append("</tr>");

		return labels.toString();
	}

	/**
	 * Get HTML-output for the link.
	 */
	private String addLink(Map<String, String> parameters, String username, String berater) {
		StringBuilder zelda = new StringBuilder();
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
		zelda.append("<div class='linkBorder'><p>Möchten Sie das Thema mit dem Berater besprechen?</p>");
		zelda.append("<p class='discuss'><a href='' onclick=\"readbuttonDiscuss('" + id
				+ "');newChat('" + berater + "', '"
				+ username + "');return false\">" + BUTTON_DISCUSS + "</a>");
		zelda.append(" - <a href='#' onclick=\"readbuttonCloseLink('" + id + "');return false\">"
				+ BUTTON_DONT_DISCUSS + "</a></p>");
		zelda.append("<a href='" + link + "' target='_blank'>" + linkText + "</a></div>");

		return zelda.toString();
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