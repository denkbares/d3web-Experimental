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

import de.d3web.we.kdom.Parser;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sectionizer;
import de.d3web.we.kdom.SectionizerModule;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class IncludeSectionizerModule implements SectionizerModule {

	@Override
	@SuppressWarnings("unchecked")
	public Section<?> createSection(String text, Type type, Section<?> father, SectionFinderResult result) {
		Parser parser = type.getParser();
		if (result.getParameterMap() != null && parser instanceof Sectionizer) {
			((Sectionizer) parser).addParameterMap(result.getParameterMap());
		}
		@SuppressWarnings("rawtypes")
		Section s = null;
		if (result instanceof IncludeSectionFinderResult) {
			s = parser.parse(text, father);
			s.getSectionStore().storeObject(father.getArticle(), Include.INCLUDE_ADDRESS_KEY,
					((IncludeSectionFinderResult) result).getIncludeAddress());
			KnowWEIncludeManager.getInstance().registerInclude(s);

		}
		return s;
	}

}
