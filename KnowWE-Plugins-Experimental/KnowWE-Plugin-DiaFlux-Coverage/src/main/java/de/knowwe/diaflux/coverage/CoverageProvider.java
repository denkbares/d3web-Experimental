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

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxTraceHighlight;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.util.Icon;

/**
 * Enables highlighting of covered nodes and edges
 *
 * @author Reinhard Hatko
 * @created 10.10.2011
 */
public class CoverageProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		Tool refresh = getHighlightTool(section, userContext);
		return new Tool[] { refresh };
	}

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return true;
	}

	protected Tool getHighlightTool(Section<?> section, UserContext userContext) {

		boolean dohighlighting =
				DiaFluxTraceHighlight.checkForHighlight(userContext,
						DiaFluxCoverageHighlight.COVERAGE_HIGHLIGHT);

		String description = "Highlights covered nodes and edges in the flowchart.";
		if (dohighlighting) {
			return new DefaultTool(Icon.DEBUG, "Hide Coverage", description,
					DiaFluxTraceHighlight.getDeactivationJSAction(), Tool.CATEGORY_UTIL);
		}
		else {
			return new DefaultTool(
					Icon.DEBUG,
					"Show Coverage",
					description,
					DiaFluxTraceHighlight.getActivationJSAction(DiaFluxCoverageHighlight.COVERAGE_HIGHLIGHT), Tool.CATEGORY_UTIL);
		}
	}
}
