/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.we.biolog.action;

import java.util.List;

import de.d3web.we.action.DeprecatedAbstractKnowWEAction;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.search.SearchTerminologyHandler;

/**
 * AutocompleteAction.
 * 
 * @author smark
 */
public class AutocompleteAction extends DeprecatedAbstractKnowWEAction {

	@Override
	public String perform(KnowWEParameterMap parameterMap) {

		String query = parameterMap.get("searchText");
		if(query == null || query.length() == 0) return "";
		
		StringBuilder result = new StringBuilder();
		
		List<String> suggestions = SearchTerminologyHandler.getInstance().getCompletionSuggestions(query);
		
		for(int i = 0; i < suggestions.size(); i++){
			if( i + 1 < suggestions.size()){
				result.append( suggestions.get( i ) + "\\c" );
			} else {
				result.append( suggestions.get( i ) );
			}
		}
		return result.toString();
	}
}
