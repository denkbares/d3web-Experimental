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
package de.d3web.we.editor.action;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.d3web.we.algorithm.DialogComponent;
import de.d3web.we.algorithm.Suggestion;
import de.d3web.we.util.UserSupportUtil;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;


/**
 * 
 * Receives the call from the Editor component to get
 * the suggestions from {@link DialogComponent}.
 * 
 * Returns them as a JSON-Array
 * 
 * @author Johannes Dienst
 * @created 29.11.2011
 */
public class GetSuggestionsAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = handle(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String handle(UserActionContext context) throws IOException {
		String title = context.getTitle();
		String toMatch = context.getParameter("toMatch");
		String sectionID = context.getParameter("sectionID");

		Section<?> markup = Sections.getSection(sectionID);
		//		Section<? extends DefaultMarkup> mark = (Section<? extends DefaultMarkup>) markup;
		//		String packageName = DefaultMarkupType.getAnnotation(mark, "package");
		//		packageName = packageName.replaceAll("[\\r\\n\\}]", "");

		if (toMatch == null) {
			return "[]";
		}

		//		Collection<Section<? extends TermDefinition>> localTermMatches =
		//				UserSupportUtil.getTermReferences(markup.getArticle());
		Collection<Section<? extends TermDefinition>> localTermMatches =
				UserSupportUtil.getTermReferencesCompilingArticle(markup.getArticle(), markup);

		List<Suggestion> suggestions =
				DialogComponent.getInstance().
				getBestSuggestionsUsedAlgorithm(toMatch, localTermMatches);

		// build JSON-Array with suggestions
		StringBuilder buildi = new StringBuilder();

		buildi.append("[");
		for (Suggestion s : suggestions) {
			buildi.append("," + "\"" + s.getSuggestion() + "\"");
		}
		buildi.append("]");
		return buildi.toString().replaceFirst(",", "");

	}

}
