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

/**
 *
 *
 * @author Stefan Mark
 * @created 27.10.2011
 */
public class NonTerminalList extends AbstractType {

	public static final char COMMA = '\u002c';
	public static final char QUOTE = '\u0022';

	public static final char LEFT_PARENTHESIS = '\u0028';
	public static final char RIGHT_PARENTHESIS = '\u0029';

	public static final char LEFT_CURLY_BRACKET = '\u007B';
	public static final char RIGHT_CURLY_BRACKET = '\u007D';

	public static final char LEFT_SQUARE_BRACKET = '\u005B';
	public static final char RIGHT_SQUARE_BRACKET = '\u005D';

	public NonTerminalList() {
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
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			String trimmed = text.trim();

			if (text.contains(Character.toString(COMMA))) {
				List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();

				// Comma should be marked as PlainText
				if (trimmed.length() == 1 && trimmed.equals(Character.valueOf(COMMA))) {
					return null;
				}

				char[] chars = text.toCharArray();

				int currentStart = text.indexOf(trimmed);
				boolean quoted = false;
				int braced = 0;

				for (int i = 0; i < chars.length; i++) {
					char current = chars[i];

					switch (current) {
					case LEFT_PARENTHESIS:
					case LEFT_CURLY_BRACKET:
					case LEFT_SQUARE_BRACKET:
						braced++;
						break;
					case RIGHT_PARENTHESIS:
					case RIGHT_CURLY_BRACKET:
					case RIGHT_SQUARE_BRACKET:
						braced--;
						break;
					case QUOTE:
						quoted = !quoted;
						break;
					}

					if (Character.valueOf(COMMA).equals(current) && !quoted && braced == 0) {
						// found comma, not quoted -> create result
						results.add(new SectionFinderResult(currentStart, i));
						currentStart = i + 1;
					}
					else if (i + 1 == text.length()) {
						results.add(new SectionFinderResult(currentStart,
								text.length()));
					}
				}

				if (results.isEmpty()) {
					return null;
				}
				return results; // return found results
			}
			return null;
		}
	}
}
