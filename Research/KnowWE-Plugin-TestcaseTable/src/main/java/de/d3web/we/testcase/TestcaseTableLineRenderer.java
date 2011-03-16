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

import java.util.List;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.kdom.table.TableLineRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;

/**
 * 
 * @author Florian Ziegler
 * @created 11.03.2011
 */
public class TestcaseTableLineRenderer extends TableLineRenderer {

	public static final String TESTCASEEXECUTED = "testcaseExecuted";
	public static final String TESTCASESKIPPED = "testcaseSkipped";

	@Override
	protected String getClasses(KnowWEArticle article, Section<TableLine> sec, UserContext user) {
		// get execution status
		Session session = D3webUtils.getSession(sec.getTitle(), user, sec.getWeb());
		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(sec.getWeb());
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		Object sessionInfoStoreForArticle = sessionInfoStore.get(sec.getTitle());

		if (sec == null || sessionInfoStoreForArticle == null) {
			return "";
		}
		Section<TimeStampType> timestamp = Sections.findSuccessor(sec, TimeStampType.class);

		if (sessionInfoStoreForArticle instanceof List<?>) {

			if (((List<TestcaseTableLine>) sessionInfoStoreForArticle).contains(sec)) {
				return TESTCASEEXECUTED;
			}

			List list = (List) sessionInfoStoreForArticle;
			Section parentTable = Sections.findAncestorOfType(sec, TestcaseTable.class);

			for (Object item : list) {
				if (item instanceof Section) {
					Section s = (Section) item;
					if (s.get() instanceof TestcaseTableLine) {
						Section p = Sections.findAncestorOfType(s, TestcaseTable.class);
					}
				}
			}
		}

		return "";
	}

}