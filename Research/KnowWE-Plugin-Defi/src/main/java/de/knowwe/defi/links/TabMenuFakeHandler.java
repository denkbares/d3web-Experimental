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
package de.knowwe.defi.links;

import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class TabMenuFakeHandler extends AbstractTagHandler {

	public TabMenuFakeHandler() {
		super("tabmenu");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		if (parameters.containsKey("pages")) {
			String pages = parameters.get("pages");
			String[] pageNames = pages.split("\\|");

			String baseUrl = Environment.getInstance().getWikiConnector().getBaseUrl();

			StringBuffer buffy = new StringBuffer();
			buffy.append(KnowWEUtils.maskHTML("<div class='tabmenu'>"));
			for (String page : pageNames) {
				String clazz = "";
				if (page.trim().equals(section.getTitle())) {
					clazz = "activetab";
				}
				buffy.append(KnowWEUtils.maskHTML("<a href='" + baseUrl
						+ "Wiki.jsp?page=" + page.trim()
						+ "' class='" + clazz + "'>"));
				buffy.append(KnowWEUtils.maskHTML(page.trim()));
				buffy.append(KnowWEUtils.maskHTML("</a>"));
			}
			buffy.append(KnowWEUtils.maskHTML("</div>"));
			return buffy.toString();

		}
		return "no pages specified: 'pages=page1|page2'";
	}

}
