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
package de.knowwe.wisskont.refactoring;

import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class RelationMarkupRefactoringToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		return new Tool[] { new DefaultTool("KnowWEExtension/images/dt_icon_realisation2.gif",
				"Relationen umbauen",
				"Relationen umbauen", getJSScript(userContext)) };
	}

	/**
	 * 
	 * @created 05.12.2012
	 * @return
	 */
	private String getJSScript(UserContext userContext) {
		String jsAction = "window.location='action/RefactoringAction" +
				"?" + Attributes.TOPIC + "=" + userContext.getTitle() +
				"&" + Attributes.USER + "=" + userContext.getUserName() +
				"&" + Attributes.WEB + "=" + Environment.DEFAULT_WEB + "'";
		;
		return jsAction;
	}

}
