/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.kdom.manchester.frame.DefaultFrame;

/**
 * Highlights elements of the Manchester OWL syntax in the article. Also wraps
 * the long lines so no ugly horizontal scrolling is necessary.
 *
 * @author Stefan Mark
 * @created 10.08.2011
 */
public class ManchesterSyntaxFrameRenderer extends KnowWEDomRenderer<DefaultFrame> {

	/**
	 * Specifies if a link to the article where the current {@link DefaultFrame}
	 * has been found, should be rendered into the resulting page.
	 */
	private boolean renderLink = false;

	@Override
	public void render(KnowWEArticle article, Section<DefaultFrame> sec, UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<pre style=\"white-space:pre-wrap;background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;position:relative;\">"));

		string.append(KnowWEUtils.maskHTML("<div style=\"position:absolute;top:0px;right:0px;border-bottom: 1px solid #E5E5E5;border-left: 1px solid #E5E5E5;padding:5px\">"
				+ getFrameName(sec)
				+ getLink(sec)
				+ "</div>"));

		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</pre>"));
	}

	public boolean getRenderLink() {
		return renderLink;
	}

	public void setRenderLink(boolean render) {
		renderLink = render;
	}

	/**
	 * Returns the name of the current {@link DefaultFrame} for visual
	 * highlighting to the user on the article page.
	 *
	 * @created 22.09.2011
	 * @param Section<DefaultFrame> section The current {@link DefaultFrame}
	 * @return The name of the {@link DefaultFrame}
	 */
	private String getFrameName(Section<DefaultFrame> section) {
		return section.get().getName();
	}

	/**
	 * Renders a link to the current article the {@link DefaultFrame} can be
	 * found.
	 *
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section The current {@link DefaultFrame}
	 * @return A link to the article the section can be found
	 */
	private String getLink(Section<DefaultFrame> section) {
		if (renderLink) {
			return " - <a href=\"Wiki.jsp?page=" + section.getTitle()
					+ "\" title=\"View section in occuring article\"/>"
					+ section.getTitle() + "</a>";
		}
		return "";
	}
}
