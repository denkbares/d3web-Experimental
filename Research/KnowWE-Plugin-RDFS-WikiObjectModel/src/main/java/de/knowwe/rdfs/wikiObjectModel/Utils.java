/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.wikiObjectModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jspwiki.types.SectionHeaderType;
import de.knowwe.jspwiki.types.HeaderType;
import de.knowwe.rdfs.wikiObjectModel.types.SectionHeaderObjectDefinition;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 06.07.2012
 */
public class Utils {

	public static String createAnchorURL(Section<HeaderType> sec) {
		Section<SectionHeaderType> header = Sections.findSuccessor(sec, SectionHeaderType.class);
		return createAnchorURLHeader(header);
	}

	public static String createAnchorURLHeaderDefinition(Section<SectionHeaderObjectDefinition> objectDef) {
		String conceptName = objectDef.get().getTermName(objectDef);

		String baseUrl = Environment.getInstance().getWikiConnector().getBaseUrl();

		try {
			return baseUrl
					+ "Wiki.jsp?page="
						+ URLEncoder.encode(conceptName, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String createURL(String pageTitle) {
		String baseUrl = Environment.getInstance().getWikiConnector().getBaseUrl();
		return baseUrl
				+ "Wiki.jsp?page="
				+ pageTitle;
	}

	public static String createAnchorURLHeader(Section<SectionHeaderType> header) {
		Section<SectionHeaderObjectDefinition> objectDef = Sections.findSuccessor(header,
				SectionHeaderObjectDefinition.class);
		return createAnchorURLHeaderDefinition(objectDef);

	}

	public static String createKDOMIDURI(Section<?> s) {
		String baseUrl = Environment.getInstance().getWikiConnector().getBaseUrl();
		return baseUrl + "#KDOM_" + s.getID();
	}

	public static String findContextConceptTermname(Section<?> s) {
		Section<SectionHeaderObjectDefinition> headerObjectSection = getContextSectionIfExisting(s);

		if (headerObjectSection != null) {
			return headerObjectSection.get().getTermName(headerObjectSection);
		}
		return s.getTitle();
	}

	public static Section<SectionHeaderObjectDefinition> getContextSectionIfExisting(Section<?> s) {
		Section<HeaderType> section = Sections.findAncestorOfType(s,
				HeaderType.class);
		if (section != null) {
			Section<SectionHeaderObjectDefinition> headerObjectSection = Sections.findSuccessor(
					section, SectionHeaderObjectDefinition.class);
			return headerObjectSection;

		}

		return null;
	}
}
