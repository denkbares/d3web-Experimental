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

package de.d3web.we.wisec.kdom;

import de.d3web.we.wisec.kdom.subtreehandler.ListSubstancesOWLSubtreeHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.user.UserContext;

/**
 * Content type for the ListSubstances section.
 * 
 * @author Sebastian Furth
 */
public class ListSubstancesType extends AbstractType {

	public ListSubstancesType() {
		setSectionFinder(new AllTextSectionFinder());
		addSubtreeHandler(Priority.LOWEST,
				new ListSubstancesOWLSubtreeHandler());
		this.setCustomRenderer(new ListSubstancesRenderer());

		// addSubtreeHandler(new ListSubstancesD3SubtreeHandler());
		// addChildType(new WISECTable());
	}

	class ListSubstancesRenderer extends KnowWEDomRenderer<ListSubstancesType> {

		@Override
		public void render(KnowWEArticle article, Section<ListSubstancesType> sec, UserContext user, StringBuilder string) {

			// %%zebra-table
			// %%sortable
			string.append("\n%%zebra-table");
			string.append("\n%%sortable\n");
			DelegateRenderer.getInstance().render(article, sec, user, string);
			string.append("\n/%");
			string.append("\n/%\n");

		}

	}
}
