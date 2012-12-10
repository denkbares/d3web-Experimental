/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.wisskont.util;

import java.util.List;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
public class MarkupUtils {

	/**
	 * Finds the concept definition iff there is exactly _one_ on this page.
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Section<IncrementalTermDefinition> getConceptDefinition(Section<?> section) {
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = getConecptDefinitions(section);
		if (conceptDefinitionMarkupSections.size() == 1) {
			Section<ConceptMarkup> defSection = conceptDefinitionMarkupSections.get(0);
			Section<IncrementalTermDefinition> termSec = Sections.findSuccessor(
					defSection,
					IncrementalTermDefinition.class);
			return termSec;
		}
		return null;
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param section
	 * @return
	 */
	public static List<Section<ConceptMarkup>> getConecptDefinitions(Section<?> section) {
		Section<RootType> rootSection = Sections.findAncestorOfType(section, RootType.class);
		List<Section<ConceptMarkup>> conceptDefinitionMarkupSections = Sections.findSuccessorsOfType(
				rootSection, ConceptMarkup.class);
		return conceptDefinitionMarkupSections;
	}

}
