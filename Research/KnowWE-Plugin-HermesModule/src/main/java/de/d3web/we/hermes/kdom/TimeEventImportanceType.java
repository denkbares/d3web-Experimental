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

package de.d3web.we.hermes.kdom;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

public class TimeEventImportanceType extends AbstractType {

	@Override
	protected void init() {
		sectionFinder = new TimeEventTitleSectionFinder();
	}

	// includes "(", ")"
	public class TimeEventTitleSectionFinder extends SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section father, Type type) {
			List<SectionFinderResult> list = new ArrayList<SectionFinderResult>();
			int firstLineBreak = text.indexOf("\n");
			String firstLine = text;
			if (firstLineBreak > -1) {
				firstLine = text.substring(0, firstLineBreak);
			}

			int indexStart = firstLine.indexOf("(");
			int indexEnd = firstLine.indexOf(")") + 1;

			if (indexStart == -1 || indexEnd == -1) {
				return null;
			}

			list.add(new SectionFinderResult(indexStart, indexEnd));

			return list;
		}
	}

}
