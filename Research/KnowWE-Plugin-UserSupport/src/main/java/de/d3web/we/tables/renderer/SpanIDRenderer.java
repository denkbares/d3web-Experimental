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
package de.d3web.we.tables.renderer;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class SpanIDRenderer extends KnowWEDomRenderer<Type> {

	@Override
	public void render(KnowWEArticle article, Section<Type> sec, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span id='" + sec.getID() + "'>"));
		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

}