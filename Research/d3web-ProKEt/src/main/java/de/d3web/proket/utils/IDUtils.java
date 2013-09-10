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

import java.util.HashMap;
import java.util.Set;

/**
 * Convenience functions for ID tasks.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class IDUtils {
	/**
	 * prefixes for the fullIds
	 */
	private static final HashMap<String, String> prefixes = new HashMap<String, String>() {
		private static final long serialVersionUID = 5493520237551690919L;
		{
			put("dialog", "d");
			put("question", "q");
			put("answer", "a");
			put("questionnaire", "qu");
			put("group", "g");
			put("solution", "s");
		}
	};

	public static String getFormID(String baseID) {
		return "f_" + baseID;
	}

	/**
	 * Adds a correct namespace to an ID according to tag name.
	 * 
	 * @param id
	 * @param tagName
	 * @return Namespaced ID
	 */
	public static String getNamespacedId(String id, String tagName) {
		tagName = tagName.toLowerCase();
		if (!prefixes.keySet().contains(tagName)
				|| id.startsWith(prefixes.get(tagName) + "_")) {
			return id;
		}
		return prefixes.get(tagName) + "_" + id;
	}

	public static Set<String> getNamespaceTags() {
		return prefixes.keySet();
	}

	public static String getPanelID(String baseID) {
		return "panel-" + baseID;
	}

	public static String getSubID(String baseID) {
		return "sub-" + baseID;
	}

	public static String getTitleID(String baseID) {
		return "t-" + baseID;
	}

	public static String getTooltipID(String baseID) {
		return "tt-" + baseID;
	}

	public static boolean isValidId(String id) {
		return id.matches("[A-Za-z0-9]+");
	}

	public static boolean needsNamspace(String tag) {
		return prefixes.keySet().contains(tag);
	}

	public static String removeNamspace(String baseID) {
		if (baseID == null)
			return null;
		return baseID.replaceFirst("[^_]*_", "");
	}
}
