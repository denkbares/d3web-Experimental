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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

/**
 * This type represents an instant in time. Different units of time (and their
 * combination, eg 1h30m) is supported.
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 12.08.2010
 */
public class TimeStampType extends AbstractType {

	private static final long[] TIME_FACTORS = {
			1, 1000, 1000, 60 * 1000, 60 * 1000, 60 * 60 * 1000, 24 * 60 * 60 * 1000 };

	private static final String[] TIME_UNITS = {
			"ms", "s", "sec", "m", "min", "h", "d" };

	private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
			"\\s*(\\d+)\\s*(ms|s|sec|m|min|h|d)\\s*",
			Pattern.CASE_INSENSITIVE);

	public TimeStampType() {
		sectionFinder = new RegexSectionFinder(TIMESTAMP_PATTERN);
	}

	public static boolean isValid(String sectionText) {
		return TIMESTAMP_PATTERN.matcher(sectionText).matches();
	}

	public static long getTimeInMillis(Section<TimeStampType> sec) {
		return getTimeInMillis(sec.getOriginalText());
	}

	public static long getTimeInMillis(String time) throws NumberFormatException {
		Matcher matcher = TIMESTAMP_PATTERN.matcher(time);

		long result = 0;
		int index = 0;
		while (matcher.find(index)) {
			String numString = matcher.group(1);
			String unit = matcher.group(2);
			double num = Double.parseDouble(numString);
			for (int i = 0; i < TIME_UNITS.length; i++) {
				if (TIME_UNITS[i].equalsIgnoreCase(unit)) {
					result += (long) Math.rint(TIME_FACTORS[i] * num);
				}
			}

			int next = matcher.end();
			if (next <= index) break;
			if (next >= time.length()) break;
			index = next;

		}

		return result;
	}

}
