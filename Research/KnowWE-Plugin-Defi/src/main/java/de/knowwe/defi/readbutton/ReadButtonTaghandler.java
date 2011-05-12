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

import java.util.List;
import java.util.Map;

import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * With the readbutton, the user can rate a lesson. If the rated Value is lower
 * than or equal with the threshold, there is the possibility to talk about it
 * with a therapist. After the user rated, the readbutton will appear in gray
 * 
 * @author dupke
 * @created 17.03.2011
 */
public class ReadButtonTaghandler extends AbstractTagHandler {

	private static final int DEFAULT_NUMBER = 4;
	private static final String DEFAULT_HIDE = "false";

	/** tagattributes **/
	private static final String THRESHOLD = "threshold";
	private static final String NUMBER = "number";
	private static final String HIDE_VALUES = "hide_values";
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
	 * The readbutton
	 * 
	 */
	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readbutton = new StringBuilder();
		if (userContext.userIsAsserted()) {
			// Initializing variables
			boolean custom = false;
			boolean contains = false;
			boolean talkAbout = false;
			int ratedValue = -1;
			int threshold, number;
			String hide_values, link, linkText, id;
			String[] labels, values;

			// Get parameters
			String username = userContext.getUserName();
			String title = username + "_data";
			String pagename = userContext.getTopic();
			String web = userContext.getWeb();

			// Get Tagattributes
			if (parameters.containsKey(NUMBER)) number = Integer.parseInt(parameters.get(NUMBER));
			else number = DEFAULT_NUMBER;

			if (parameters.containsKey(HIDE_VALUES)) hide_values = parameters.get(HIDE_VALUES);
			else hide_values = DEFAULT_HIDE;

			labels = new String[number];
			values = new String[number];
			
			for (int i = 0; i < number; i++) {
				if (parameters.containsKey(LABEL + (i + 1))) labels[i] = parameters.get(LABEL
						+ (i + 1));
				else labels[i] = "";

				if (parameters.containsKey(VALUE + (i + 1))) {
					values[i] = parameters.get(VALUE + (i + 1));
					custom = true;
				}
				else values[i] = "";
			}
			
			if (parameters.containsKey(THRESHOLD)) threshold = Integer.parseInt(parameters.get(THRESHOLD));
			else threshold = (int) Math.floor(number / 2);
			
			if (parameters.containsKey(ADDLINK)) link = parameters.get(ADDLINK);
			else link = "";

			if (parameters.containsKey(LINKTEXT)) linkText = parameters.get(LINKTEXT);
			else linkText = "";

			if (parameters.containsKey(ID)) id = parameters.get(ID);
			else return KnowWEUtils.maskHTML("<p>Fehler: Dem Button fehlt das Attribut 'id'.</p>");

			if (checkID(id, article) == 1) return KnowWEUtils.maskHTML("<p>Fehler: Das Attribut 'id' darf nicht die Zeichen '::' und ';' enthalten.</p>");
			if (checkID(id, article) == 2) return KnowWEUtils.maskHTML("<p>Fehler: Das Attribut 'id' muss einmalig sein!");

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

					if (s.split("::")[0].toLowerCase().equals(pagename.toLowerCase())
							&& s.split("::")[1].equals(id)) {

						contains = true;
						ratedValue = Integer.parseInt(s.split("::")[2]);
						if (ratedValue <= threshold
								&& s.split("::")[3].equals("0")) {

							talkAbout = true;
						}
					}

				}
			}

			readbutton.append("<form name='readbuttonform' class='rbtag' id='" + id + "'>");
			// Generate table
			readbutton.append("<table class='rbtag'>");
			readbutton = appendRadiobuttons(readbutton, ratedValue, number, hide_values, custom,
					values, labels);

			// - user has to rate
			if (!contains) {
				readbutton.append("<tr><td colspan='" + number + "'>");
				readbutton.append("<input class='submit' type='button' value='OK' onclick='getReadButtonValue(0,"
						+ number + ",\"" + id + "\")' />");
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
				readbutton.append(" - <a href='#' onclick='getReadButtonValue(1," + number + ",\""
						+ id + "\");return false'>Nicht Besprechen</a></p>");
				if (link.startsWith("[") && link.endsWith("]")) {
					// is wiki link (because of ajax not rendering by jspwiki
					// pipeline
					// => render here
					String linkPagename = link.substring(1, link.length() - 1).trim();
					String baseUrl = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl();
					readbutton.append("<a href='" + baseUrl + "Wiki.jsp?page="
							+ linkPagename
							+ "' target='_blank'>"
							+ linkPagename + "</a>");
				}
				else {
					readbutton.append("<a href='" + link + "' target='_blank'>"
							+ linkText + "</a>");
				}
			}
			// - user has already rated
			else {
				readbutton.append("<tr><td colspan='" + number + "'>");
				readbutton.append("<p>Alles klar. Vielen Dank!</p>");
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
	private StringBuilder appendRadiobuttons(StringBuilder readbutton, int ratedValue, int number, String hide_values, boolean custom, String[] values, String[] labels) {

		if (ratedValue != -1) readbutton.append("<tr  class='rated'>");
		else readbutton.append("<tr>");
		for (int i = 0; i < number; i++) {
			readbutton.append("<td style='width:" + (100 / number) + "%'>");

			readbutton.append("<input type='radio' ");
			if (ratedValue != -1) readbutton.append("disabled ");
			if (ratedValue != -1 && ratedValue == (i + 1)) readbutton.append("checked ");
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

	/**
	 * Checks the id for errors.
	 * 
	 * @return 0 = no error, 1 = illegal characters, 2 = double id
	 */
	private int checkID(String id, KnowWEArticle article) {
		List<Section<? extends Type>> allNodes = article.getAllNodesPreOrder();
		Section<? extends Type> node;
		int count = 0;

		// (1) Check id for illegal characters
		if (id.contains("::") || id.contains(";")) return 1;

		// (2) Check for double id
		for (int i = 0; i < allNodes.size(); i++) {
			node = allNodes.get(i);

			// conditions:
			// - TagHandlerType
			// - readbutton
			// - readbutton id = current id
			if (node.get().toString().contains("TagHandlerType")
					&& node.toString().contains("KnowWEPlugin readbutton")
						&& node.toString().contains("id=" + id)) {
				System.out.println(node);
					count++;
			}
		}

		System.out.println(count);
		// (2) current id has been found more often than once => double id
		if (count > 1) return 2;
		return 0;
	}

}
