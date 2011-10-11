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

import java.io.IOException;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * Resets the session. Optionally, reruns the last executed tests of the table
 * 
 * @author Florian Ziegler / Reinhard Hatko
 * @created 14.02.2011
 */
public class TestcaseTableResetAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		boolean rerun = Boolean.valueOf(context.getParameter("rerun"));

		if (rerun) {
			String table = context.getParameter("table");
			String web = context.getWeb();

			Section<TestcaseTableType> tableDMType = (Section<TestcaseTableType>) Sections.getSection(table);

			String master = TestcaseTableType.getMaster(tableDMType, context.getTopic());
			// get tests before clearing the session
			List<Section<TestcaseTableLine>> executedLines = getExecutedTestsFromSession(context,
					web, tableDMType, master);
			// clear session...
			clearSession(context);

			// ... get new session and rerun tests
			Session newSession = D3webUtils.getSession(master, context, web);

			KnowledgeBase kb =
					D3webModule.getKnowledgeRepresentationHandler(web).getKB(master);

			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web, master);
			List<Section<TestcaseTableLine>> alreadyExecuted = TestcaseTable.getExecutedLinesOfTable(
					tableDMType, context, newSession);

			for (Section<TestcaseTableLine> line : executedLines) {

				RunTestcaseAction.executeTableLine(article, newSession, alreadyExecuted, kb, line);
			}

		}
		else {
			clearSession(context);
		}

	}

	/**
	 * Returns the test from the session
	 * 
	 * @param context
	 * @param web
	 * @param tableDMType
	 * @param master
	 * @return
	 */
	private List<Section<TestcaseTableLine>> getExecutedTestsFromSession(UserActionContext context, String web, Section<TestcaseTableType> tableDMType, String master) {
		Session oldSession = D3webUtils.getSession(master, context, web);
		return TestcaseTable.getExecutedLinesOfTable(
				tableDMType, context, oldSession);
	}

	private void clearSession(UserActionContext context) {
		SessionBroker broker = D3webModule.getBroker(context.getParameters());
		broker.clear();
	}

}
