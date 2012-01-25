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
package de.knowwe.sessiondebugger;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.TestCase;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Action that gets called when cases should be executed with the
 * SessionDebugger
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 19.01.2012
 */
public class ExecuteCasesAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String sectionid = context.getParameter("id");
		SessionDebugStatus status = (SessionDebugStatus) context.getSession().getAttribute(
				sectionid);
		Date endDate;
		try {
			endDate = SessionDebuggerRenderer.dateFormat.parse(context.getParameter("date"));
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
		@SuppressWarnings("unchecked")
		Section<SessionDebuggerType> section = (Section<SessionDebuggerType>) Sections.getSection(sectionid);
		String master = DefaultMarkupType.getAnnotation(section,
				SessionDebuggerType.ANNOTATION_MASTER);
		String web = context.getWeb();
		String sessionId = KnowWEEnvironment.generateDefaultID(master);
		SessionBroker broker = D3webModule.getBroker(context.getUserName(), web);
		Session session = broker.getSession(sessionId);
		// reset session
		TestCase testCase = status.getTestCase();
		if (session != status.getSession() || status.getLastExecuted() == null) {
			session = SessionFactory.createSession(session.getKnowledgeBase(),
					testCase.getStartDate());
			broker.removeSession(sessionId);
			broker.addSession(sessionId, session);
			status.setSession(session);
			runTo(session, testCase, endDate, status);
			status.setLastExecuted(endDate);
		}
		else {
			runTo(session, testCase, status.getLastExecuted(), endDate, status);
			status.setLastExecuted(endDate);
		}
	}

	private static void runTo(Session session, TestCase testCase, Date endDate, SessionDebugStatus status) {
		for (Date date : testCase.chronology()) {
			if (date.before(endDate) || date.equals(endDate)) {
				TestCaseUtils.applyFindings(session, testCase.getFindings(date));
				for (Check c : testCase.getChecks(date)) {
					status.addCheckResult(date, c, c.check(session));
				}
			}
		}
	}

	private static void runTo(Session session, TestCase testCase, Date startDate, Date endDate, SessionDebugStatus status) {
		for (Date date : testCase.chronology()) {
			if (date.after(startDate) && (date.before(endDate) || date.equals(endDate))) {
				TestCaseUtils.applyFindings(session, testCase.getFindings(date));
				for (Check c : testCase.getChecks(date)) {
					status.addCheckResult(date, c, c.check(session));
				}
			}
		}
	}

}
