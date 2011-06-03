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

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * A renderer that wrapps the content with a div and a specified css-class
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class DivStyleClassRenderer extends KnowWEDomRenderer<Type> {

	private final String cssClass;
	private final KnowWEDomRenderer<Type> customRenderer;

	public DivStyleClassRenderer(String s, KnowWEDomRenderer<Type> c) {
		this.cssClass = s;
		customRenderer = c;
	}

	@Override
	public void render(KnowWEArticle article, Section<Type> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<div class='"
				+ cssClass
				+ "'>"));
		if (customRenderer != null)
			customRenderer.render(article, sec, user, string);
		else
			MouseOverTitleRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</div>"));

	}


}
