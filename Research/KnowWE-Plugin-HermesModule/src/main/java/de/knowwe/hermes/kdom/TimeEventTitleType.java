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

package de.knowwe.hermes.kdom;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

public class TimeEventTitleType extends AbstractType {

	@Override
	protected void init() {
		sectionFinder = new TimeEventTitleSectionFinder();
	}

	public class TimeEventTitleSectionFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, Type type) {
			if (text.length() < 3) {
				return null;
			}

			int firstLineBreak = text.indexOf("\n");
			String firstLine = text;
			if (firstLineBreak > -1) {
				firstLine = text.substring(0, firstLineBreak);
			}

			int indexEnd = firstLine.lastIndexOf("(");
			if (indexEnd == -1) {
				return null;
			}
			List<SectionFinderResult> list = new ArrayList<SectionFinderResult>();
			list.add(new SectionFinderResult(0, indexEnd));
			return list;

		}
	}
}
