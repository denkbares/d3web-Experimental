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
package de.d3web.we.correction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.algorithm.DialogComponent;
import de.d3web.we.algorithm.Suggestion;
import de.d3web.we.util.UserSupportUtil;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author Johannes Dienst
 * @created 15.09.2011
 */
public class ApproximateCorrectionProviderImpl implements ApproximateCorrectionProvider {

	@Override
	public List<Suggestion> getSuggestions(KnowWEArticle article, Section<?> section) {

		// Collection<Section<? extends TermDefinition>> localTermMatches =
		// UserSupportUtil.getTermReferences(markup.getArticle());
		Collection<Section<?>> localTermMatches =
				UserSupportUtil.getTermReferencesCompilingArticle(section.getArticle(), section);
		ArrayList<String> localStrings = new ArrayList<String>();
		for (Section<?> def : localTermMatches)
			localStrings.add(def.getText());

		String toMatch = section.getText().trim();

		List<Suggestion> suggestions =
				DialogComponent.getInstance().
						getBestSuggestions(toMatch, localStrings);

		return suggestions;
	}

}
