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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * The AboutMeRenderer renders the content of the about me page. It renders a
 * list of different avatars and a HTML textarea. In this box the user can write
 * something about himself. From the list of avatars the user can choose one
 * that fits him/her.
 * 
 * @author Stefan Mark
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
			string.append("<img src=\"KnowWEExtension/images/avatars/" + avatar
					+ "\" height=\"80\" width=\"80\" />\n");
		}
		else {

			String[] avatars = {
					"004.jpg",
					"006.jpg",
					"1000px-Comic_image_missing.svg.jpg",
					"2006-02-13_Drop-impact.jpg",
					"425px-Hen_and_chicks_cartoon_04.svg.jpg",
					"500px-Cosmic-eidex-herz.svg.jpg",
					"Absolute_mer_de_glace_01.jpg",
					"Adler.jpg",
					"Anatomy_Heart_English_Tiesworks.jpg",
					"Aster_Tataricus.jpg",
					"Bali_june_aft.jpg",
					"Blackbird-sunset-03.jpg",
					"Boelge_stor.jpg",
					"Booby_chick.jpg",
					"Cartoon-cats-black-cat.jpg",
					"Cartoon-Fish.jpg",
					"Cello_study.jpg",
					"Chess_queen_0994.jpg",
					"Chick_(PSF).jpg",
					"Citrus_fruits.jpg",
					"Colouring_pencils.jpg",
					"Dof_bloom_mental_ray.jpg",
					"DSC_0042.jpg",
					"DSC_0440_1.jpg",
					"DSC_0483.jpg",
					"DSC_0553.jpg",
					"DSC_0656.jpg",
					"DSC_1057-1.jpg",
					"DSC_1065.jpg",
					"DSC_1240.jpg",
					"DSC_1315_1.jpg",
					"DSC_1428.jpg",
					"DSC_1434.jpg",
					"DSC_1460.jpg",
					"DSC_1617.jpg",
					"DSC_1620.jpg",
					"DSC_1918.jpg",
					"DSC_1932.jpg",
					"DSC_2196.jpg",
					"DSC_3440.jpg",
					"DSC_4213.jpg",
					"DSC_4214.jpg",
					"DSC_4257.jpg",
					"DSC_4823.jpg",
					"DSC_4910.jpg",
					"DSC_4911.jpg",
					"DSC_4913.jpg",
					"DSC_4914.jpg",
					"DSC_4917.jpg",
					"DSC_4918.jpg",
					"Fruit_Stall_in_Barcelona_Market.jpg",
					"Funny_Cide.jpg",
					"Funny_dog.jpg",
					"German_garden_gnome.jpg",
					"Heart_frontally_PDA.jpg",
					"Howmet_TX_at_Silverstone_Classic_Endurance_Car_Racing_in_September_2009.jpg",
					"Hund4.jpg",
					"ICD.jpg",
					"Lautsprecher_01_KMJ.jpg",
					"Lions_on_rock.jpg",
					"Maserati_3500_GT_Touring_Coupe_1963.jpg",
					"Postauto_oldtimer_Grimsel.jpg",
					"Red_Apple.jpg",
					"safd.jpg",
					"Sealion_-Whipsnade_Zoo_-14Apr2009_(2).jpg",
					"Strombus_sinuatus_2010_G1.jpg",
					"Sunrise,_Manaslu.jpg",
					"Train_face_at_Danzig_train_station.jpg",
					"Two_cups_of_tea_with_spoon.jpg",
					"United_Autosports_Cars.jpg" };

			for (int i = 0; i < avatars.length; i++) {
				String icon = avatars[i];
				String checked = "";

				if (avatar != null && avatar.equals(icon)) {
					checked = " checked='checked'";
				}

				string.append("<img src=\"KnowWEExtension/images/avatars/" + icon
							+ "\" height=\"80\" width=\"80\" />\n");
				string.append("<input type=\"radio\" name=\"" + AboutMe.HTML_AVATAR + "\" id=\""
							+ AboutMe.HTML_AVATAR + "\" value=\""
							+ icon + "\" " + checked + " />\n");
			}
			string.append("<br />");
		}
	}
}