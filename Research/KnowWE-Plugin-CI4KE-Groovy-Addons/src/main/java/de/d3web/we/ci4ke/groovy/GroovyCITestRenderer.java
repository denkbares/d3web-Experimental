/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import de.d3web.we.knowledgebase.KnowledgeBaseType;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.core.kdom.KnowWEArticle;
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
public class GroovyCITestRenderer extends DefaultMarkupRenderer<GroovyCITestType> {

	public GroovyCITestRenderer() {
		super("KnowWEExtension/images/terminal-icon.png", false);
	}

	@Override
	protected void renderContents(KnowWEArticle article, Section<GroovyCITestType> section, UserContext user, StringBuilder string) {
		KnowWERessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shCore.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);
		KnowWERessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shThemeEclipse.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);

		KnowWERessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shCore.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("syntaxhighlighter_3.0.83/shBrushGroovy.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("SyntaxHighlighter.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);

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
