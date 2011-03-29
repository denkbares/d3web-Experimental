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

			KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
			Section<?> sec = mgr.getArticle(title).getSection();
			Section<DataMarkup> child = Sections.findSuccessor(sec, DataMarkup.class);
			boolean contains = false;
			boolean talkAbout = false;
			if (child != null) {
			String readpages = DefaultMarkupType.getAnnotation(child, "readpages");
			if (readpages != null) {
				String[] pages = readpages.split(";");

				for (String s : pages) {

						if (s.split(",")[0].toLowerCase().equals(pagename.toLowerCase())) {
							contains = true;
							if (s.split(",")[1].equals("1") || s.split(",")[1].equals("2")) {
								talkAbout = true;
							}
						}

				}
			}
			}
			readbutton.append("<p>Wie hat ihnen das gefallen?</p>");
			if (contains && talkAbout) {
				readbutton.append("<form name='readbuttonform' class='rbtag'>");
				readbutton.append("<p>'Besprechen-Button' - 'Nicht-Besprechen-Button'</p>");
				readbutton.append("</form>");
			}
			else if (contains) {
				readbutton.append("<form name='readbuttonform' class='rbtag'>");
				readbutton.append("<p>- bereits bewertet -</p>");
				readbutton.append("</form>");
			}
			else {
				readbutton.append("<form name='readbuttonform' class='rbtag'>");
				readbutton.append("<input type='radio' name='panel' value='1' /> 1");
				readbutton.append("<input type='radio' name='panel' value='2' /> 2");
				readbutton.append("<input type='radio' name='panel' value='3' /> 3");
				readbutton.append("<input type='radio' name='panel' value='4' /> 4");
				readbutton.append("</form>");
				readbutton.append("<input type='button' value='OK' onclick=\"getReadButtonValue()\" />");
			}
		}

		return KnowWEUtils.maskHTML(readbutton.toString());
	}
	
}
