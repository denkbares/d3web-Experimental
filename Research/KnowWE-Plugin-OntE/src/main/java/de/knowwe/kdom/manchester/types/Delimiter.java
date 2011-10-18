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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

/**
 *
 * @author Stefan Mark
 */
public class Delimiter extends AbstractType{

	public static final char COMMA = '\u002c';

	public static final char LEFT_PARENTHESIS = '\u0028';
	public static final char RIGHT_PARENTHESIS = '\u0029';

	public static final char LEFT_SQUARE_BRACKET = '\u005B';
	public static final char RIGHT_SQUARE_BRACKET = '\u005D';

	public static final char LEFT_CURLY_BRACKET = '\u007B';
	public static final char RIGHT_CURLY_BRACKET = '\u007D';

	public Delimiter() {
		this.setSectionFinder(new RegexSectionFinder(String.valueOf(COMMA)));
	}

	public boolean isComma(Section<? extends Type> section) {
		return isSectionType(section, String.valueOf(COMMA));
	}

	public boolean isParenthesis(Section<? extends Type> section) {
		return isSectionType(section, String.valueOf(LEFT_PARENTHESIS));
	}

	public boolean isCurly(Section<? extends Type> section) {
		return isSectionType(section, String.valueOf(LEFT_CURLY_BRACKET));
	}

	public boolean isSquare(Section<? extends Type> section) {
		return isSectionType(section, String.valueOf(LEFT_SQUARE_BRACKET));
	}

	public String getWrappedWithDelimiter(Section<? extends Type> section, String toWrap) {
		if (isComma(section)) {
			return toWrap + COMMA;
		}
		else if (isParenthesis(section)) {
			return LEFT_PARENTHESIS + toWrap + RIGHT_PARENTHESIS;
		}
		return "";
	}

	private boolean isSectionType(Section<? extends Type> section, String delimiter) {
		Section<Delimiter> d = Sections.findSuccessor(section, Delimiter.class);
		if (d != null) {
			if (d.getOriginalText().trim().equals(delimiter)) {
				return true;
			}
		}
		return false;
	}
}
