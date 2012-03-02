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

import java.util.List;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.manchester.types.Annotation;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.DataRangeExpression;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 * Helper class for storing regular expressions needed in the AbstractTypes of
 * the Manchester Syntax and some other utility functions. For more information
 * read the comment on each function.
 *
 * @author Stefan Mark
 * @created 15.08.2011
 */
public class ManchesterSyntaxUtil {

	/**
	 * Returns a regular expression that matches the frame keywords.
	 *
	 * @created 25.10.2011
	 * @param keyword
	 * @return
	 */
	public static String getFrameKeywordPattern(ManchesterSyntaxKeywords keyword) {
		return keyword.getKeyword() + "[:]?";
	}

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
		String frame = "(?m)(^$)(\r\n?|\n){1}" +
				"(^$TOKEN$.+$" +
				"(\r\n?|\n)" +
				"((^.*$)(\r\n?|\n))" +
				"*?)" +
				"((^$)(\r\n?|\n)){1}";
		String regex = frame.replace("$TOKEN$", keyword);
		return Pattern.compile(regex);
	}

	/**
	 * Returns a regular expression that matches everything till a certain token
	 * (keywords).
	 *
	 * @created 25.10.2011
	 * @param keyword
	 * @return
	 */
	public static Pattern getTillKeywordPattern(String keyword) {
		String pattern = "(.*?)" // Everything, until
			+ "(?="    //followed by the keywords
			+ keyword
				+ ")";
		return Pattern.compile(pattern);
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
	public static Type getMCE() {

		ManchesterClassExpression mce = new ManchesterClassExpression();
		mce.setSectionFinder(new AllTextFinderTrimmed());
		mce.initRestrictionTypes(false);

		return mce;
	}

	/**
	 *
	 *
	 * @created 02.07.2011
	 * @return
	 */
	public static Type getDataRangeExpression() {

		DataRangeExpression dre = new DataRangeExpression();
		dre.setSectionFinder(new AllTextFinderTrimmed());
		dre.initRestrictionTypes();

		return dre;
	}

	public static boolean hasAnnotations(Section<?> section) {
		return Sections.findSuccessor(section, Annotations.class) != null;
	}

	public static List<Section<Annotation>> getAnnotations(Section<?> section) {
		return Sections.findSuccessorsOfType(section, Annotation.class);
	}

}