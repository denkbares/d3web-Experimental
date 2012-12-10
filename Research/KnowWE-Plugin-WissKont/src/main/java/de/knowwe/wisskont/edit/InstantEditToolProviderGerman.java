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
package de.knowwe.wisskont.edit;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.instantedit.tools.InstantEditTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 28.11.2012
 */
public class InstantEditToolProviderGerman implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		return new Tool[] { getQuickEditPageTool(section, userContext) };
	}

	protected Tool getQuickEditPageTool(Section<?> section, UserContext userContext) {
		return new InstantEditTool(
					"KnowWEExtension/images/pencil.png",
					"Editieren",
					"Diesen Abschnitt editieren",
					section);
	}
}
