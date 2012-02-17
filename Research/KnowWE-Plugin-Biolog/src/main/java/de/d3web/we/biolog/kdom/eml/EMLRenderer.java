/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.biolog.kdom.eml;

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.xml.AbstractXMLType;
import de.knowwe.kdom.xml.XMLContent;

/**
 * Renders the EML-data to a table-format.
 * 
 * @author smark
 */
public class EMLRenderer implements KnowWERenderer {

	@Override
	public void render(Section sec, UserContext user,
			StringBuilder string) {
		if (sec.get() instanceof EMLXMLType) {
			Section<AbstractXMLType> s = sec;

			Section<? extends AbstractXMLType> methodSection = AbstractXMLType.findSubSectionOfTag(
					"methods", s);

			if (methodSection != null) {
				this.renderCommonData(s, string, user);

				Object[][] tblCreator = {
						{
								"Nachname", "surName", "individualName" },
						{
								"Vorname", "givenName", "individualName" },
						{
								"Anrede", "salutation", "individualName" },
						{
								"E-Mail", "electronicMailAddress", "creator" },
						{
								"Organisation", "organizationName", "creator" },
						{
								"Organisation Strasse", "deliveryPoint", "creator" },
						{
								"Organisation PLZ", "postalCode", "creator" },
						{
								"Organisation Ort", "city", "creator" } };

				this.renderPersonDetails(s, string, user, tblCreator, "creator", 2);

				Object[][] tblContact = {
						{
								"Nachname", "surName", "individualName" },
						{
								"Vorname", "givenName", "individualName" },
						{
								"Anrede", "salutation", "individualName" },
						{
								"E-Mail", "electronicMailAddress", "contact" },
						{
								"Organisation", "organizationName", "contact" },
						{
								"Organisation Strasse", "deliveryPoint", "contact" },
						{
								"Organisation PLZ", "postalCode", "contact" },
						{
								"Organisation Ort", "city", "contact" } };

				this.renderPersonDetails(s, string, user, tblContact, "contact", 3);

				this.renderMethods(s, string, user);
			}
			else {
				string.append("no method section found");
			}
		}
	}

	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderCommonData(Section<? extends AbstractXMLType> section,
			StringBuilder string,
			UserContext user) {

		Object[][] tblColumns = {
				{
						"Zweck", "purpose", "dataset" },
				{
						"Projekt", "title", "project" },
				{
						"Titel", "title", "dataset" },
				{
						"Abstract", "abstract", "dataset" },
				{
						"Keywords", "keyword", "keywordSet" } };

		StringBuilder eml = new StringBuilder();
		eml.append("<h2>1. Allgemeine Angaben</h2>");
		eml.append("<table class=\"eml\"><tbody>");

		for (Object[] element : tblColumns) {
			eml.append("<tr><td class=\"eml-highlight\">"
					+ element[0] + "</td><td class=\"eml-normal\">");

			Collection<Section<? extends AbstractXMLType>> tags = new ArrayList<Section<? extends AbstractXMLType>>();
			AbstractXMLType.findSubSectionsOfTag(element[1].toString(), section, tags);

			for (Section<? extends AbstractXMLType> tag : tags) {
				Section<? extends AbstractXMLType> father = AbstractXMLType.getXMLFatherElement(tag);
				String fatherTag = AbstractXMLType.getTagName(father);

				if (fatherTag.equals(element[2])) {
					Section<? extends XMLContent> xml = Sections.findChildOfType(tag,
							XMLContent.class);
					if (xml != null) {
						eml.append(xml.getText());
					}
					eml.append(", ");
				}
			}
			if (eml.toString().endsWith(", ")) {
				eml.delete(eml.lastIndexOf(", "), eml.length());
			}
			eml.append("</td></tr>");
		}

		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML(eml.toString()));
	}

	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderPersonDetails(Section<? extends AbstractXMLType> section,
			StringBuilder string,
			UserContext user,
			Object[][] tblColumns,
			String tblName,
			int num) {

		StringBuilder eml = new StringBuilder();
		eml.append("<h2> " + num + ". " + (tblName.equals("creator") ? "Author" : "Kontakt")
				+ "</h2>");
		eml.append("<table class=\"eml\"><tbody>");

		Collection<Section<? extends AbstractXMLType>> tags = new ArrayList<Section<? extends AbstractXMLType>>();
		AbstractXMLType.findSubSectionsOfTag(tblName, section, tags);

		for (Object[] element : tblColumns) {
			eml.append("<tr>");
			eml.append("<td class=\"eml-highlight\" style=\"width:20%;\">" + element[0] + "</td>");

			for (Section<? extends AbstractXMLType> tag : tags) {

				eml.append("<td class=\"eml-normal\">");
				Section<? extends AbstractXMLType> c = AbstractXMLType.findSubSectionOfTag(
						element[1].toString(), tag);
				if (c != null) {
					Section<? extends AbstractXMLType> father = AbstractXMLType.getXMLFatherElement(c);
					String fatherTag = AbstractXMLType.getTagName(father);

					if (fatherTag.equals(element[2])) {
						Section<? extends XMLContent> xml = Sections.findChildOfType(c,
								XMLContent.class);
						if (xml != null) {
							eml.append(xml.getText());
						}
						eml.append(", ");
					}
					if (eml.toString().endsWith(", ")) {
						eml.delete(eml.lastIndexOf(", "), eml.length());
					}
				}
				eml.append("</td>");
			}
			eml.append("</tr>");
		}

		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML(eml.toString()));
	}

	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderMethods(Section<? extends AbstractXMLType> section,
			StringBuilder string,
			UserContext user) {

		StringBuilder eml = new StringBuilder();
		eml.append("<h2> 4. Methoden </h2>");
		eml.append("<table class=\"eml\"><tbody>");

		eml.append("<tr><td colspan=\"5\" class=\"eml-highlight\">4.1 Angewendete Methoden</td></tr>");

		Collection<Section<? extends AbstractXMLType>> tags = new ArrayList<Section<? extends AbstractXMLType>>();
		AbstractXMLType.findSubSectionsOfTag("description", section, tags);

		for (Section<? extends AbstractXMLType> tag : tags) {
			Section<? extends AbstractXMLType> father = AbstractXMLType.getXMLFatherElement(tag);
			String fatherTag = AbstractXMLType.getTagName(father);

			if (fatherTag.equals("methodStep")) {
				addTD(tag, eml, new String[] { "description" }, null);
			}
		}

		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.2 Sampling</td></tr>");

		tags = new ArrayList<Section<? extends AbstractXMLType>>();
		AbstractXMLType.findSubSectionsOfTag("samplingDescription", section, tags);

		for (Section<? extends AbstractXMLType> tag : tags) {
			Section<? extends AbstractXMLType> father = AbstractXMLType.getXMLFatherElement(tag);
			String fatherTag = AbstractXMLType.getTagName(father);

			if (fatherTag.equals("sampling")) {
				addTD(tag, eml, new String[] { "samplingDescription" }, null);
			}
		}

		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3 Umfang der Untersuchung</td></tr>");

		String[] p = {
				"studyExtent", "description" };
		addTD(section, eml, p, null);

		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3.1 zeitlicher Umfang der Untersuchung</td></tr>");

		p = new String[] {
				"temporalCoverage", "beginDate", "calendarDate" };
		addTD(section, eml, p, "Beginn");

		p = new String[] {
				"temporalCoverage", "endDate", "calendarDate" };
		addTD(section, eml, p, "Ende");

		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3.2 räumlicher Umfang der Untersuchung</td></tr>");

		p = new String[] {
				"coverage", "geographicCoverage", "description" };
		addTD(section, eml, p, "Koordinatensystem");

		p = new String[] {
				"coverage", "geographicCoverage", "BoundingCoordinates", "northBoundingCoordinate" };
		addTD(section, eml, p, "Nord (max.Y)");

		p = new String[] {
				"coverage", "geographicCoverage", "BoundingCoordinates", "southBoundingCoordinate" };
		addTD(section, eml, p, "Süd (min Y)");

		p = new String[] {
				"coverage", "geographicCoverage", "BoundingCoordinates", "eastBoundingCoordinate" };
		addTD(section, eml, p, "Ost (max X)");

		p = new String[] {
				"coverage", "geographicCoverage", "BoundingCoordinates", "westBoundingCoordinate" };
		addTD(section, eml, p, "West (min X)");

		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML(eml.toString()));
	}

	/**
	 * 
	 * @param string
	 * @param path
	 * @param tdName
	 */
	private void addTD(Section<? extends AbstractXMLType> section,
			StringBuilder string, String[] path, String tdName) {

		String s = getTagContent(section, path);
		if (s != null && s != "") {
			if (tdName != null) {
				string.append("<tr><td class=\"eml-highlight\">" + tdName
						+ "</td><td class=\"eml-normal\">" + s + "</td></tr>");
			}
			else {
				string.append("<tr><td class=\"eml-normal\" colspan=\"2\">" + s + "</td></tr>");
			}
		}
	}

	/**
	 * 
	 * @param s
	 * @param father
	 * @param tag
	 * @return
	 */
	private String getTagContent(Section<? extends AbstractXMLType> s, String[] path) {
		String txt = null;
		Section<? extends AbstractXMLType> tmpTag = null;

		for (String string : path) {
			if (tmpTag == null) {
				tmpTag = s;
			}
			tmpTag = AbstractXMLType.findSubSectionOfTag(string, tmpTag);
			if (tmpTag == null) return null;
		}

		if (tmpTag != null) {
			Section<? extends XMLContent> xml = Sections.findChildOfType(tmpTag, XMLContent.class);
			if (xml != null) {
				txt = xml.getText();
			}
		}
		if (txt != null) {
			return txt;
		}
		return "";
	}

}
