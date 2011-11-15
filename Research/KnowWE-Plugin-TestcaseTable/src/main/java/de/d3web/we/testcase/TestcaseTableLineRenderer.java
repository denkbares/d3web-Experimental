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

import java.util.Collection;
import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMError;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.table.TableLine;
import de.knowwe.kdom.table.TableLineRenderer;
import de.knowwe.kdom.table.TableUtils;

/**
 * 
 * @author Florian Ziegler
 * @created 11.03.2011
 */
public class TestcaseTableLineRenderer extends TableLineRenderer {

	public static final String TESTCASEEXECUTED = "tcExecuted";
	public static final String TESTCASESKIPPED = "tcSkipped";
	public static final String TESTCASELINE = "tcLine";
	public static final String TESTCASEERROR = "tcError";

	@Override
	protected String getClasses(KnowWEArticle article, Section<TableLine> sec, UserContext user) {

		Collection<KDOMError> errorMessages = KnowWEUtils.getMessagesFromSubtree(article, sec,
				KDOMError.class);

		if (errorMessages != null && !errorMessages.isEmpty()) {
			return TESTCASELINE + " " + TESTCASEERROR;
		}

		// get execution status
		Section<TestcaseTableType> table = Sections.findAncestorOfExactType(sec,
				TestcaseTableType.class);
		String master = TestcaseTableType.getMaster(table, user.getTitle());
		Session session = D3webUtils.getSession(master, user, sec.getWeb());

		if (session == null) {
			return TESTCASELINE;
		}

		Section<TestcaseTableType> parentTable = Sections.findAncestorOfType(sec,
				TestcaseTableType.class);

		List<Section<TestcaseTableLine>> executedLines = TestcaseTable.getExecutedLinesOfTable(
				parentTable, user, session);

		if (executedLines.isEmpty()) {
			return TESTCASELINE;
		}
		else if (executedLines.contains(sec)) {
			return TESTCASELINE + " " + TESTCASEEXECUTED;
		}
		else {
			Section<TestcaseTableLine> lastLine = executedLines.get(executedLines.size() - 1);

			if (TableUtils.getRowOfLine(sec) < TableUtils.getRowOfLine(lastLine)) {
				return TESTCASELINE + " " + TESTCASESKIPPED;
			}
			else {
				return TESTCASELINE;

			}
		}

	}

}