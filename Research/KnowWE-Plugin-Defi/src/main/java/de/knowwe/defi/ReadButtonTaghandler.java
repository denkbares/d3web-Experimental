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
package de.knowwe.defi;

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

	/**
	 * @param name
	 */
	public ReadButtonTaghandler() {
		super("readbutton");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readbutton = new StringBuilder();

		if (userContext.userIsAsserted()) {
			String title = userContext.getUserName() + "_data";
			String pagename = userContext.getTopic();
			String web = userContext.getWeb();
			boolean contains = false;
			boolean talkAbout = false;
			String rateValue = "";

			// Get the readpages-annotation
			KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
			Section<?> sec = mgr.getArticle(title).getSection();
			Section<DataMarkup> child = Sections.findSuccessor(sec, DataMarkup.class);
			if (child != null) {

				String readpages = DefaultMarkupType.getAnnotation(child, "readpages");
				if (readpages != null) {

					// Checks whether the lesson is already rated and if the
					// talkAbout-link has to appear
					String[] pages = readpages.split(";");
					for (String s : pages) {

						if (s.split(",")[0].toLowerCase().equals(pagename.toLowerCase())) {

							contains = true;
							rateValue = s.split(",")[1];
							if ((rateValue.equals("1") || rateValue.equals("2"))
										&& s.split(",")[2].equals("0")) {

								talkAbout = true;
							}
							}

					}
				}
			}
			// There is no rating for this page or ...
			if (!contains) {

				readbutton.append("<p>Wie hat ihnen das gefallen?</p>");
				readbutton.append("<form name='readbuttonform' class='rbtag'>");
				readbutton.append("<input type='radio' name='panel' value='1' /> 1");
				readbutton.append("<input type='radio' name='panel' value='2' /> 2");
				readbutton.append("<input type='radio' name='panel' value='3' /> 3");
				readbutton.append("<input type='radio' name='panel' value='4' /> 4");
				readbutton.append("</form>");
				readbutton.append("<input type='button' value='OK' onclick='getReadButtonValue(0)' />");
			}
			// ... there is a rating and a talkAbout-link or ...
			else if (talkAbout) {

				String talkPage = userContext.getUserName() + "_comment_therapist";
				title = parameters.get("title");
				if (title == null) {
					title = " &raquo; " + talkPage;
				}
				readbutton.append("<p>Möchten Sie mit Therapeuten dar&uuml;ber sprechen?</p>");
				readbutton.append("<p><a href=\"Wiki.jsp?page=");
				readbutton.append(KnowWEUtils.urlencode(talkPage.trim()));
				readbutton.append("\" title=\"Title:");
				readbutton.append(title);
				readbutton.append("\" rel=\"nofollow\">");
				readbutton.append("Jetzt Besprechen");
				readbutton.append("</a>");
				readbutton.append(" - <a href=\"\" onclick='getReadButtonValue(1)'>Nicht Besprechen</a></p>");
			}
			// ... there is a rating and no need to talk about
			else {
				readbutton.append("<p>Sie haben diese Lektion mit einer " + rateValue
						+ " bewertet.</p>");
			}

		}

		return KnowWEUtils.maskHTML(readbutton.toString());
	}
	
}
