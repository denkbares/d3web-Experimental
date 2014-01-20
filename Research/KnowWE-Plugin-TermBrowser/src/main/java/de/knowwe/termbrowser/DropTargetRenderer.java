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
package de.knowwe.termbrowser;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;

public class DropTargetRenderer implements Renderer {

	public void renderPre(Section<?> section, UserContext user, RenderResult string) {
		if (section.getText().trim().length() > 3) {
			string.appendHtml("<div style='display:inline;' dragdropid='" + section.getID()
					+ "' class='dropTargetMarkup'>");

		}
	}

	public void renderPost(Section<?> section, UserContext user, RenderResult string) {
		if (section.getText().trim().length() > 3) {
			string.appendHtml("</div>");
		}
	}

	@Override
	public void render(Section<?> section, UserContext user, RenderResult result) {
		renderPre(section, user, result);

		// call original (and potentially additional plugged) renderers
		Renderer nextRendererForType = Environment.getInstance().getNextRendererForType(
				section.get(), this);
		if (nextRendererForType != null) {
			nextRendererForType.render(section, user, result);
		}

		renderPost(section, user, result);

	}

}
