/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.we.kdom.include;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class IncludedFromSectionRenderer extends KnowWEDomRenderer {

	
	@Override
	public void render(Section sec, KnowWEUserContext user, StringBuilder string) {
		String src = sec instanceof IncludedFromSection ? ((IncludedFromSection) sec).getSrc() 
				: "Unknown Source";
		StringBuilder content = new StringBuilder();
		StringBuilder b = new StringBuilder();
		int i = 0;
		for (Section child:sec.getChildren()) {
			if (!((i < 2 || i > sec.getChildren().size() - 3) && child.isEmpty())) {
				child.getObjectType().getRenderer().render(child, user, content);
				b.append(content.toString());
				// make content empty, so no new Object has to be created
				content.delete(0, content.length());
			}
			i++;
		}

		//renderedContent = DefaultDelegateRenderer.getInstance().render(sec, user, web, topic);
		string.append(wrapIncludeFrame(b.toString(), src));		
	}
	
	protected String wrapIncludeFrame(String renderedContent, String src) {
		
		//relies on src begin 'src="article/section"'
		String articleName = src.substring(5, src.indexOf('/'));
		String linkText = src.substring(5, src.length() - 1);
		
		String srclink = KnowWEEnvironment.getInstance().getWikiConnector().createWikiLink(articleName, linkText);
		return KnowWEEnvironment.maskHTML("<div style=\"text-align:left; padding-top:5px; padding-right:5px; padding-left:5px; border:thin solid #99CC99\">") 
			+ renderedContent + KnowWEEnvironment.maskHTML("<div style=\"text-align:right\"><font size=\"1\">")+ "From: " + srclink + KnowWEEnvironment.maskHTML("</font></div></div><p>");
	}

}
