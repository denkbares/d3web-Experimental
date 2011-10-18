/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.onte.ci4ke;

import java.util.Collection;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * Simple helper functions for the ci4ke integration.
 *
 * @author Stefan Mark
 * @created 18.10.2011
 */
public class OnteCi4keUtil {

	public static String renderHyperlink(String conceptName) {
		Collection<Section<? extends TermDefinition>> terminology = KnowWEUtils.getTerminologyHandler(
				KnowWEEnvironment.DEFAULT_WEB).getAllGlobalTermDefs();

		StringBuilder link = new StringBuilder();
		for (Section<? extends TermDefinition> section : terminology) {
			if (section.getOriginalText().equals(conceptName)) {
				link.append("<span style=\"font-size:9px;\">(Asserted in local article: ");
				link.append("<a href=\"Wiki.jsp?page=" + section.getArticle().getTitle()
						+ "\" title=\"Goto definition article\">");
				link.append(section.getArticle().getTitle());
				link.append("</a>)</span>");
			}
		}
		return link.toString();
	}
}
