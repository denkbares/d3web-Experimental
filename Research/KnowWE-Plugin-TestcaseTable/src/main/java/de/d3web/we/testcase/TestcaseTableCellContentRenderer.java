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

import java.util.List;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableCellContentRenderer;
import de.d3web.we.kdom.table.TableUtils;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * 
 * @author Florian Ziegler / Sebastian Furth
 * @created 10.08.2010
 */
public class TestcaseTableCellContentRenderer extends TableCellContentRenderer {

	/**
	 * Wraps the content of the cell (sectionText) with the HTML-Code needed for
	 * the table
	 */
	@Override
	protected String wrappContent(String sectionText, Section<TableCellContent> sec, KnowWEUserContext user) {

		int col = TableCellContent.getCol(sec);
		int row = TableCellContent.getRow(sec);

		// get execution status
		Session session = D3webUtils.getSession(sec.getTitle(), user, sec.getWeb());
		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(sec.getWeb());
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		Object o = sessionInfoStore.get(sec.getTitle());

		Section<TestcaseTableLine> parentLine = Sections.findAncestorOfExactType(sec,
				TestcaseTableLine.class);

		String status = getStatus(parentLine, o);


		// Check if there is a valid TimeStamp
		if (col == 0 && row > 0) {

			Section<TimeStampType> timestamp = Sections.findSuccessor(sec, TimeStampType.class);
			boolean sort = TableUtils.sortTest(sec);
			boolean validTimeStamp = false;

			if (timestamp != null && TimeStampType.isValid(timestamp.getOriginalText())) {
				validTimeStamp = true;
			}

			StringBuilder html = new StringBuilder();

			if (sort) {
				html.append("<th class=\"sort" + status + "\">");
			}
			else if (validTimeStamp) {
				html.append("<td class=\"testcaseLineHeader "
						+ status
						+ "\"><div class=\"startTestcaseIncluding\" title=\"run Testcases until and including this\" onclick=\"return Testcase.runTestcase(this, true)\" id=\""
						+ sec.getID()
						+ "\"></div><div class=\"startTestcase\" title=\"run Testcase\" onclick=\"return Testcase.runTestcase(this, false)\" id=\""
						+ sec.getID() + "\"></div>");

			}
			else {
				html.append("<td class=\"invalidTimeStamp testcaseLineHeader\"></div>");
			}

			generateContent(sectionText, sec, user, sec.getID(), html);

			if (sort) {
				html.append("</th>");
			}

			else {
				html.append("</td>");
				return KnowWEUtils.maskHTML(html.toString());
			}
		}
		
		if (status != null && status != "") {
			StringBuilder html = new StringBuilder();
			html.append("<td class=\"" + status + "\">");
			generateContent(sectionText, sec, user, sec.getID(), html);
			html.append("</td>");
			return KnowWEUtils.maskHTML(html.toString());

		}


		// No TimeStamp Cell -> Normal Rendering!
		return super.wrappContent(sectionText, sec, user);

	}

	/**
	 * Returns the status as String which is then inserted as css class
	 */
	@SuppressWarnings("unchecked")
	private String getStatus(Section<TestcaseTableLine> parentLine, Object sessionInfoStoreForArticle) {
		if (parentLine == null || sessionInfoStoreForArticle == null) {
			return null;
		}
		Section<TimeStampType> timestamp = Sections.findSuccessor(parentLine, TimeStampType.class);
		long time = TimeStampType.getTimeInMillis(timestamp);
		long currentMax = -1;

		if (sessionInfoStoreForArticle instanceof List) {
			if (((List) sessionInfoStoreForArticle).contains(parentLine)) {
				return "testcaseExecuted";
			}

			List list = (List) sessionInfoStoreForArticle;
			Section parentTable = Sections.findAncestorOfType(parentLine, TestcaseTable.class);

			for (Object item : list) {
				if (item instanceof Section) {
					Section s = (Section) item;
					if (s.get() instanceof TestcaseTableLine) {
						Section p = Sections.findAncestorOfType(s, TestcaseTable.class);
						if (parentTable.equals(p)
								&& TimeStampType.getTimeInMillis((Section<TimeStampType>) item) > currentMax) {
							currentMax = TimeStampType.getTimeInMillis((Section<TimeStampType>) item);
						}
					}
				}
			}
		}

		if (time < currentMax) {
			return "testcaseUnavailable";
		}
		else {
			return "";
		}
	}

}
