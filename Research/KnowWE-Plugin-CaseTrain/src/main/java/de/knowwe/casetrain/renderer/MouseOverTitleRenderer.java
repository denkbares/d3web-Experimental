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
package de.knowwe.casetrain.renderer;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupType;


/**
 * 
 * To show the KnowWEObjectType on mouseover.
 * Uses the Title-Tag of HTML to achieve this behaviour.
 * 
 * @author Johannes Dienst
 * @created 18.04.2011
 */
public class MouseOverTitleRenderer extends KnowWEDomRenderer {

	private static MouseOverTitleRenderer uniqueInstance;

	public static MouseOverTitleRenderer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MouseOverTitleRenderer();
		}
		return uniqueInstance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user,
			StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span title=\"" + sec.get().getName() +  "\">"));
		// TODO Should be delegated but does NOT work!
		if (sec.get().isAssignableFromType(BlockMarkupType.class)) {
			Section<BlockMarkupContent> section = Sections.findSuccessor(sec, BlockMarkupContent.class);
			section.get().getRenderer().render(article, section, user, string);
			return;
		}

		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

}
