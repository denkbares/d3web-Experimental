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
package de.knowwe.diaflux.coverage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.CoverageSessionObject;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.we.testcaseexecutor.TestCaseExecutorType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
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
		String master = DiaFluxCoverageType.getMaster(coverageSec);

		Pattern pattern = Pattern.compile(tests, Pattern.CASE_INSENSITIVE);

		Map<String, String> articles = Environment.getInstance().getWikiConnector().getAllArticles(
				context.getWeb());

		TestCaseAnalysis analysis = new TestCaseAnalysis();
		CoverageTestListener listener = new CoverageTestListener();
		analysis.addTestListener(listener);

		for (String title : articles.keySet()) {
			Article article = Environment.getInstance().getArticle(context.getWeb(),
					title);
			if (!pattern.matcher(title).matches()) continue;

			List<Section<TestCaseExecutorType>> testcases = Sections.findSuccessorsOfType(
					article.getSection(), TestCaseExecutorType.class);

			for (Section<TestCaseExecutorType> section : testcases) {

				TestCaseExecutorType.execute(section, analysis);

			}

		}

		KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), master);
		Map<SequentialTestCase, CoverageSessionObject> results = listener.getResults();
		CoverageResult result = DefaultCoverageResult.calculateResult(results.values(), kb);
		coverageSec.getSectionStore().storeObject(DiaFluxCoverageType.COVERAGE_RESULT, result);

	}

}
