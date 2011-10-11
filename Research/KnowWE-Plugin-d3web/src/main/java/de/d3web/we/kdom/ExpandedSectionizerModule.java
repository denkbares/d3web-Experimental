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

package de.d3web.we.kdom;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.SectionizerModule;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;


public class ExpandedSectionizerModule implements SectionizerModule {

	@Override
	public Section<?> createSection(String text, Type type, Section<?> father, SectionFinderResult result) {
		if (result instanceof ExpandedSectionFinderResult) {
			return createExpandedSection((ExpandedSectionFinderResult) result, type, father);

		}
		return null;
	}

	private Section<?> createExpandedSection(ExpandedSectionFinderResult result, Type type, Section<?> father) {

		Section<?> s = result.get().getParser().parse(result.getText(), father);
		// Section<?> s = Section.createSection(result.getText(),
		// result.get(), father,
		// result.getStart(), father.getArticle(), null, true);

		for (ExpandedSectionFinderResult childResult : result.getChildren()) {
			createExpandedSection(childResult, result.get(), s);
		}
		return s;
	}

}
