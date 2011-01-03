/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

/**
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 12.08.2010
 */
public class TimeStampType extends DefaultAbstractKnowWEObjectType {

	private static String HOUR = "\\d+h\\s*";
	private static String MINUTE = "\\d+m\\s*";
	private static String SECOND = "\\d+s\\s*";
	private static String MILLIS = "\\d+ms\\s*";
	private static String HOUR_PERMUTATION = HOUR + "(" + MINUTE + ")?(" + SECOND + ")?(" + MILLIS
			+ ")?";
	private static String MINUTE_PERMUTATION = "(" + HOUR + ")?" + MINUTE + "(" + SECOND + ")?("
			+ MILLIS + ")?";
	private static String SECOND_PERMUTATION = "(" + HOUR + ")?(" + MINUTE + ")?" + SECOND + "("
			+ MILLIS + ")?";
	private static String MILLIS_PERMUTATION = "(" + HOUR + ")?(" + MINUTE + ")?(" + SECOND
			+ ")?" + MILLIS;

	private static String pattern = "(" + HOUR_PERMUTATION + "|" + MINUTE_PERMUTATION + "|"
			+ SECOND_PERMUTATION + "|" + MILLIS_PERMUTATION + ")";
	private static Pattern timeStampPattern = Pattern.compile(pattern);

	public TimeStampType() {
		/*
		 * Comment (Sebastian Furth): added SectionFinder because otherwise
		 * every cell in the table is a TimeStamp. Burn me if i was wrong ;)
		 */
		sectionFinder = new RegexSectionFinder(pattern);
	}

	public static boolean isValid(String sectionText) {
		return timeStampPattern.matcher(sectionText).matches();
	}

	public static long getTimeInMillis(String sectionText) {
		if (!isValid(sectionText)) {
			return -1;
		}

		sectionText = sectionText.trim();

		long sum = 0;
		while (true) {
			String current = "" + sectionText.charAt(0);
			String number = "";
			int i = 0;
			while (current.matches("[0-9]")) {
				number += current;
				i++;
				current = "" + sectionText.charAt(i);
			}

			current = "" + sectionText.charAt(i);
			if (current.equals("h")) {
				sum += Long.valueOf(number) * 60 * 60 * 1000;
			}
			else if (current.equals("m")) {
				sum += Long.valueOf(number) * 60 * 1000;
			}
			else if (current.equals("s")) {
				sum += Long.valueOf(number) * 1000;
			}
			else {
				sum += Long.valueOf(number);
			}

			if (i + 1 < sectionText.length()) {
				sectionText = sectionText.substring(i + 1);
			}
			else {
				break;
			}
		}

		return sum;
	}

}
