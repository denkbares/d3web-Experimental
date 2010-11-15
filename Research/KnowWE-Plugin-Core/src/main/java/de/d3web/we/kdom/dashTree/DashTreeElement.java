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
package de.d3web.we.kdom.dashTree;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Jochen
 * 
 *         A DashTree-element (one line with dashes), which is always the
 *         root-element of the SubTree which comes after it.
 * 
 */
public class DashTreeElement extends DefaultAbstractKnowWEObjectType {

	@Override
	protected void init() {
		this.sectionFinder = new RootFinder();
		this.childrenTypes.add(new DashesPrefix());
		this.childrenTypes.add(new LineEndComment());
		this.childrenTypes.add(new DashTreeElementContent());

	}

	/**
	 * Looks for the first non-empty line in the SubTree to make it
	 * (root-)Element
	 * 
	 * @author Jochen
	 * 
	 */
	class RootFinder extends SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, KnowWEObjectType type) {

			ArrayList<SectionFinderResult> result = new ArrayList<SectionFinderResult>();

			List<SectionFinderResult> lookForSections = LineSectionFinder
					.getInstance().lookForSections(text, father, type);
			if (lookForSections != null && lookForSections.size() > 0) {
				int index = 0;

				// Search for first non-empty line
				while (index < lookForSections.size()) {
					SectionFinderResult sectionFinderResult = lookForSections
							.get(index);
					index++;
					int start = sectionFinderResult.getStart();
					int end = sectionFinderResult.getEnd();
					String finding = text.substring(start, end);
					if (!KnowWEUtils.isEmpty(finding)) {
						result.add(sectionFinderResult);
						break;
					}
				}
			}
			return result;
		}

	}

}
