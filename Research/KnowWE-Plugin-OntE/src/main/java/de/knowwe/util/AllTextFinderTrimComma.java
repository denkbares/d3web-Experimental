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
package de.knowwe.util;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;

/**
 * This SectionFinder removes trailing spaces on an input string (simply
 * performs a trim() string operation and cuts of a trailing comma if found.
 * (Inspired by the {@link AllTextFinderTrimmed}.
 *
 * @author smark
 * @created 14.08.2011
 */
public class AllTextFinderTrimComma implements SectionFinder {

	public AllTextFinderTrimComma() {

	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
		List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();

		String trimmed = text.trim();
		if (trimmed.length() == 0) return result;

		if (trimmed.endsWith("\u002C")) {
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}

		int leadingSpaces = text.indexOf(trimmed);
		int followingSpaces = text.length() - (trimmed.length() + leadingSpaces);

		result.add(new SectionFinderResult(leadingSpaces, text.length()
				- followingSpaces));
		return result;
	}

}
