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
package de.knowwe.defi.aboutMe;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The AboutMeRenderer renders the content of the about me page. It renders a
 * list of different avatars and a HTML textarea. In this box the user can write
 * something about himself. From the list of avatars the user can choose one
 * that fits him/her.
 * 
 * @author smark
 * @created 25.01.2011
 */
public class AboutMeRenderer<T extends AbstractType> extends KnowWEDomRenderer<T> {

	@Override
	public void render(KnowWEArticle article, Section<T> sec, UserContext user, StringBuilder string) {

		String avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
		String about = DefaultMarkupType.getAnnotation(sec, "about");

		String username = user.getUserName();
		String pageName = article.getTitle();

		if (pageName.toLowerCase().equals(username.toLowerCase()) && user.userIsAsserted()) {
			string.append(KnowWEUtils.maskHTML("<form action=\"KnowWE.jsp\" method=\"post\">"));
			string.append(KnowWEUtils.maskHTML("<p>Bitte wählen Sie einen Bild aus:</p><div>"));

			this.createAvatarHTML(string, avatar, "");
			this.createAvatarHTML(string, avatar, "F");

			string.append(KnowWEUtils.maskHTML("</div>"));

			if (about == null) {
				string.append(KnowWEUtils.maskHTML("<p>Schreiben Sie ein paar Worte über sich:</p>"));
				string.append(KnowWEUtils.maskHTML("<p><textarea cols=\"60\" rows=\"15\" name=\""
						+ AboutMe.HTMLID_ABOUT + "\"></textarea></p>"));
			}
			string.append(KnowWEUtils.maskHTML("<p><input type=\"submit\" value=\"Speichern\"/></p>"));
			string.append(KnowWEUtils.maskHTML("<input type=\"hidden\" name=\"action\" value=\"AboutMeSaveAction\" />"));
			string.append(KnowWEUtils.maskHTML("<input type=\"hidden\" name=\"KWiki_Topic\" value=\""
					+ article.getTitle() + "\" />"));
			string.append(KnowWEUtils.maskHTML("</form>"));
		}
		else {
			if (avatar != null) {
				string.append(KnowWEUtils.maskHTML("<img src=\"KnowWEExtension/images/"
						+ avatar + ".png\" height=\"80\" width=\"80\" />"));
			}
		}
	}

	/**
	 *
	 */
	private void createAvatarHTML(StringBuilder string, String avatar, String prefix) {
		String letters = "ACE";
		for (int j = 0; j < letters.length(); j++) {
			for (int i = 1; i < 6; i++) {
				String icon = (prefix != "")
						? (prefix + letters.charAt(j) + "0" + i)
						: letters.charAt(j) + "0" + i;
				String checked = "";

				if (avatar != null && avatar.equals(icon)) {
					checked = " checked='checked'";
				}

				string.append(KnowWEUtils.maskHTML("<img src=\"KnowWEExtension/images/" + icon
						+ ".png\" height=\"80\" width=\"80\" />\n"));
				string.append(KnowWEUtils.maskHTML("<input type=\"radio\" name=\"defi-avatar\" id=\""
						+ AboutMe.HTMLID_AVATAR + "\" value=\""
						+ icon + "\" " + checked + " />\n"));
			}
			string.append(KnowWEUtils.maskHTML("<br />"));
		}
	}
}
