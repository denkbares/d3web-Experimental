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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Florian Ziegler
 * @created 31.07.2010
 */
public class RunTestcaseAction extends AbstractAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(UserActionContext context) throws IOException {
		String web = context.getWeb();
		String execLine = context.getParameter("execLine");
		boolean multiLines = Boolean.valueOf(context.getParameter("multiLines"));

		Section<CellContent> cell = (Section<CellContent>) KnowWEEnvironment.getInstance().getArticleManager(
				web).findNode(execLine);

		Section<TestcaseTableLine> line = Sections.findAncestorOfExactType(cell,
				TestcaseTableLine.class);
		Section<TestcaseTableType> tableDMType = Sections.findAncestorOfExactType(line,
				TestcaseTableType.class);
		String master = TestcaseTableType.getMaster(tableDMType, context.getTopic());
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web, master);

		List<Section<TestcaseTableLine>> toBeExecutedLines = new LinkedList<Section<TestcaseTableLine>>();
		toBeExecutedLines.add(line);

		if (multiLines) {
			findTestcaseIncluding(line, toBeExecutedLines);
		}

		// saving/recalling execution status
		String user = context.getUserName();
		Session session = D3webUtils.getSession(master, user, web);
		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(web);
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		Object o = sessionInfoStore.get(master);


		if (session == null) return;

		KnowledgeBase kb =
				D3webModule.getKnowledgeRepresentationHandler(web).getKB(master);

		for (Section<TestcaseTableLine> tctLine : toBeExecutedLines) {

			if (o == null) {
				List<Section<TestcaseTableLine>> list = new ArrayList<Section<TestcaseTableLine>>();
				list.add(tctLine);
				sessionInfoStore.put(master, list);
			}
			else if (o instanceof List) {
				if (((List) o).contains(tctLine)) {
					continue;
				}
				else {
					((List) o).add(tctLine);
				}
			}

			RatedTestCase testcase = (RatedTestCase) KnowWEUtils.getStoredObject(article,
					tctLine,
					TestcaseTableLine.TESTCASE_KEY);
			executeTestCase(testcase, session, kb);
		}

	}

	/**
	 *
	 * @created 22.01.2011
	 * @param testcase
	 * @param session
	 * @param kbm
	 */
	private void executeTestCase(RatedTestCase testcase, Session session, KnowledgeBase kb) {
		Blackboard blackboard = session.getBlackboard();
		long time = getPropagationTime(session, kb, testcase.getTimeStamp().getTime());

		try {

			session.getPropagationManager().openPropagation(time);
			for (Finding f : testcase.getFindings()) {
				blackboard.addValueFact(new DefaultFact(f.getQuestion(), f.getValue(), time,
							PSMethodUserSelected.getInstance(),
							PSMethodUserSelected.getInstance()));
			}

		}
		finally {
			session.getPropagationManager().commitPropagation();
		}

	}

	/**
	 *
	 * @created 20.01.2011
	 * @param session
	 * @param kbm
	 * @param offSet
	 * @return
	 */
	private long getPropagationTime(Session session, KnowledgeBase kb, long offSet) {

		Question question = kb.getManager().searchQuestion("start");
		if (question == null) { // no timeDB present
			return offSet;
		}
		else {
			// start is no date question. Maybe timeDB is not present
			if (!(question instanceof QuestionDate)) {
				return offSet;
			}
			Value value = session.getBlackboard().getValue(question);

			if (value == UndefinedValue.getInstance()) return 0;

			DateValue dateValue = (DateValue) value;

			Date date = dateValue.getDate();

			return date.getTime() + offSet;
		}

	}

	private void findTestcaseIncluding(Section<TestcaseTableLine> line, List<Section<TestcaseTableLine>> list) {
		long originalTimeStampValue = TimeStampType.getTimeInMillis(Sections.findSuccessor(
				line.getChildren().get(0),
				TimeStampType.class));

		Section<TestcaseTable> table = (Section<TestcaseTable>) line.getFather();
		List<Section<? extends Type>> lines = table.getChildren();

		for (Section<? extends Type> l : lines) {
			if (!(l.get() instanceof HeaderLine)) {
				Section<TestcaseTableLine> currentLine = (Section<TestcaseTableLine>) l;
				long currentTimeStampValue = TimeStampType.getTimeInMillis(Sections.findSuccessor(
						currentLine.getChildren().get(
								0), TimeStampType.class));
				if (currentTimeStampValue < originalTimeStampValue) {
					list.add(currentLine);
				}

			}
		}
	}

}
