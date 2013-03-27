/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.diaflux.coverageCity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.DiaFluxCoverageTrace;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.TestCase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.d3webviz.diafluxCity.GLCityGenerator;
import de.knowwe.d3webviz.diafluxCity.metrics.Constant;
import de.knowwe.d3webviz.diafluxCity.metrics.Metrics;
import de.knowwe.d3webviz.diafluxCity.metrics.MetricsSet;
import de.knowwe.d3webviz.diafluxCity.metrics.OutgoingEdgesMetric;
import de.knowwe.diaflux.coverage.CoverageUtils;
import de.knowwe.diaflux.coverage.DiaFluxCoverageType;
import de.knowwe.diaflux.coverageCity.metrics.CoveredOutgoingEdgesMetric;
import de.knowwe.diaflux.coverageCity.metrics.CoveredPathsColorMetric;
import de.knowwe.diaflux.coverageCity.metrics.MaximumInSameFlowNodeCoverage;
import de.knowwe.diaflux.coverageCity.metrics.NodeCoverageMetric;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.testcases.TestCaseProvider;
import de.knowwe.testcases.TestCaseProviderStorage;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 20.03.2013
 */
public class CoverageCityAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String sectionID = context.getParameter(Attributes.SECTION_ID);

		Section<CoverageCityType> section = Sections.getSection(sectionID,
				CoverageCityType.class);

		if (section == null) {
			// TODO error handling
			return;
		}

		Iterator<Article> iterator = KnowWEUtils.getCompilingArticles(section).iterator();
		if (!iterator.hasNext()) return;

		Article article = iterator.next();
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(section.getWeb(), article.getTitle());

		CoverageResult coverage = calculateCoverage(section, kb);

		String city = GLCityGenerator.generateCity(createMetrics(coverage), kb).toString();

		context.setContentType("text/json");
		context.getWriter().write(city);

	}

	private static MetricsSet<Node> createMetrics(CoverageResult coverage) {
		MetricsSet<Node> metrics = new MetricsSet<Node>(Arrays.asList("Wait"), true);
		metrics.setHeightMetric(Metrics.multiply(Metrics.relate(new
				NodeCoverageMetric(coverage), new MaximumInSameFlowNodeCoverage(coverage)), 5));
		metrics.setHeightMetric(new CoveredOutgoingEdgesMetric(coverage));
		metrics.setLengthMetric(new OutgoingEdgesMetric());
		metrics.setWidthMetric(Metrics.relate(new NodeCoverageMetric(coverage), new Constant<Node>(
				15)));
		metrics.setColorMetric(new CoveredPathsColorMetric(coverage));

		return metrics;
	}

	public static CoverageResult calculateCoverage(Section<CoverageCityType> coverageSec, KnowledgeBase kb) {
		CoverageResult result;
		// result = (CoverageResult) coverageSec.getSectionStore().getObject(
		// DiaFluxCoverageType.COVERAGE_RESULT);
		//
		// if (result != null) return result;

		Environment env = Environment.getInstance();
		PackageManager packageManager = env.getPackageManager(coverageSec.getWeb());

		// Map<String, String> articles =
		// Environment.getInstance().getWikiConnector().getAllArticles(
		// context.getWeb());
		String[] packages = DefaultMarkupType.getPackages(coverageSec);
		List<String> articles = new LinkedList<String>();
		for (String packageName : packages) {
			articles.addAll(packageManager.getCompilingArticles(packageName));
		}

		List<DiaFluxCoverageTrace> results = new LinkedList<DiaFluxCoverageTrace>();
		for (String title : articles) {
			Article article = Environment.getInstance().getArticle(coverageSec.getWeb(),
					title);

			// List<Section<KnowledgeBaseType>> knowledgeBaseSections =
			// Sections.findSuccessorsOfType(
			// article.getRootSection(), KnowledgeBaseType.class);
			//
			// if (knowledgeBaseSections.size() != 1) {
			// continue;
			// }

			// Section<KnowledgeBaseType> kbSection =
			// knowledgeBaseSections.get(0);

			Set<Section<?>> sectionsCompiledByArticle = new HashSet<Section<?>>();
			// TODO use all packages of KB or only those of the defined packages
			// for (String packageName : compiledPackages) {
			for (String packageName : packages) {
				sectionsCompiledByArticle.addAll(packageManager.getSectionsOfPackage(packageName));
			}
			for (Section<?> potentioalTestCaseSection : sectionsCompiledByArticle) {
				TestCaseProviderStorage testCaseProviderStorage =
						(TestCaseProviderStorage) potentioalTestCaseSection.getSectionStore().getObject(
								article,
								TestCaseProviderStorage.KEY);
				if (testCaseProviderStorage != null) {
					for (TestCaseProvider testCaseProvider : testCaseProviderStorage.getTestCaseProviders()) {
						Session session = runTestCase(testCaseProvider, kb);
						results.add(CoverageUtils.getCoverage(session));
					}
				}
			}
			// TODO fixme: only works for 1 compiling KB, use article in
			// storeObject??
			System.out.println("No of coveragse;: " + results.size());

			// Path2GraphViz.createPaths(kb, result.getPathCounts().keySet());

		}
		result = DefaultCoverageResult.calculateResult(results, kb);
		coverageSec.getSectionStore().storeObject(DiaFluxCoverageType.COVERAGE_RESULT, result);
		return result;
	}

	private static Session runTestCase(TestCaseProvider testCaseProvider, KnowledgeBase kb) {
		TestCase testCase = testCaseProvider.getTestCase();
		Session session = SessionFactory.createSession(kb,
				testCase.getStartDate());
		for (Date date : testCase.chronology()) {
			TestCaseUtils.applyFindings(session, testCase, date);
		}
		return session;
	}

}
