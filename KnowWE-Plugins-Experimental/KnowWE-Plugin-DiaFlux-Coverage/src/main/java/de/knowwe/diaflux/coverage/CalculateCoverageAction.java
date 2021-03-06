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
package de.knowwe.diaflux.coverage;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.DiaFluxCoverageTrace;
import de.d3web.testcase.TestCaseUtils;
import de.d3web.testcase.model.TestCase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.testcases.TestCaseProvider;
import de.knowwe.testcases.TestCaseProviderStorage;

/**
 * 
 * @author Reinhard Hatko
 * @created 13.09.2011
 */
public class CalculateCoverageAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		// String coverageKdomid = context.getParameter("kdomid");
		//
		// Section<DiaFluxCoverageType> coverageSec =
		// Sections.get(coverageKdomid,
		// DiaFluxCoverageType.class);
		//
		// String[] packages = DefaultMarkupType.getPackages(coverageSec);
		//
		// Environment env = Environment.getInstance();
		// PackageManager packageManager =
		// env.getPackageManager(context.getWeb());
		//
		// for (String packageName : packages) {
		// List<Section<?>> sectionsOfPackage =
		// packageManager.getSectionsOfPackage(packageName);
		//
		// }
		// Set<String> compilingArticles =
		// packageManager.getCompilingArticles(packages);
		// if (compilingArticles.isEmpty()) {
		// return;
		// }
		//
		// // TODO use every KB, not only first
		// Article article =
		// Environment.getInstance().getArticleManager(context.getWeb()).getArticle(compilingArticles.iterator().next());
		// KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(),
		// article.getTitle());
		// List<CoverageSessionObject> results = new
		// LinkedList<CoverageSessionObject>();
		//
		// for (Section<?> section : sectionsOfPackage) {
		//
		// TestCaseProviderStorage testCaseProviderStorage =
		// (TestCaseProviderStorage) section.getSectionStore().getObject(
		// article, TestCaseProviderStorage.KEY);
		// if (testCaseProviderStorage != null) {
		// for (TestCaseProvider testCaseProvider :
		// testCaseProviderStorage.getTestCaseProviders()) {
		// Session session = runTestCase(testCaseProvider, kb);
		// results.add(PSMDiaFluxCoverage.getCoverage(session));
		//
		// }
		// }
		// }
		String coverageKdomid = context.getParameter(Attributes.SECTION_ID);

		Section<DiaFluxCoverageType> coverageSec = Sections.get(coverageKdomid,
				DiaFluxCoverageType.class);

		// TODO error handling
		if (coverageSec == null) {
			return;
		}

		calculateCoverage(coverageSec);

	}

	/**
	 * 
	 * @created 26.03.2012
	 * @param context
	 * @param coverageSec
	 */
	public static void calculateCoverage(Section<DiaFluxCoverageType> coverageSec) {
		PackageManager packageManager = KnowWEUtils.getPackageManager(coverageSec);

		KnowledgeBase kb = D3webUtils.getKnowledgeBase(coverageSec);
		Set<String> packageNames = coverageSec.getPackageNames();
		String[] packageArray = packageNames.toArray(new String[packageNames.size()]);
		Collection<Section<?>> sectionsOfPackages = packageManager.getSectionsOfPackage(packageArray);
		List<DiaFluxCoverageTrace> results = new LinkedList<>();
		for (Section<?> potentioalTestCaseSection : sectionsOfPackages) {
			if (!(potentioalTestCaseSection.get() instanceof DefaultMarkupType)) continue;
			TestCaseProviderStorage testCaseProviderStorage = de.knowwe.testcases.TestCaseUtils.getTestCaseProviderStorage(
					potentioalTestCaseSection);

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
		CoverageResult result = DefaultCoverageResult.calculateResult(results, kb);
		coverageSec.storeObject(DiaFluxCoverageType.COVERAGE_RESULT, result);

		// Path2GraphViz.createPaths(kb, result.getPathCounts().keySet());

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
