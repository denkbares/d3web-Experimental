/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.pathcoloring;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxTraceHighlight;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class HighlightProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		Tool refresh = getHighlightTool(section, userContext);
		return new Tool[] { refresh };
	}

	protected Tool getHighlightTool(Section<?> section, UserContext userContext) {

		boolean dohighlighting =
				DiaFluxTraceHighlight.checkForHighlight(userContext,
						"anomalie_coverage");

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
					"Hide Anomalies",
					"Highlights Anomalies in the flowchart.",
					jsAction);
		}
		else {
			String jsAction = "var url = window.location.href;" +
					"if (url.search('highlight')!=-1)" +
					"{url = url.replace(/highlight=[^&]*/g, 'highlight=anomalie_coverage');}" +
					"else {" +
					"if (url.indexOf('?') == -1) {url += '?';}" +
					"url = url.replace(/\\?/g,'?highlight=anomalie_coverage&');}" +
					"window.location = url;";
			return new DefaultTool(
					"KnowWEExtension/flowchart/icon/debug16.png",
					"Show Anomalies",
					"Highlights Anomalies in the flowchart.",
					jsAction);
		}
	}
}