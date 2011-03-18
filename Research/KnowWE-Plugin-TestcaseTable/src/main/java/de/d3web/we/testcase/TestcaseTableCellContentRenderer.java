/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableCellContentRenderer;
import de.d3web.we.kdom.table.TableUtils;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 10.08.2010
 */
public class TestcaseTableCellContentRenderer extends TableCellContentRenderer {

	public static final String INVALIDTIMESTAMP = "invalidTimeStamp";


	@Override
	public void render(KnowWEArticle article, Section<TableCellContent> sec, UserContext user, StringBuilder string) {

		Section<TestcaseTableLine> line = Sections.findAncestorOfExactType(sec,
				TestcaseTableLine.class);

		boolean testcaseAvailable = KnowWEUtils.getStoredObject(article, line,
				TestcaseTable.TESTCASE_KEY) != null;

		int column = TableUtils.getColumn(sec);

		StringBuilder html = new StringBuilder();

		if (!testcaseAvailable) {
			html.append("<td class=\"" + INVALIDTIMESTAMP + "\">");
		}
		else if (column != 0) {
			html.append("<td>");

		}
		else {

			html.append("<td>");
			html.append("<div class='startTestcaseIncluding' title='run Testcases until and including this' onclick='Testcase.runTestcase(this, true)'>"
						+ "</div>"
						+ "<div class='startTestcase' title='run Testcase' onclick='Testcase.runTestcase(this, false)'></div>");

		}

		html.append(sec.getText());

		html.append("</td>");
		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
