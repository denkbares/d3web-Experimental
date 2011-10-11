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

import java.io.IOException;
import java.util.List;

import de.d3web.we.search.SearchTerminologyHandler;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * AutocompleteAction.
 *
 * @author smark
 */
public class AutocompleteAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		String query = context.getParameter("searchText");
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
