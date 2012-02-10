/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.jurisearch;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class BoxRenderer extends KnowWEDomRenderer {

	private final String clazz;

	public BoxRenderer(String clazz) {
		this.clazz = clazz;
	}

	@Override
	public void render(KnowWEArticle article, Section section, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<div"));
		string.append(" class='").append(clazz).append("'");

		string.append(KnowWEUtils.maskHTML(">"));
		DelegateRenderer.getInstance().render(article, section, user, string);

		string.append(KnowWEUtils.maskHTML("</div>"));
	}

}
