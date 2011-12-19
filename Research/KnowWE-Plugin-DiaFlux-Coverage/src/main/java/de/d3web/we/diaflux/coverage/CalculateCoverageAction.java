/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.coverage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.CoverageSessionObject;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestListener;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.testcaseexecutor.TestCaseExecutorType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author Reinhard Hatko
 * @created 13.09.2011
 */
public class CalculateCoverageAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String coverageKdomid = context.getParameter("kdomid");

		Section<DiaFluxCoverageType> coverageSec = (Section<DiaFluxCoverageType>) Sections.getSection(
				coverageKdomid);

		String tests = DefaultMarkupType.getAnnotation(coverageSec,
				DiaFluxCoverageType.ANNOTATION_TEST).replace("*", ".*").replace("?", ".");
		String master = DiaFluxCoverageType.getMaster(coverageSec, context.getTitle());

		Pattern pattern = Pattern.compile(tests, Pattern.CASE_INSENSITIVE);

		Map<String, String> articles = KnowWEEnvironment.getInstance().getWikiConnector().getAllArticles(
				context.getWeb());

		final Map<SequentialTestCase, CoverageSessionObject> results = new HashMap<SequentialTestCase, CoverageSessionObject>();
		TestCaseAnalysis analysis = new TestCaseAnalysis();
		analysis.addTestListener(new TestListener() {

			@Override
			public void testcaseStarting(TestCase tc) {
			}

			@Override
			public void testcaseFinished(TestCase tc, TestCaseAnalysisReport report) {
			}

			@Override
			public void sequentialTestcaseStarting(SequentialTestCase stc, Session session) {
			}

			@Override
			public void ratedTestcaseStarting(RatedTestCase rtc) {
			}

			@Override
			public void ratedTestcaseFinished(RatedTestCase rtc, RTCDiff rtc_diff) {
			}

			@Override
			public void sequentialTestcaseFinished(SequentialTestCase stc, Session session, Diff diff) {
				CoverageSessionObject result = PSMDiaFluxCoverage.getCoverage(session);
				results.put(stc, result);
			}

		});

		for (String title : articles.keySet()) {
			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(context.getWeb(),
					title);
			if (!pattern.matcher(title).matches()) continue;

			List<Section<TestCaseExecutorType>> testcases = Sections.findSuccessorsOfType(
					article.getSection(), TestCaseExecutorType.class);

			for (Section<TestCaseExecutorType> section : testcases) {

				TestCaseExecutorType.execute(section, analysis);

			}

		}

		KnowledgeBase kb = D3webUtils.getKB(context.getWeb(), master);
		CoverageResult result = DefaultCoverageResult.calculateResult(results.values(), kb);
		coverageSec.getSectionStore().storeObject(DiaFluxCoverageType.COVERAGE_RESULT, result);

	}

}
