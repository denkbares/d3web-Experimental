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

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class ConceptPageLinkProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {

		String text = section.getText();

		if (section.get() instanceof IncrementalTermReference) {
			@SuppressWarnings("unchecked")
			Section<IncrementalTermReference> incSection = (Section<IncrementalTermReference>) section;
			String termIdentifier = incSection.get().getTermIdentifier(incSection);
			Collection<Section<? extends SimpleDefinition>> definitions =
					IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
							termIdentifier);
			if (definitions != null && definitions.size() > 0) {
				Section<?> definition = definitions.iterator().next();
				KnowWEArticle defArticle = definition.getArticle();

				if (defArticle == null) {
					return new Tool[] {}; // predefined term
				}
				String defArticleName = defArticle.getTitle();
				StringBuffer link = new StringBuffer();
				link.append(defArticleName);
				link.append("#");
				link.append(definition.getID());

				text = link.toString();
			}
		}

		Tool t = new DefaultTool(
				"KnowWEExtension/images/dt_icon_premises.gif",
				"<a href='Wiki.jsp?page=" + text + "'>" + "To definition" + "</a>",
				text,
				null);
		return new Tool[] { t };
	}

}
