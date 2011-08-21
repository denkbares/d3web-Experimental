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
package de.knowwe.kdom.manchester;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

/**
 * Helper class for storing regular expressions needed in the AbstractTypes of
 * the Manchester Syntax and some other utility functions. For more information
 * read the comment on each function.
 *
 * @author smark
 * @created 15.08.2011
 */
public class ManchesterSyntaxUtil {

	/**
	 * Returns a regular expression string that maps a frame of the Manchester
	 * Syntax. The word boundary expression near the frame keyword is necessary
	 * due some frames having the same name in their description.
	 *
	 * @created 01.07.2011
	 * @param keyword
	 * @return
	 */
	public static Pattern getFramePattern(String keyword) {
		String frame = "(?m)" +
				"(\\b$TOKEN$.+$" +
				"(\r\n?|\n)" +
				"((^.*$)(\r\n?|\n))" +
				"*?)" +
				"((^$)(\r\n?|\n)){2}";
		String regex = frame.replace("$TOKEN$", keyword);
		return Pattern.compile(regex);
	}

	/**
	 *
	 *
	 * @created 01.07.2011
	 * @param keyword
	 * @return
	 */
	public static Pattern getDescriptionPattern(String clazzKey, String descriptionKey) {
		String regex = "(" + descriptionKey + "(.+?))" + clazzKey;
		return Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
	}

	/**
	 *
	 *
	 * @created 02.07.2011
	 * @return
	 */
	public static List<Type> getMCE() {
		List<Type> t = new ArrayList<Type>();

		ManchesterClassExpression mce = new ManchesterClassExpression();
		mce.setSectionFinder(new AllTextFinderTrimmed());
		mce.initRestrictionTypes();

		t.add(mce);
		return t;
	}
}