package utils;

/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.empiricaltesting.TestSuite;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.testsuite.kdom.TestSuiteType;
import de.d3web.we.utils.KnowWEUtils;

/**
 *
 * @author Sebastian Furth
 * @created 27/10/2010
 */
public class TestSuiteTestUtil {

	private static TestSuiteTestUtil instance = new TestSuiteTestUtil();
	private TestSuite createdTS;
	private KnowledgeBase kb;

	private TestSuiteTestUtil() {
		createKB();
		createTestSuite();
	}

	public static TestSuiteTestUtil getInstance() {
		return instance;
	}

	public TestSuite findTestSuite(KnowWEArticle article) {
		Section<TestSuiteType> s = article.getSection().findSuccessor(TestSuiteType.class);
		return (TestSuite) KnowWEUtils.getStoredObject("default_web",
				article.getTitle(),
				s.getID(), TestSuiteType.TESTSUITEKEY);
	}

	public KnowledgeBase getCreatedKB() {
		return kb;
	}

	/**
	 * Returns the TestSuite which was created manually.
	 *
	 * @return TestSuite
	 */
	public TestSuite getCreatedTS() {
		return createdTS;
	}

	private void createKB() {
		kb = new KnowledgeBase();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);

		// Root Solution
		Solution p0 = new Solution("P000");
		p0.setName("P000");
		kb.add(p0);
		kb.setRootSolution(p0);

		// Solution
		Solution p = new Solution("P1");
		p.setName("Other problem");
		kb.getRootSolution().addChild(p);
		kb.add(p);

		// Root Questionnaire
		QContainer qc0 = new QContainer("Q000");
		qc0.setName("Q000");
		kb.add(qc0);
		kb.setRootQASet(qc0);

		// Questionnaire
		QContainer qc = new QContainer("QC1");
		qc.setName("Observations");
		kb.getRootQASet().addChild(qc);
		kb.add(qc);

		// Add question:
		// - Driving [mc]
		// -- insufficient power on partial load
		// -- insufficient power on full load
		// -- unsteady idle speed
		// -- everything is fine
		kbm.createQuestionMC("Driving", qc, new String[] {
						"insufficient power on partial load",
						"insufficient power on full load",
						"unsteady idle speed",
						"everything is fine" });

	}

	/**
	 * Creats a TestSuite similar to the one which is created in the
	 * KnowWEArticle
	 */
	private void createTestSuite() {

		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(kb);

		// Create Finding
		Question q = kbm.findQuestion("Driving");
		Choice a = kbm.findChoice((QuestionChoice) q, "everything is fine");
		Finding f = new Finding(q, new MultipleChoiceValue(new ChoiceID(a)));

		// Create RatedSolution
		Solution d = kbm.findSolution("Other problem");
		StateRating sr = new StateRating(new Rating(State.ESTABLISHED));
		RatedSolution rs = new RatedSolution(d, sr);

		// Add Finding and RatedSolution to RatedTestCase
		RatedTestCase rtc = new RatedTestCase();
		rtc.add(f);
		rtc.addExpected(rs);
		rtc.setName("STC1_RTC1");

		// Add RatedTestCase to SequentialTestCase
		SequentialTestCase stc = new SequentialTestCase();
		stc.add(rtc);
		stc.setName("STC1");

		// Add SequentialTestCase to the repository
		List<SequentialTestCase> repository = new ArrayList<SequentialTestCase>();
		repository.add(stc);

		// Create testSuite
		TestSuite t = new TestSuite();
		t.setKb(kb);
		t.setRepository(repository);
		createdTS = t;
	}

}
