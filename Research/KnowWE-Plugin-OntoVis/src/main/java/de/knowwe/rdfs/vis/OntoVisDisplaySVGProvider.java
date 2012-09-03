/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.rdfs.vis;


import de.knowwe.core.Attributes;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * @author Johanna Latt
 * @created 20.06.2012
 */
public class OntoVisDisplaySVGProvider implements ToolProvider {

	public static final String PARAM_FILENAME = "filename";

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		// and provide both download and refresh as tools
		Tool download = getDownloadTool(section, userContext);
		return new Tool[] { download };
	}

	protected Tool getDownloadTool(Section<?> section, UserContext userContext) {
		// tool to provide download capability
		String jsAction = "window.location='action/OntoVisDisplaySVG" +
				"?" + Attributes.TOPIC + "=" + section.getTitle() +
				"&" + Attributes.WEB + "=" + section.getWeb() + "'";
		return new DefaultTool(
				"KnowWEExtension/d3web/icon/comment16.png",
				"Display the .svg",
				"Display the graph",
				jsAction);
	}

}
