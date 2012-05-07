/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.taghandler;

import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

public class ParseFullLocalPageButton extends AbstractHTMLTagHandler {

	public ParseFullLocalPageButton() {
		super("fullParse");
	}

	// TODO: factor me out!
	static final String text = "full-parse";

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {

		// might be placed in 'secondary' pages (i.e., leftmenu, moremenu...)
		String theMainTopic = user.getTitle();

		String baseURL = Environment.getInstance().getWikiConnector().getBaseUrl();

		String link = Strings.maskHTML("<a href=\"" + baseURL + "Wiki.jsp?page="
				+ theMainTopic
				+ "&parse=full\" title=\"full parse action for current article\">");
		link += text;
		link += Strings.maskHTML("</a>");

		return link;
	}

}
