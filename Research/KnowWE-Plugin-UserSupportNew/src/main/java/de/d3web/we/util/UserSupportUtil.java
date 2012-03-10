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
package de.d3web.we.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.we.algorithm.Suggestion;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

/**
 * 
 * @author Johannes Dienst
 * @created 15.12.2011
 */
public class UserSupportUtil {

	/**
	 * 
	 * Collect only the TermDefinitions used by a given Section
	 * 
	 * @created 23.12.2011
	 * @param article
	 * @param markup
	 * @return
	 */
	public static Collection<Section<?>> getTermReferencesCompilingArticle(Article article, Section<?> markup)
	{

		article = KnowWEUtils.getCompilingArticles(markup).iterator().next();

		TerminologyManager tH = KnowWEUtils.getTerminologyManager(article);
		Collection<String> allDefinedTerms = tH.getAllDefinedTerms();

		Collection<Section<?>> globalTerms = new LinkedList<Section<?>>();
		for (String term : allDefinedTerms)
		{
			globalTerms.addAll(tH.getTermDefiningSections(term));
		}

		return globalTerms;
	}

	/**
	 * 
	 * @created 22.02.2012
	 * @param suggestions
	 * @return
	 */
	public static String buildJSONArray(List<Suggestion> suggestions)
	{
		StringBuilder buildi = new StringBuilder();

		buildi.append("[");
		for (Suggestion s : suggestions)
		{
			if (!s.getSuggestion().equals(""))
				buildi.append("," + "\"" + s.getSuggestion() + "\"");
		}
		buildi.append("]");
		String toReturn = buildi.toString().replaceFirst(",", "");
		toReturn = toReturn.replaceAll("\"\"", "\"");
		return toReturn;
	}

	/**
	 * Has the section some ToolProvider attached, render the tools into the
	 * resulting HTML output. This is a adaption from the
	 * ToolMenuDecoratingRenderer. This was needed to include some of the
	 * ToolProvider beside the DefaultMarkup. Maybe this can be handled better
	 * in the future.
	 * 
	 * @created 12.11.2011
	 * @param article
	 * @param sec
	 * @param user
	 * @return
	 */
	public static String renderTools(Section<?> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		Tool[] tools = ToolUtils.getTools(sec, user);

		for (Tool t : tools) {
			String icon = t.getIconPath();
			String jsAction = t.getJSAction();
			boolean hasIcon = icon != null && !icon.trim().isEmpty();

			string.append("<span class=\"" + t.getClass().getSimpleName() + "\" >"
					+ "<"
					+ (jsAction == null ? "span" : "a")
					+ " class=\"markupMenuItem\""
					+ (jsAction != null
					? " href=\"javascript:" + t.getJSAction() + ";undefined;\""
							: "") +
							" title=\"" + t.getDescription() + "\">" +
							(hasIcon ? ("<img src=\"" + icon + "\"></img>") : "") +
							"</" + (jsAction == null ? "span" : "a") + ">" +
					"</span>");
		}
		return string.toString();
	}

}
