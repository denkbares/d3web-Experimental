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

package de.d3web.we.kdom.xcl;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * This renderer adds the kdomid to the span in which a Section is rendered. Use
 * it to enable highlighting is possible. It Uses the
 * XCLRelationHighlightingRenderer.
 * 
 * @author Johannes Dienst
 */
public class XCLRelationKdomIdWrapperRenderer extends KnowWEDomRenderer {

	private static XCLRelationKdomIdWrapperRenderer instance;

	public static synchronized XCLRelationKdomIdWrapperRenderer getInstance() {
		if (instance == null) instance = new XCLRelationKdomIdWrapperRenderer();
		return instance;
	}

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {
		// Span is for kdom id.
		// id can be found by the class.
		StringBuilder b = new StringBuilder();
		XCLRelationHighlightingRenderer.getInstance().render(article, sec, user, b);
		string.append(KnowWEUtils.maskHTML("<span id='" + sec.getID()
				+ "' class = 'XCLRelationInList'>"
											+ b.toString()
											+ "</span>"));
	}
}
