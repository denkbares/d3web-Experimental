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
package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;

public class NonTerminalList extends AbstractType {

	public static final char COMMA = '\u002c';
	public static final char QUOTE = '\u0022';

	@Override
	protected void init() {
		this.setSectionFinder(new NoneTerminalListFinder());
	}

	/**
	 * None terminal list separator
	 *
	 * @author Stefan Mark
	 * @created 29.09.2011
	 */
	class NoneTerminalListFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<? extends Type> father, Type type) {

			String trimmed = text.trim();

			if (text.contains(Character.toString(COMMA))) {
				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

				// Comma should be marked as PlainText
				if (trimmed.length() == 1 && trimmed.equals(Character.valueOf(COMMA))) {
					return null;
				}

				char[] chars = text.toCharArray();

				int currentEnd = 0;
				int currentStart = text.indexOf(trimmed);
				boolean quoted = false;

				for (int i = 0; i < chars.length; i++) {
					if (Character.valueOf(COMMA).equals(chars[i]) && !quoted) {
						// found comma, not quoted -> create result
						currentEnd = i;
						results.add(new SectionFinderResult(currentStart, currentEnd));
						currentStart = i + 1;
					}
					else if (i + 1 == text.length()) {
						// take everything till the end of the input as the
						// token after the last comma
						currentEnd = text.length();
						results.add(new SectionFinderResult(currentStart, currentEnd));
					}
					else if (Character.valueOf(QUOTE).equals(chars[i])) {
						// found quote, not comma in quotes should be handled
						quoted = !quoted;
					}
				}
				return results; // return found results
			}
			return null;
		}
	}
}
