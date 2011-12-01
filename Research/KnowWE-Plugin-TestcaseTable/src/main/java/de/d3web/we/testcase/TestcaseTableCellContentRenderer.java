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

import java.util.Collection;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.table.TableCellContent;
import de.knowwe.kdom.table.TableCellContentRenderer;
import de.knowwe.kdom.table.TableUtils;

/**
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 10.08.2010
 */
public class TestcaseTableCellContentRenderer extends TableCellContentRenderer {

	@Override
	public void render(KnowWEArticle article, Section<TableCellContent> sec, UserContext user, StringBuilder string) {

		Collection<Message> messages = Messages.getErrors(Messages.getMessagesFromSubtree(article,
				sec));

		int column = TableUtils.getColumn(sec);

		StringBuilder html = new StringBuilder();
		if (!sec.hasQuickEditModeSet(user.getUserName())) {
			if (!messages.isEmpty()) {
				html.append("<td class='error'>");
			}
			else if (column != 0) {
				html.append("<td>");
			}
			else {

				Section<TestcaseTableType> table = Sections.findAncestorOfExactType(sec,
						TestcaseTableType.class);
				String skipAnnotation = DefaultMarkupType.getAnnotation(table,
						TestcaseTableType.ANNOTATION_SHOW_SKIP_BUTTON);
				html.append("<td>");
				html.append("<div class='startTestcaseIncluding' title='run testcases until this' onclick='Testcase.runTestcase(this, true)'></div>");
				if ("true".equalsIgnoreCase(skipAnnotation)) {
					html.append("<div class='startTestcase' title='run testcase' onclick='Testcase.runTestcase(this, false)'></div>");
				}

			}
			html.append(sec.getText());
		}
		else {
			if (!messages.isEmpty()) {
				html.append("<td class='error'>");
				html.append(sec.getText());
			}
			else if (column == 1) {
				html.append("<td>");
				html.append("<input type='text' name='" + sec.getOriginalText() + "' id='"
						+ sec.getID()
						+ "' value='" + TableUtils.quote(sec.getOriginalText())
						+ "' class='table-edit-node'/>");
			}
			else if (column != 0) {
				html.append("<td>");
				generateContent(sec.getOriginalText(), sec, user, sec.getID(), html);

			}
			else {
				html.append("<td>");
				html.append("<div class='startTestcaseIncluding' title='run Testcases until and including this' onclick='Testcase.runTestcase(this, true)'>"
							+ "</div>"
							+ "<div class='startTestcase' title='run Testcase' onclick='Testcase.runTestcase(this, false)'></div>");

			}
		}

		html.append("</td>");
		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
