/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.tools.DefaultTool;
import de.d3web.we.tools.Tool;
import de.d3web.we.tools.ToolProvider;
import de.d3web.we.user.UserContext;

/**
 * 
 * @author Florian Ziegler
 * @created 14.02.2011
 */
public class TestcaseTableResetProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {
		Tool reset = getResetTool(article, section, userContext);
		Tool resetAndRerun = getResetAndRerunTool(article, section, userContext);
		return new Tool[] {
				reset, resetAndRerun };
	}

	private Tool getResetAndRerunTool(KnowWEArticle article, Section<?> section, UserContext userContext) {
		String jsAction = createResetAction(section, userContext, true);
		return new DefaultTool(
				"KnowWEExtension/images/testcasetable/resetRerun.png",
				"Clear session and rerun",
				"Clears the session and reruns the last tests",
				jsAction);
	}

	private Tool getResetTool(KnowWEArticle article, Section<?> section, UserContext userContext) {
		String jsAction = createResetAction(section, userContext, false);
		return new DefaultTool(
				"KnowWEExtension/images/progress_stop.gif",
				"Clear session",
				"Clears the session",
				jsAction);
	}


	private static String createResetAction(Section<?> section, UserContext userContext, boolean rerun) {
		return "Testcase.resetTestcase('" + section.getID() + "', " + Boolean.toString(rerun) + ");";
	}

}
