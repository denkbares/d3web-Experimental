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

		String username = user.getUserName();
		String pageName = article.getTitle();

		boolean isOwner = pageName.toLowerCase().equals(username.toLowerCase())
				&& user.userIsAsserted();

		StringBuilder noneHTML = new StringBuilder();

		noneHTML.append("<h1>Meine Profileinstellungen im WIKI</h1>");

		if (isOwner) {
			noneHTML.append("<strong>Hinweis</strong>: Alle Daten, die Sie hier angeben, sind für andere Benutzer sichtbar. "
					+
					"Sie können selbst entscheiden, was Sie von sich preisgeben möchten. Selbstverständlich "
					+
					"können Sie hier eingegebene Daten auch jederzeit ändern oder löschen.");
			noneHTML.append("<form action=\"KnowWE.jsp\" method=\"post\">");
		}

		noneHTML.append("<p>Alter: ");
		noneHTML.append(getInputDependingOnUserState(sec, AboutMe.HTML_AGE, isOwner));
		noneHTML.append("</p>");

		noneHTML.append("<p>Wohnort: ");
		noneHTML.append(getInputDependingOnUserState(sec, AboutMe.HTML_CITY, isOwner));
		noneHTML.append("</p>");

		if (isOwner) {
			noneHTML.append("<p>Avatar: (Mit diesem Bild können Sie Ihrem Profil ein „Gesicht“ verleihen. Hier können "
					+ "Sie entweder eines der vorgegebenen Bilder auswählen, oder ein eigenes Bild von Ihrem PC hochladen. "
					+ "(Hinweis auf Vermeidung anstößiger oder rechtswidriger Inhalte)</p>");
		}

		this.createAvatarHTML(sec, noneHTML, AboutMe.HTML_AVATAR, isOwner);
		// this.createAvatarHTML(string, avatar, "F");
		// string.append(KnowWEUtils.maskHTML("</div>"));

		noneHTML.append("<h2>Über mich und meinen Defi:</h2>");
		noneHTML.append("<dl>");
		noneHTML.append("<dt>Defi-Modell</dt>");
		noneHTML.append("<dd>Hersteller: "
				+ getInputDependingOnUserState(sec, AboutMe.HTML_PRODUCER, isOwner) +
				"</dd>");
		noneHTML.append("<dd>Type: "
				+ getInputDependingOnUserState(sec, AboutMe.HTML_TYPE, isOwner)
				+ "</dd>");
		noneHTML.append("</dl>");

		noneHTML.append("<p>Warum ich einen Defi habe: "
				+ getInputDependingOnUserState(sec, AboutMe.HTML_REASON, isOwner) + "</p>");
		noneHTML.append("<p>Meine Hobbies: "
				+ getTextareaDependingOnUserState(sec, AboutMe.HTML_HOBBIES, isOwner)
				+ "</p>");
		noneHTML.append("<p>Was ich sonst noch über mich sagen möchte: "
				+ getTextareaDependingOnUserState(sec, AboutMe.HTML_ABOUT, isOwner) + "</p>");

		// if (about == null) {
		// string.append(KnowWEUtils.maskHTML("<p>Schreiben Sie ein paar Worte über sich:</p>"));
		// string.append(KnowWEUtils.maskHTML("<p><textarea cols=\"60\" rows=\"15\" name=\""
		// + AboutMe.HTMLID_ABOUT + "\"></textarea></p>"));
		// }
		if (isOwner) {
			noneHTML.append("<p><input type=\"submit\" value=\"Speichern\"/></p>");
			noneHTML.append("<input type=\"hidden\" name=\"action\" value=\"AboutMeSaveAction\" />");
			noneHTML.append("<input type=\"hidden\" name=\"KWiki_Topic\" value=\""
					+ article.getTitle() + "\" />");
			noneHTML.append("</form>");
		}

		string.append(KnowWEUtils.maskHTML(noneHTML.toString()));
	}

	/**
	 * Creates an HTML Input element or a normal string depending on the user
	 * that called the page.
	 * 
	 * @created 22.09.2011
	 * @param section
	 * @param value
	 * @param isOwner
	 * @return
	 */
	private String getInputDependingOnUserState(Section<T> section, String value, boolean isOwner) {

		String input = DefaultMarkupType.getAnnotation(section, value);

		if (isOwner) {
			if (input != null) {
				return "<br /><input type=\"text\" name=\"" + value + "\"  value=\"" + input
						+ "\" size=\"43\" />";
			}
			return "<br /><input type=\"text\" name=\"" + value + "\"  value=\"\" size=\"43\" />";
		}
		else {
			if (input != null) {
				return input + "<br />";
			}
			return "<br />";
		}
	}

	/**
	 * Creates an HTML Input element or a normal string depending on the user
	 * that called the page.
	 * 
	 * @created 22.09.2011
	 * @param section
	 * @param value
	 * @param isOwner
	 * @return
	 */
	private String getTextareaDependingOnUserState(Section<T> section, String value, boolean isOwner) {

		String input = DefaultMarkupType.getAnnotation(section, value);

		if (isOwner) {
			if (input != null) {
				return "<br /><textarea type=\"text\" name=\"" + value + "\" cols=\"40\">" + input
						+ "</textarea>";
			}
			return "<br /><textarea type=\"text\" name=\"" + value + "\" cols=\"40\"></textarea>";
		}
		else {
			if (input != null) {
				return input + "<br />";
			}
			return "<br />";
		}
	}

	/**
	 *
	 */
	private void createAvatarHTML(Section<T> section, StringBuilder string, String key, boolean isOwner) {

		String avatar = DefaultMarkupType.getAnnotation(section, key);

		if (!isOwner && avatar != null) {
			string.append("<img src=\"KnowWEExtension/images/" + avatar
					+ ".png\" height=\"80\" width=\"80\" />\n");
		}
		else {
			for (int i = 1; i < 3; i++) {
				String icon = "A0" + i;
				String checked = "";

				if (avatar != null && avatar.equals(icon)) {
					checked = " checked='checked'";
				}

				string.append("<img src=\"KnowWEExtension/images/" + icon
							+ ".png\" height=\"80\" width=\"80\" />\n");
				string.append("<input type=\"radio\" name=\"" + AboutMe.HTML_AVATAR + "\" id=\""
							+ AboutMe.HTML_AVATAR + "\" value=\""
							+ icon + "\" " + checked + " />\n");
			}
			string.append("<br />");
		}
	}
}