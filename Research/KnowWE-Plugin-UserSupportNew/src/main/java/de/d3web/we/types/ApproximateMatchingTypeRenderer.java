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
package de.d3web.we.types;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

/**
 * 
 * @author Johannes Dienst
 * @created 16.09.2011
 */
public class ApproximateMatchingTypeRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<div>"));
		new ToolMenuDecoratingRenderer(
				new StyleRenderer("color:rgb(40, 40, 160)")).render(sec, user, string);
		string.append(KnowWEUtils.maskHTML("</div>"));
	}

}
