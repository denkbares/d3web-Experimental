/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.knowwe.ontology.autocompletion;

import java.io.IOException;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.completion.AutoCompletionSlotProvider;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 28.12.15.
 */
public class OntologyCompletionSlotProvider implements AutoCompletionSlotProvider {
	@Override
	public void init(Section<?> section, UserContext user) throws IOException {
		// nothing to do
	}

	@Override
	public void renderAutoCompletionSlot(RenderResult string, Section<?> section) {
		// if service is available, render actual search markup
		string.appendHtml("<div id='sscSearchSlot'>");
		string.appendHtml("<span class='semanticautocompletionmaster' style='display:none'>");
		string.append(section.getID());
		string.appendHtml("</span>");

		string.appendHtmlTag("input",
				"type", "text",
				"name", "Semantic Autocompletion",
				"value", "",
				"class", "termbrowserautocompletion",
				"id", "semanticautocompletion-" + section.hashCode()
		);
		string.appendHtml("</div>");
	}
}
