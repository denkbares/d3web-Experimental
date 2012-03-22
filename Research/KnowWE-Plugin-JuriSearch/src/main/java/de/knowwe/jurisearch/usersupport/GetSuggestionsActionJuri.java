/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch.usersupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.usersupport.algorithm.DialogComponent;
import de.knowwe.usersupport.algorithm.Suggestion;
import de.knowwe.usersupport.util.UserSupportUtil;


/**
 * TODO what terms to match here?
 * 
 * @author Johannes Dienst
 * @created 22.02.2012
 */
public class GetSuggestionsActionJuri extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException
	{

		String result = handle(context);
		if (result != null && context.getWriter() != null)
		{
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String handle(UserActionContext context) throws IOException
	{
		String toMatch = context.getParameter("toMatch");
		String sectionID = context.getParameter("sectionID");

		Section<?> markup = Sections.getSection(sectionID);

		if (toMatch == null)
		{
			return "[]";
		}

		// Collection<Section<? extends TermDefinition>> localTermMatches =
		// UserSupportUtil.getTermReferences(markup.getArticle());
		Collection<Section<?>> localTermMatches =
				UserSupportUtil.getTermReferencesCompilingArticle(markup.getArticle(), markup);
		ArrayList<String> localStrings = new ArrayList<String>();
		for (Section<?> def : localTermMatches)
			localStrings.add(def.getText());

		List<Suggestion> suggestions =
				DialogComponent.getInstance().
				getBestSuggestionsAllAlgorithms(toMatch, localStrings);

		String toReturn = UserSupportUtil.buildJSONArray(suggestions);

		return toReturn;

	}

}
