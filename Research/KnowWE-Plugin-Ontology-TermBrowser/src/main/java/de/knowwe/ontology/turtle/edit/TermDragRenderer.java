/*
 * Copyright (C) 2013 denkbares GmbH
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

package de.knowwe.ontology.turtle.edit;

import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

/**
 * Created by jochenreutelshofer on 25.03.14.
 */
public class TermDragRenderer implements Renderer {
	@Override
	public void render(Section<?> section, UserContext user, RenderResult result) {

		String identifier = getIdentifierString(section);
		result.appendHtml("<div style='display:inline;position:static !important;' class='dragMarkupTerm'>");
		result.appendHtml("<div class='termID'>" + identifier + "</div>");
		Renderer nextRenderer = Environment.getInstance().getNextRendererForType(section.get(), this);
		if (nextRenderer == null) {
			nextRenderer = DelegateRenderer.getInstance();
		}
		nextRenderer.render(section, user, result);
		result.appendHtml("</div>");
	}

	protected String getIdentifierString(Section<?> section) {
		List<Section<Term>> termRefs = Sections.successors(section, Term.class);
		Section<Term> lastRefSection = termRefs.get(termRefs.size() - 1);
		return lastRefSection.get().getTermIdentifier(lastRefSection).toExternalForm();
	}
}
