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
package de.knowwe.defi.readbutton;

import java.util.Map;

import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * With the readbutton, the user can rate a lesson. If he rates the lesson with
 * 1 or 2, there is the possibility to talk about it with a therapist.
 * 
 * @author dupke
 * @created 17.03.2011
 */
public class ReadButtonTaghandler extends AbstractTagHandler {

	private static int DEFAULT_NUMBER = 4;
	private static String DEFAULT_HIDE = "false";

	int number, borderValue, ratedValue;
	boolean contains, talkAbout, custom;
	String hide_values, link, linkText;
	String[] labels, values;

	/**
	 * @param name
	 */
	public ReadButtonTaghandler() {
		super("readbutton");
	}

	/**
	 * @param parameters has the following entries: number (The number of
	 *        radiobuttons), value_1, value_2, ...(The String near to the
	 *        radiobutton), label_1, label_2, ...(The String under the
	 *        radiobutton), hide_values(true, false(default)), borderValue(rated
	 *        value <= borderValue? speak with therapist), link and
	 *        linkText(adds a link after user rated)
	 * 
	 */
	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readbutton = new StringBuilder();
		if (userContext.userIsAsserted()) {
			String username = userContext.getUserName();
			String title = username + "_data";
			String pagename = userContext.getTopic();
			String web = userContext.getWeb();
			custom = false;
			contains = false;
			talkAbout = false;
			ratedValue = -1;

			// Get Tagattributes
			if (parameters.containsKey("number")) number = Integer.parseInt(parameters.get("number"));
			else number = DEFAULT_NUMBER;

			if (parameters.containsKey("hide_values")) hide_values = parameters.get("hide_values");
			else hide_values = DEFAULT_HIDE;

			labels = new String[number];
			values = new String[number];
			
			for (int i = 0; i < number; i++) {
				if (parameters.containsKey("label_" + (i + 1))) labels[i] = parameters.get("label_"
						+ (i + 1));
				else labels[i] = "";

				if (parameters.containsKey("value_" + (i + 1))) {
					values[i] = parameters.get("value_" + (i + 1));
					custom = true;
				}
				else values[i] = "";
			}
			
			if (parameters.containsKey("borderValue")) borderValue = Integer.getInteger(parameters.get("borderValue"));
			else borderValue = (int) Math.floor(number / 2);
			
			if (parameters.containsKey("addLink")) link = parameters.get("addLink");
			else link = "";

			if (parameters.containsKey("linkText")) linkText = parameters.get("linkText");
			else linkText = "";

			// Get the readpages-annotation
			KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
			Section<DataMarkup> child = null;
			String readpages = "";

			try {
				Section<?> sec = mgr.getArticle(title).getSection();
				child = Sections.findSuccessor(sec, DataMarkup.class);
				readpages = DefaultMarkupType.getAnnotation(child, "readpages");

			}
			// Generate MarkUp and article if missing
			catch (NullPointerException npe) {

				String pagePermissions = "[{ALLOW view All}]\r\n[{ALLOW delete "
						+ username + "}]\r\n\r\n";
				String content = pagePermissions + "%%data\r\n%\r\n";
				KnowWEEnvironment.getInstance().buildAndRegisterArticle(username, content,
						title, KnowWEEnvironment.DEFAULT_WEB);
				KnowWEEnvironment.getInstance().getWikiConnector()
						.createWikiPage(title, content, username);

				Section<?> sec = mgr.getArticle(title).getSection();
				child = Sections.findSuccessor(sec, DataMarkup.class);
			}

			if (readpages != null) {
				// Checks whether the lesson is already rated and if the
				// talkAbout-link has to appear
				String[] pages = readpages.split(";");
				for (String s : pages) {

					if (s.split(",")[0].toLowerCase().equals(pagename.toLowerCase())) {

						contains = true;
						ratedValue = Integer.parseInt(s.split(",")[1]);
						if (ratedValue <= borderValue
								&& s.split(",")[2].equals("0")) {

							talkAbout = true;
						}
					}

				}
			}

			readbutton.append("<form name='readbuttonform' class='rbtag'>");
			// Generate table
			readbutton.append("<table class='rbtag'>");
			readbutton = appendRadiobuttons(readbutton);

			// - user has to rate
			if (!contains) {
				readbutton.append("<tr><td colspan='" + number + "'>");
				readbutton.append("<input class='submit' type='button' value='OK' onclick='getReadButtonValue(0,"
						+ number + ")' />");
				readbutton.append("</td></tr></table>");
				readbutton.append("</form>");
			}
			// - user can talk about it
			else if (talkAbout) {
				readbutton.append("</table></form>");
				String talkPage = userContext.getUserName() + "_comment_therapist";
				title = parameters.get("title");
				if (title == null) {
					title = " &raquo; " + talkPage;
				}

				// subject = pagename
				readbutton.append("<p><a href=\"Wiki.jsp?page=");
				readbutton.append(KnowWEUtils.urlencode(talkPage.trim()));
				readbutton.append("&amp;talkabout=");
				readbutton.append(KnowWEUtils.urlencode(pagename.trim()));
				readbutton.append("\" title=\"Title:");
				readbutton.append(title);
				readbutton.append("\" rel=\"nofollow\">");
				readbutton.append("Mit Therapeuten dar&uuml;ber sprechen");
				readbutton.append("</a>");
				readbutton.append(" - <a href=\"\" onclick='getReadButtonValue(1," + number
						+ ")'>Nicht Besprechen</a></p>");
				readbutton.append("<a href='" + link + "' target='_blank'>" + linkText + "</a>");
			}
			// - user has already rated
			else {
				readbutton.append("<tr><td colspan='" + number + "'>");
				readbutton.append("<p>Vielen Dank!</p>");
				readbutton.append("</td></tr>");
				readbutton.append("</table></form>");
			}

		}

		return KnowWEUtils.maskHTML(readbutton.toString());
	}

	/**
	 * Appends the radiobutton, if user has rated, the buttons are disabled
	 * 
	 * @created 07.04.2011
	 * @param readbutton
	 * @return
	 */
	private StringBuilder appendRadiobuttons(StringBuilder readbutton) {

		readbutton.append("<tr>");
		for (int i = 0; i < number; i++) {
			readbutton.append("<td style='width:" + (100 / number) + "%'>");

			readbutton.append("<input type='radio' ");
			if (ratedValue != -1) readbutton.append("disabled ");
			if (ratedValue != -1 && ratedValue == (i + 1)) readbutton.append("disabled checked ");
			readbutton.append("name='panel' value='" + (i + 1) + "' />");

			// hidden: hide value
			if (hide_values.equals("true")) {
				readbutton.append("</td>");
			}
			// custom: show value
			else if (custom) {
				readbutton.append(values[i] + "</td>");
			}
			// default: show numbers from 1 to last radiobutton
			else {
				readbutton.append((i + 1) + "</td>");
			}
		}
		readbutton.append("</tr><tr>");

		// append labels
		for (int i = 0; i < number; i++) {
			readbutton.append("<td>" + labels[i] + "</td>");
		}
		readbutton.append("</tr>");

		return readbutton;
	}

}
