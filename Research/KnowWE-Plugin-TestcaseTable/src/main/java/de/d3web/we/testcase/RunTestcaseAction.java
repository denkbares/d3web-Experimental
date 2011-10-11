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
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import de.d3web.we.basic.D3webModule;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * @author Florian Ziegler
 * @created 31.07.2010
 */
public class RunTestcaseAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String web = context.getWeb();
		String execLine = context.getParameter("execLine");
		boolean multiLines = Boolean.valueOf(context.getParameter("multiLines"));

		Section<TestcaseTableLine> line = (Section<TestcaseTableLine>) Sections.getSection(execLine);

		Section<TestcaseTableType> tableDMType = Sections.findAncestorOfExactType(line,
				TestcaseTableType.class);
		String master = TestcaseTableType.getMaster(tableDMType, context.getTopic());
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web, master);

		Session session = D3webUtils.getSession(master, context, web);

		if (session == null) return;

		List<Section<TestcaseTableLine>> alreadyExecuted = TestcaseTable.getExecutedLinesOfTable(
				tableDMType, context, session);

		KnowledgeBase kb =
				D3webModule.getKnowledgeRepresentationHandler(web).getKB(master);

		if (!multiLines) {
			executeTableLine(article, session, alreadyExecuted, kb, line);

		}
		else {
			List<Section<TestcaseTableLine>> toBeExecutedLines = findTestcaseIncluding(line,
					alreadyExecuted);

			for (Section<TestcaseTableLine> testLine : toBeExecutedLines) {
				executeTableLine(article, session, alreadyExecuted, kb, testLine);
			}
		}

	}

	/**
	 * Executes a single table line;
	 * 
	 * @created 17.03.2011
	 * @param article
	 * @param session
	 * @param alreadyExecuted
	 * @param kb
	 * @param line
	 */
	public static void executeTableLine(KnowWEArticle article, Session session, List<Section<TestcaseTableLine>> alreadyExecuted, KnowledgeBase kb, Section<TestcaseTableLine> line) {
		alreadyExecuted.add(line);

		RatedTestCase testcase = (RatedTestCase) KnowWEUtils.getStoredObject(article,
				line, TestcaseTableLine.TESTCASE_KEY);
		executeTestCase(testcase, session, kb);
	}

	/**
	 * 
	 * @created 22.01.2011
	 * @param testcase
	 * @param session
	 * @param kbm
	 */
	private static void executeTestCase(RatedTestCase testcase, Session session, KnowledgeBase kb) {
		Blackboard blackboard = session.getBlackboard();
		long time = getPropagationTime(session, kb, testcase.getTimeStamp().getTime());

		try {

			session.getPropagationManager().openPropagation(time);
			for (Finding f : testcase.getFindings()) {
				blackboard.addValueFact(new DefaultFact(f.getQuestion(), f.getValue(),
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
	private static long getPropagationTime(Session session, KnowledgeBase kb, long offSet) {

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

	private List<Section<TestcaseTableLine>> findTestcaseIncluding(Section<TestcaseTableLine> line, List<Section<TestcaseTableLine>> alreadyExecuted) {

		Section<TestcaseTable> tableSection = Sections.findAncestorOfExactType(line,
				TestcaseTable.class);

		List<Section<TestcaseTableLine>> allLines = Sections.findChildrenOfType(tableSection,
				TestcaseTableLine.class);

		int firstLine;
		if (alreadyExecuted.isEmpty()) {
			firstLine = 0;
		}
		else {
			Section<TestcaseTableLine> lastExLineSec = alreadyExecuted.get(alreadyExecuted.size() - 1);
			firstLine = allLines.indexOf(lastExLineSec) + 1;
		}

		int lastLine = allLines.indexOf(line) + 1;

		// can happen, when executing the table in more than 1 tab
		if (lastLine < firstLine) return Collections.emptyList();

		return allLines.subList(firstLine, lastLine);

	}

}
