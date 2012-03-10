/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import de.d3web.we.knowledgebase.KnowledgeBaseType;
import de.knowwe.core.RessourceLoader;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Renderer for the {@link GroovyCITestType}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 30.11.2010
 */
public class GroovyCITestRenderer extends DefaultMarkupRenderer {

	public GroovyCITestRenderer() {
		super("KnowWEExtension/images/terminal-icon.png");
	}

	@Override
	protected void renderContents(Section<?> section, UserContext user, StringBuilder string) {
		RessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shCore.css",
				RessourceLoader.RESOURCE_STYLESHEET);
		RessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shThemeEclipse.css",
				RessourceLoader.RESOURCE_STYLESHEET);

		RessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shCore.js",
				RessourceLoader.RESOURCE_SCRIPT);
		RessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shBrushGroovy.js",
				RessourceLoader.RESOURCE_SCRIPT);
		RessourceLoader.getInstance().add("SyntaxHighlighter.js",
				RessourceLoader.RESOURCE_SCRIPT);

		String testname = KnowledgeBaseType.getAnnotation(section,
				GroovyCITestType.ANNOTATION_NAME);
		string.append(KnowWEUtils.maskHTML("<b>" + testname + "</b>\n"));

		// string.append(KnowWEUtils.maskHTML("<span>\n"));
		string.append(KnowWEUtils.maskHTML("<script type=\"syntaxhighlighter\" class=\"brush: groovy\">"));
		string.append(KnowWEUtils.maskHTML("<![CDATA[\n"));

		string.append(DefaultMarkupType.getContent(section));

		string.append(KnowWEUtils.maskHTML("\n]]></script>\n"));
		// string.append(KnowWEUtils.maskHTML("</span>\n"));
	}

}
