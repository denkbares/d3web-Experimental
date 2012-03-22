/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.usersupport.correction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.usersupport.algorithm.DialogComponent;
import de.knowwe.usersupport.algorithm.Suggestion;
import de.knowwe.usersupport.util.UserSupportUtil;

/**
 * 
 * Searches for Suggestions in the terminology. Used in
 * {@link ApproximateCorrectionToolProvider} to provide the
 * corrections.
 * 
 * @author Johannes Dienst
 * @created 15.09.2011
 */
public class ApproximateCorrectionProviderImpl implements ApproximateCorrectionProvider
{

	@Override
	public List<Suggestion> getSuggestions(Article article, Section<?> section)
	{
		Collection<Section<?>> localTermMatches =
				UserSupportUtil.getTermReferencesCompilingArticle(section.getArticle(), section);
		ArrayList<String> localStrings = new ArrayList<String>();
		for (Section<?> def : localTermMatches)
			localStrings.add(def.getText());

		String toMatch = section.getText().trim();

		//		for (int j = 0; j < 4; j++)
		//		{
		//			for (int i = 0; i < 10; i++)
		//			{
		//				MatchingAlgorithm algo =
		//						DialogComponent.getInstance().getPossibleMatchingAlgorithms().get(5+j);
		//				List<Suggestion> suggestions =
		//						DialogComponent.getInstance().
		//						getSuggestions(toMatch, localStrings, algo);
		//			}
		//
		//		}


		List<Suggestion> suggestions =
				DialogComponent.getInstance().
				getBestSuggestionsAllAlgorithms(toMatch, localStrings);


		return suggestions;
		//		return new ArrayList<Suggestion>();
	}

}
