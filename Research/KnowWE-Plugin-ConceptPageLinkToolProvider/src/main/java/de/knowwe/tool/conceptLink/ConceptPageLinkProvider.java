/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.tool.conceptLink;

import java.util.Collection;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.tools.ToolUtils;
import de.knowwe.util.Icon;

public class ConceptPageLinkProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {

		Section<?> definition = getDefinitionSection(section);
		if (definition == null) return ToolUtils.emptyToolArray();

		Article defArticle = definition.getArticle();
		if (defArticle == null) {
			// predefined term
			return ToolUtils.emptyToolArray();
		}

		String defArticleName = defArticle.getTitle();
		StringBuffer link = new StringBuffer();
		link.append(defArticleName);
		link.append("#");
		link.append(definition.getID());

		String text = link.toString();
		Tool tool = new DefaultTool(
				Icon.OPENPAGE,
				"<a href='Wiki.jsp?page=" + text + "'>" + "To definition" + "</a>",
				text,
				Tool.CATEGORY_INFO);
		return new Tool[] { tool };
	}

	private Section<?> getDefinitionSection(Section<?> section) {
		Section<?> definition = null;
		if (section.get() instanceof IncrementalTermReference) {
			Section<IncrementalTermReference> incSection = Sections.cast(section,
					IncrementalTermReference.class);
			Identifier termIdentifier = incSection.get().getTermIdentifier(incSection);
			ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
			Collection<Section<? extends SimpleDefinition>> definitions =
					terminology.getTermDefinitions(termIdentifier);
			if (definitions != null && definitions.size() > 0) {
				definition = definitions.iterator().next();
			}
		}
		return definition;
	}

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		Section<?> definition = getDefinitionSection(section);
		return definition != null && definition.getArticle() != null;
	}

}
