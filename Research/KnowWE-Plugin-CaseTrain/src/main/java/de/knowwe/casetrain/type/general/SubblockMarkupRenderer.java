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
package de.knowwe.casetrain.type.general;

import de.knowwe.casetrain.renderer.MouseOverTitleRenderer;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author Johannes Dienst
 * @created 05.06.2011
 */
public class SubblockMarkupRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {
		string.append(Strings.maskHTML("<div class='"
				+ ((SubblockMarkup) sec.get()).getCSSClass()
				+ "'>"));

		// Only render the Subblock and not the
		// PlainText surrounding it.
		MouseOverTitleRenderer.getInstance().render(
				Sections.findSuccessor(sec, SubblockMarkupContent.class), user,
				string);
		string.append(Strings.maskHTML("</div>"));
	}
}
