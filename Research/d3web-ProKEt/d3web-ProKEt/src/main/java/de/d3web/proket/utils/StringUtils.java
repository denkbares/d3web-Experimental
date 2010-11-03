/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions for working with string objects.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class StringUtils {
	public static String capitalizeFirstLetter(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		return String.format("%s%s", Character.toUpperCase(string.charAt(0)),
				string.substring(1));
	}

	/**
	 * Compare a String to a {@link Boolean} object.
	 * 
	 * @param string
	 *            String to compare.
	 * @param value2
	 *            {@link Boolean} object to compare.
	 * @return see {@link Comparable}
	 */
	/*
	 * public static Boolean compareValue(String string, Boolean value2) {
	 * Boolean value1 = getValue(string, null); if (value1 == null) { if (value2
	 * == null) { return true; } else { return false; } } return
	 * (value1.compareTo(value2) == 0); }
	 */

	/**
	 * Decapitalize the String given, meaning that the first to letter is
	 * decapitalized if necessary
	 * 
	 * @param string the String to be decapitalized
	 * @return String the decapitalized String
	 */
	public static String decapitalizeFirstLetter(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		return String.format("%s%s",
				Character.toLowerCase(string.charAt(0)), string.substring(1));
	}

	/**
	 * Converts a PascalCase/CamelCase string to a xml-like version. Something
	 * like ThisObject would be transformed into this-object.
	 * 
	 * @param field A field name
	 * @return String with all capital characters replaced with a dash and the
	 *         non-capital version of the character.
	 */
	public static String fieldToXml(String field) {
		StringBuilder sb = new StringBuilder(field);
		Pattern p = Pattern.compile("([a-z])([A-Z])");
		while (true) {
			Matcher m = p.matcher(sb.toString());
			if (m.find()) {
				sb.replace(m.start(), m.end(), m.group(1) + "-"
						+ m.group(2).toLowerCase());
			} else {
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Process a variable list of objects and return the first one not null.
	 * 
	 * @created 10.10.2010
	 * @param objects the Object list
	 * @return the first non-null Object
	 */
	public static Object firstNonNull(Object... objects) {
		for (Object o : objects) {
			if (o != null) {
				return o;
			}
		}
		return null;
	}

	public static String getInputStreamContent(InputStream in)
			throws IOException {
		if (in == null) {
			return null;
		}
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}

		bufferedReader.close();
		return stringBuilder.toString();
	}

	/* SOME BOOLEAN TO STRING FUNCTIONS, formerly BooleanString class */

	/*
	 * public static Boolean getValue(String string, Boolean defaultValue) { if
	 * (string == null) return defaultValue; if (string.equalsIgnoreCase("true")
	 * || string.equalsIgnoreCase("yes") || string.equals("1")) return
	 * Boolean.valueOf(true); if (string.equalsIgnoreCase("false") ||
	 * string.equalsIgnoreCase("no") || string.equals("0")) return
	 * Boolean.valueOf(false);
	 * 
	 * if (defaultValue == null) return null; return defaultValue; }
	 */

	public static String toHTML(String string) {
		StringBuffer sb = new StringBuffer(string.length());

		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);

			// HTML Special Chars
			if (c == '"') {
				sb.append("&quot;");
			}
			else if (c == '&') {
				sb.append("&amp;");
			}
			else if (c == '<') {
				sb.append("&lt;");
			}
			else if (c == '>') {
				sb.append("&gt;");
			}
			else if (c == '\n') {
				// Handle Newline
				sb.append("&lt;br/&gt;");
			}
			else {
				int ci = 0xffff & c;
				if (ci < 160) {
					// nothing special only 7 Bit
					sb.append(c);
				}
				else {
					// Not 7 Bit use the unicode system
					sb.append("&#");
					sb.append(Integer.valueOf(ci).toString());
					sb.append(';');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts a xml-like string to a PascalCase version.
	 * 
	 * @param xml
	 * @return PascalCase string.
	 */
	public static String xmlToField(String xml) {
		StringBuilder sb = new StringBuilder(xml);
		Pattern p = Pattern.compile("([a-zA-Z])-([a-z])");
		while (true) {
			Matcher m = p.matcher(sb.toString());
			if (m.find()) {
				sb.replace(m.start(), m.end(), m.group(1)
						+ m.group(2).toUpperCase());
			} else {
				break;
			}
		}
		return sb.toString();
	}
}
