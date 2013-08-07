/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.relationMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 06.08.2013
 */
public class RelationMarkupUtils {

	public static String getLineRegex(String key) {
		return getLineRegex(key, true);
	}

	public static String getLineRegex(String key, boolean withColon) {
		String keyRegex = getKeyRegex(key, withColon);
		return "(?i)^" + keyRegex + "\\u0020?(.*?)(\r?\n(\\s*)\r?\n|$)";
	}

	public static String getKeyRegex(String key) {
		return getKeyRegex(key, true);
	}

	public static String getKeyRegex(String key, boolean withColon) {
		String result = "(" + key;
		if (withColon) {
			result += ":";
		}
		result += ")";
		return result;
	}

}
