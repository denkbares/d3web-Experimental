/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.DefaultCoverageResult;
import de.d3web.diaflux.coverage.DiaFluxCoverageTrace;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.d3webviz.diafluxCity.GLBuilding;
import de.knowwe.d3webviz.diafluxCity.GLCity;
import de.knowwe.d3webviz.diafluxCity.GLCityGenerator;
import de.knowwe.d3webviz.diafluxCity.GLNodeMapper;
import de.knowwe.d3webviz.diafluxCity.metrics.Constant;
import de.knowwe.d3webviz.diafluxCity.metrics.Metrics;
import de.knowwe.d3webviz.diafluxCity.metrics.MetricsSet;
import de.knowwe.d3webviz.diafluxCity.metrics.OutgoingEdgesMetric;
import de.knowwe.diaflux.coverage.CoverageTestListener;
import de.knowwe.diaflux.coverageCity.metrics.CoveredOutgoingEdgesMetric;
import de.knowwe.diaflux.coverageCity.metrics.CoveredPathsColorMetric;
import de.knowwe.diaflux.coverageCity.metrics.MaximumInSameFlowNodeCoverage;
import de.knowwe.diaflux.coverageCity.metrics.NodeCoverageMetric;


/**
 * 
 * @author Reinhard Hatko
 * @created 16.02.2012
 */
public class GLCityTest {

	public static void main(String[] args) {

		randomCity();

	}

	public static void cityFromKB() {

		try {
			InitPluginManager.init();
			// String kbName = "bmi.d3web";
			String kbName = "CG_BIPAP.s42";
			String workspace = "D:\\test\\";
			File kbFile = new File(workspace + kbName);

			KnowledgeBase kb = PersistenceManager.getInstance().load(kbFile);

			CoverageResult coverage = runTest(kb, kbFile.getParentFile());


			GLCity city = GLCityGenerator.generateCity(createMetrics(coverage), kb);

			System.out.println(city);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(city.toString()), null);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static MetricsSet<Node> createMetrics(CoverageResult coverage) {
		MetricsSet<Node> metrics = new MetricsSet<Node>(Arrays.asList("Wait"), true);
		metrics.setHeightMetric(Metrics.multiply(Metrics.relate(new
				NodeCoverageMetric(coverage),
				new MaximumInSameFlowNodeCoverage(coverage)), 5));
		metrics.setHeightMetric(new CoveredOutgoingEdgesMetric(coverage));
		metrics.setLengthMetric(new OutgoingEdgesMetric());
		metrics.setWidthMetric(Metrics.relate(new
				NodeCoverageMetric(coverage),
				new Constant<Node>(15)));
		metrics.setColorMetric(new CoveredPathsColorMetric(coverage));

		// metrics.setLengthMetric(new IncomingEdgesMetric());
		// metrics.setHeightMetric(new IncomingEdgesMetric());
		// metrics.setWidthMetric(new OutgoingEdgesMetric());
		// metrics.setColorMetric(new NodeTypeColorMetric());
		return metrics;
	}

	/**
	 * 
	 * @created 16.02.2012
	 */
	public static void randomCity() {
		double margin = 1;
		List<GLBuilding> boxes = new LinkedList<GLBuilding>();

		{
			List<GLBuilding> distBoxes = new LinkedList<GLBuilding>();
			distBoxes.add(new GLBuilding(3, 2, 3));
			distBoxes.add(new GLBuilding(4, 4, 3));

			{
				List<GLBuilding> distBoxes2 = new LinkedList<GLBuilding>();
				distBoxes2.add(new GLBuilding(5, 2, 5));
				distBoxes2.add(new GLBuilding(2, 4, 5));

				GLBuilding district = GLCityGenerator.createDistrict(distBoxes2,
						new GLNodeMapper(), margin, .3);
				distBoxes.add(district);
			}

			GLBuilding district = GLCityGenerator.createDistrict(distBoxes, new GLNodeMapper(),
					margin,
					.3);
			boxes.add(district);
		}

		boxes.add(new GLBuilding(4, 2, 1));
		boxes.add(new GLBuilding(3, 6, 1));
		boxes.add(new GLBuilding(3, 6, 1));
		boxes.add(new GLBuilding(3, 6, 1));
		boxes.add(new GLBuilding(3, 6, 1));

		GLBuilding building = GLCityGenerator.createDistrict(boxes, new GLNodeMapper(), margin, .3);

		System.out.println(building);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(building.toString()), null);
	}

	private static CoverageResult runTest(KnowledgeBase kb, File workspace) throws MalformedURLException {

		TestCaseAnalysis analysis = new TestCaseAnalysis();
		CoverageTestListener listener = new CoverageTestListener();
		analysis.addTestListener(listener);
		TestCase suite = loadCases(workspace.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		}), kb);
		analysis.runAndAnalyze(suite);
		Map<SequentialTestCase, DiaFluxCoverageTrace> results = listener.getResults();

		return DefaultCoverageResult.calculateResult(results.values(), kb);
	}

	public static TestCase loadCases(File[] stcInputs, KnowledgeBase knowledge) throws MalformedURLException {
		List<SequentialTestCase> cases = new ArrayList<SequentialTestCase>();
		for (File file : stcInputs) {
			List<SequentialTestCase> loadedCases = TestPersistence.getInstance().loadCases(
					file.toURI().toURL(), knowledge);
			if (loadedCases != null) {
				cases.addAll(loadedCases);
			}
		}

		TestCase suite = new TestCase();
		suite.setKb(knowledge);
		suite.setRepository(cases);

		return suite;
	}
}
