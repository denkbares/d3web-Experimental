/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.knowwe.diaflux.coverage;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxTraceHighlight;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * Enables highlighting of covered nodes and edges
 * 
 * @author Reinhard Hatko
 * @created 10.10.2011
 */
public class CoverageProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {
		Tool refresh = getHighlightTool(article, section, userContext);
		return new Tool[] { refresh };
	}

	protected Tool getHighlightTool(KnowWEArticle article, Section<?> section, UserContext userContext) {

		boolean dohighlighting =
				DiaFluxTraceHighlight.checkForHighlight(userContext,
						DiaFluxCoverageHighlight.COVERAGE_HIGHLIGHT);

		if (dohighlighting) {
			String jsAction = "var url = window.location.href;" +
					"if (url.search('highlight')!=-1)" +
					"{url = url.replace(/highlight=[^&]*/g, 'highlight=none');}" +
					"else {" +
					"if (url.indexOf('?') == -1) {url += '?';}" +
					"url = url.replace(/\\?/g,'?highlight=none&');}" +
					"window.location = url;";
			return new DefaultTool(
					"KnowWEExtension/flowchart/icon/debug16.png",
					"Hide Coverage",
					"Highlights covered nodes and edges in the flowchart.",
					jsAction);
		}
		else {
			String jsAction = "var url = window.location.href;" +
					"if (url.search('highlight')!=-1)" +
					"{url = url.replace(/highlight=[^&]*/g, 'highlight=coverage');}" +
					"else {" +
					"if (url.indexOf('?') == -1) {url += '?';}" +
					"url = url.replace(/\\?/g,'?highlight=coverage&');}" +
					"window.location = url;";
			return new DefaultTool(
					"KnowWEExtension/flowchart/icon/debug16.png",
					"Show Coverage",
					"Highlights covered nodes and edges in the flowchart.",
					jsAction);
		}
	}
}
