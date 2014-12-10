/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.rdfs.inspect;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.tools.ToolUtils;
import de.knowwe.util.Icon;

/**
 * The StatementImportanceToolProvider allows the user to determine the
 * importance of an RDFS statement. For more information see the
 * {@link InferenceDiffTagHandler}.
 *
 * @author Stefan Mark
 * @created 31.05.2011
 */
public class InferenceDiffToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		Section<KnowledgeUnit> knowledgeUnit =
				Sections.ancestor(section, KnowledgeUnit.class);
		return knowledgeUnit != null;
	}

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		Section<KnowledgeUnit> knowledgeUnit =
				Sections.ancestor(section, KnowledgeUnit.class);
		if (knowledgeUnit == null) return ToolUtils.emptyToolArray();
		return new Tool[] { getStatementImportancePageTool(knowledgeUnit, userContext) };
	}

	/**
	 * Returns the Tool for the StatementImportancePageTool.
	 */
	protected Tool getStatementImportancePageTool(Section<?> section, UserContext userContext) {

		String jsAction = "window.location.href = "
				+ "'Wiki.jsp?page=InferenceDiff&amp;section=' + encodeURIComponent('"
				+ section.getID()
				+ "') + '&amp;topic='+ encodeURIComponent('"
				+ section.getTitle()
				+ "')";
		return new DefaultTool(
				Icon.MINUS,
				"Show inference diff for statement",
				"Removes the selected statement from the triple store and calculates shows a diff between the data before and after the deletion to show the importance of the statement..",
				jsAction,
				Tool.CATEGORY_INFO);
	}
}
