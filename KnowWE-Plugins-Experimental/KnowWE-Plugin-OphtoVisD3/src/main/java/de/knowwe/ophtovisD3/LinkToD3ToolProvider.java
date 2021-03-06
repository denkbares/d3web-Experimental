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
package de.knowwe.ophtovisD3;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.util.Icon;

/**
 * @author adm_rieder
 * @created 07.11.2012
 */
public class LinkToD3ToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		String termName = null;
		Section<IncrementalTermDefinition> defSection = Sections.successor(
				section, IncrementalTermDefinition.class);

		if (defSection != null) {
			termName = defSection.get().getTermName(defSection);
		}

		String jsAction = "window.location.href = " +
				"'GraphVisualisierung.jsp?concept=' + encodeURIComponent('" +
				termName + "')";
		return new Tool[] { new DefaultTool(Icon.STATISTICS,
				"Visualisierung",
				"Visualisierung mithilfe von D3", jsAction) };
	}

}
