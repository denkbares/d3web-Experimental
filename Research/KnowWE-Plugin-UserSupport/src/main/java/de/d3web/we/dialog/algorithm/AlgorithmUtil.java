/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.dialog.algorithm;

import java.util.List;


/**
 * 
 * Contains static methods used in this package.
 * 
 * @author Johannes Dienst
 * @created 21.11.2011
 */
public class AlgorithmUtil {

	/**
	 * 
	 * Returns if the matchList has a SuggestionValuePair with a
	 * searched Suggestion s in it.
	 * -1 if not in List
	 * index of SuggestionValuePair otherwise
	 * 
	 * @created 21.11.2011
	 * @param matchList
	 * @param s
	 * @return
	 */
	public static int containsSuggestion(
			List<DialogComponent.SuggestionValuePair> matchList, Suggestion s) {
		for (DialogComponent.SuggestionValuePair pair : matchList) {
			if (pair.getSuggestion().compareTo(s) == 0)
				return matchList.indexOf(pair);
		}
		return -1;
	}

}
