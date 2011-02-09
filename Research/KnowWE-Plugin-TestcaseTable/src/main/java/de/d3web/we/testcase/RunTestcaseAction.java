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
import java.util.Date;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.manage.KnowledgeBaseManagement;
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
import de.d3web.we.action.ActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Florian Ziegler
 * @created 31.07.2010
 */
public class RunTestcaseAction extends AbstractAction {

	@Override
	public void execute(ActionContext context) throws IOException {
		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String web = map.getWeb();
		String execLine = map.get("execLine");

		Section<CellContent> cell = (Section<CellContent>) KnowWEEnvironment.getInstance().getArticleManager(
				web).findNode(execLine);

		Section<TestcaseTableLine> line = cell.findAncestorOfExactType(TestcaseTableLine.class);
		Section<TestcaseTableType> tableDMType = line.findAncestorOfExactType(TestcaseTableType.class);
		String master = TestcaseTableType.getMaster(tableDMType, map.getTopic());
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(web, master);

		RatedTestCase testcase = (RatedTestCase) KnowWEUtils.getStoredObject(article,
				line,
				TestcaseTableLine.TESTCASE_KEY);

		String user = context.getWikiContext().getUserName();
		Session session = D3webUtils.getSession(master, user, web);

		if (session == null) return;

		KnowledgeBaseManagement kbm =
				D3webModule.getKnowledgeRepresentationHandler(web).getKBM(master);

		executeTestCase(testcase, session, kbm);


	}

	/**
	 * 
	 * @created 22.01.2011
	 * @param testcase
	 * @param session
	 * @param kbm
	 */
	private void executeTestCase(RatedTestCase testcase, Session session, KnowledgeBaseManagement kbm) {
		Blackboard blackboard = session.getBlackboard();
		long time = getPropagationTime(session, kbm, testcase.getTimeStamp().getTime());

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
	private long getPropagationTime(Session session, KnowledgeBaseManagement kbm, long offSet) {


		Question question = kbm.findQuestion("start");
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

	// /**
	// * @param testcaseMap
	// * @param web
	// * @param user
	// * @param kbm
	// * @param blackboard
	// * @param time
	// */
	// private void setValues(Map<String, String> testcaseMap, String web,
	// String user, KnowledgeBaseManagement kbm, Session session) {
	//
	// Blackboard blackboard = session.getBlackboard();
	//
	// for (String questionName : testcaseMap.keySet()) {
	//
	// String valueString = testcaseMap.get(questionName);
	//
	// // Necessary for FindingSetEvent
	// Question question = kbm.findQuestion(questionName);
	// // TODO solutions
	// if (question == null) {
	// System.out.println("Could not find Question '" + questionName + "'.");
	// continue;
	// }
	//
	// // do not change value
	// if
	// (valueString.equals(GetNewQuickEditAnswersAction.UNCHANGED_VALUE_STRING))
	// {
	// continue;
	// }
	//
	// Value value;
	//
	// if
	// (valueString.equals(GetNewQuickEditAnswersAction.UNKNOWN_VALUE_STRING)) {
	// value = Unknown.getInstance();
	// }
	// else {
	// value = kbm.findValue(question, valueString);
	// }
	//
	// if (value == null) {
	// System.out.println("Could not find value '" + valueString +
	// "' on Question '"
	// + questionName + "'.");
	// continue;
	// }
	//
	// if (question instanceof QuestionMC &&
	// !value.equals(Unknown.getInstance())) {
	// Fact mcFact = blackboard.getValueFact(question);
	// if (mcFact != null && !mcFact.getValue().equals(Unknown.getInstance())) {
	// MultipleChoiceValue mcv = ((MultipleChoiceValue) mcFact.getValue());
	// Collection<ChoiceValue> thisMcv = (Collection<ChoiceValue>)
	// ((MultipleChoiceValue) value).getValue();
	// for (ChoiceValue cv : (Collection<ChoiceValue>) mcv.getValue()) {
	// if (!thisMcv.contains(cv)) {
	// thisMcv.add(cv);
	// }
	// }
	// }
	// }
	//
	// blackboard.addValueFact(FactFactory.createFact(session, question, value,
	// PSMethodUserSelected.getInstance(), PSMethodUserSelected.getInstance()));
	//
	// // TODO
	// EventManager.getInstance().fireEvent(
	// new FindingSetEvent(question, value, "TODO namespace", web, user));
	//
	// }
	// }

}
