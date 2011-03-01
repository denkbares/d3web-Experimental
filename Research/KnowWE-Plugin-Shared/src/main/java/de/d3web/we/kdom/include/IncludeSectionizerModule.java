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

package de.d3web.we.kdom.include;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.SectionizerModule;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.utils.KnowWEUtils;

public class IncludeSectionizerModule implements SectionizerModule {

	@Override
	@SuppressWarnings("unchecked")
	public Section<?> createSection(String text, Type type, Section<?> father, KnowWEArticle article, SectionFinderResult result) {
		Section s = null;
		if (result instanceof IncludeSectionFinderResult) {
			s = type.getParser().parse(text, type, result.getId(), father, article);
			// s = Section.createSection(
			// thisSection.getOriginalText().substring(
			// result.getStart(),
			// result.getEnd()),
			// ob,
			// father,
			// thisSection.getOffSetFromFatherText()
			// + result.getStart(),
			// article,
			// result.getId(),
			// false);

			KnowWEUtils.storeObject(s.getWeb(), s.getTitle(), s.getID(),
					Include.INCLUDE_ADDRESS_KEY,
					((IncludeSectionFinderResult) result).getIncludeAddress());
			KnowWEIncludeManager.getInstance().registerInclude(s);

		}
		return s;
	}

}
