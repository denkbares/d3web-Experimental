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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.we.algorithm.Suggestion;
import de.d3web.we.tables.CausalDiagnosisScore;
import de.d3web.we.tables.DecisionTable;
import de.d3web.we.tables.HeuristicDiagnosisTable;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * Renders the correction suggestions in the DefaultMarkup
 * of the {@link CausalDiagnosisScore}, {@link DecisionTable},
 * {@link HeuristicDiagnosisTable}
 * 
 * @author Johannes Dienst
 * @created 15.09.2011
 */
public class ApproximateCorrectionToolProvider implements ToolProvider
{

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext)
	{
		List<Suggestion> suggestions = new LinkedList<Suggestion>();

		ApproximateCorrectionProviderImpl impl = new ApproximateCorrectionProviderImpl();

		// Ensure there are no duplicates
		suggestions = new LinkedList<Suggestion>(
				new HashSet<Suggestion>(impl.getSuggestions(
						KnowWEUtils.getCompilingArticles(section).iterator().next(), section)));

		// Sort by ascending distance
		Collections.sort(suggestions);

		if (suggestions.size() == 0)
		{
			return new Tool[0];
		}

		Tool[] tools = new Tool[suggestions.size() + 1];

		tools[0] = new DefaultTool(
				"KnowWEExtension/images/quickfix.gif",
				Environment.getInstance().getMessageBundle().getString("KnowWE.Correction.do"),
				Environment.getInstance().getMessageBundle().getString("KnowWE.Correction.do"),
				null,
				"correct"
				);

		for (int i = 0; i < suggestions.size(); i++)
		{
			tools[i + 1] = new DefaultTool(
					"KnowWEExtension/images/correction_change.gif",
					suggestions.get(i).getSuggestion(),
					"",
					"KNOWWE.plugin.usersupport.doCorrection('" + section.getID() + "', '"
							+ suggestions.get(i).getSuggestion() + "');",
							"correct/item"
					);
		}

		return tools;
	}

}
