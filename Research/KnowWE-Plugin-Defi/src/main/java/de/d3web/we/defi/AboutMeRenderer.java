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
package de.d3web.we.defi;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;


/**
 * 
 * @author smark
 * @created 25.01.2011
 */
public class AboutMeRenderer<T extends KnowWEObjectType> extends KnowWEDomRenderer<T> {

	@Override
	public void render(KnowWEArticle article, Section<T> sec, KnowWEUserContext user, StringBuilder string) {

		String avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
		String about = DefaultMarkupType.getAnnotation(sec, "about");

		// check if information are given

		String username = user.getUserName();
		String pageName = article.getTitle();
		if (pageName.toLowerCase().equals(username.toLowerCase())) {
			// user is allowed to enter information show edit box
			string.append(KnowWEUtils.maskHTML("<form action=\"KnowWE.jsp\" method=\"post\">"));

			string.append(KnowWEUtils.maskHTML("<p>Choose an avatar:</p>"));
			for (int i = 1; i < 6; i++) {
				String icon = "A0" + i;
				string.append(KnowWEUtils.maskHTML("<img src=\"KnowWEExtension/images/" + icon
						+ ".png\" height=\"80\" width=\"80\" />"));
				string.append(KnowWEUtils.maskHTML("<input type=\"radio\" name=\"defi-avatar\" id=\"defi-avatar\""));
			}

			string.append(KnowWEUtils.maskHTML("<p>Enter some information about yourself:</p>"));
			string.append(KnowWEUtils.maskHTML("<textarea cols=\"80\" rows=\"15\" name=\"defi-about\"></textarea>"));
			string.append(KnowWEUtils.maskHTML("<input type=\"submit\" value=\"Save\"/>"));
			string.append(KnowWEUtils.maskHTML("<input type=\"hidden\" name=\"action\" value=\"AboutMeSaveAction\" />"));
			string.append(KnowWEUtils.maskHTML("</form>"));
			string.append(KnowWEUtils.maskHTML(""));
			string.append(KnowWEUtils.maskHTML(""));
			string.append(KnowWEUtils.maskHTML(""));
		}
		else {
			string.append("access denied");
		}

		// show information or nothing
	}
}
