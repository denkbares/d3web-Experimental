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

package de.knowwe.casetrain.renderer;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * A renderer that wrapps the content with a div and a specified css-class
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class DivStyleClassRenderer implements KnowWERenderer<Type> {

	private final String cssClass;
	private final KnowWERenderer<Type> customRenderer;

	public DivStyleClassRenderer(String s, KnowWERenderer<Type> c) {
		this.cssClass = s;
		customRenderer = c;
	}

	@Override
	public void render(Section<Type> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<div class='"
				+ cssClass
				+ "'>"));
		if (customRenderer != null) customRenderer.render(sec, user, string);
		else MouseOverTitleRenderer.getInstance().render(sec, user, string);
		string.append(KnowWEUtils.maskHTML("</div>"));

	}

}
