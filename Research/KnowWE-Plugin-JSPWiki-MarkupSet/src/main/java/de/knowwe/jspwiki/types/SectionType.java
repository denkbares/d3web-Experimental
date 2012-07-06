/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.knowwe.jspwiki.types;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

/**
 * 
 * @author Lukas Brehl
 * @created 25.05.2012
 */

public class SectionType extends AbstractType {

	private static SectionType instance = null;

	public static SectionType getInstance() {
		if (instance == null) {
			instance = new SectionType();
		}
		return instance;
	}

	/*
	 * A SectionType can have a SectionHeaderType and a SectionContentType as
	 * children.
	 */
	public SectionType() {
		this.setSectionFinder(new WikiBookSectionFinder('!', true));
		this.addChildType(new SectionHeaderType());
		this.addChildType(SectionContentType.getInstance());
	}

	public class WikiBookSectionFinder implements SectionFinder {
		/*
		 * Looks for Sections separated throw a preceding Char. The order of
		 * Sections can be decreasing and increasing amounts of preceding chars.
		 */

		private final char precedingChar;
		private final boolean decreasing;

		public WikiBookSectionFinder(char precedingChar, boolean decreasing) {
			this.precedingChar = precedingChar;
			this.decreasing = decreasing;
		}

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, Type type) {
			List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
			String[] rows = text.split("(\n|^)");

			int start = 0;
			int level = 0;
			int startrow = 0;
			for (int i = 1; i < rows.length; i++) {
				if (count(rows[i]) != 0) {
					level = count(rows[i]);
					start = text.indexOf(rows[i]);
					startrow = i;
					break;
				}
			}
			if (level == 0) {
				return result;
			}
			for (int i = startrow + 1; i < rows.length; i++) {
				if (level <= count(rows[i])) {
					int end = text.indexOf(rows[i], start);
					SectionFinderResult s = new SectionFinderResult(start, end);
					result.add(s);
					start = text.indexOf(rows[i], start);
					level = count(rows[i]);
				}
			}

			int end = text.length();
			SectionFinderResult s = new SectionFinderResult(start, end);
			if (start != end) {
				result.add(s);
			}
			return result;
		}

		/*
		 * Counts the preceding chars at the beginning of string s if decreasing
		 * equals true. If not it mirrors the amount by 2.
		 */
		public int count(String s) {
			if (s.isEmpty() == true) {
				return 0;
			}
			int level = 0;
			if (s.charAt(0) == precedingChar) {
				level++;
				if (s.charAt(1) == precedingChar) {
					level++;
					if (s.charAt(2) == precedingChar) {
						level++;
					}
				}
			}
			if (level == 0) {
				return 0;
			}
			if (decreasing == false) {
				level = 4 - level;
			}
			return level;
		}
	}
}
