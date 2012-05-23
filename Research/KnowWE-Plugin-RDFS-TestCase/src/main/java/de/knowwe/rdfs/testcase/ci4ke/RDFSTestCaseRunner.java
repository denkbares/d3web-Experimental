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
package de.knowwe.rdfs.testcase.ci4ke;

import cc.denkbares.testing.ArgsCheckResult;
import cc.denkbares.testing.Message;
import cc.denkbares.testing.Message.Type;
import cc.denkbares.testing.Test;
import de.knowwe.core.Environment;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.testcase.RDFSTestCase;
import de.knowwe.rdfs.testcase.analysis.RDFSTestCaseAnalysis;
import de.knowwe.rdfs.testcase.analysis.RDFSTestCaseAnalysisReport;
import de.knowwe.rdfs.testcase.diff.Diff;
import de.knowwe.rdfs.testcase.util.RDFSTestCaseLoader;

/**
 * 
 * @author Sebastian Furth
 * @created 22.12.2011
 */
public class RDFSTestCaseRunner implements Test<Rdf2GoCore> {

	@Override
	public Message execute(Rdf2GoCore core, String[] args) {

		String monitoredArticleTitle = args[0];
		String testCaseName = args[1];

		// load test case
		RDFSTestCase testCase = RDFSTestCaseLoader.loadTestCase(
				monitoredArticleTitle, Environment.DEFAULT_WEB, testCaseName);

		if (testCase == null) {
			return new Message(Type.ERROR, "No test case with name '" + testCaseName
					+ "' found in article '" + monitoredArticleTitle + "'");
		}

		// run test case
		RDFSTestCaseAnalysisReport report = RDFSTestCaseAnalysis.runAndAnalyseTestCase(testCase);

		// check for diffs
		if (report.hasDiffs()) {
			StringBuilder description = new StringBuilder();
			description.append("Precision: ");
			description.append(report.precision());
			description.append("; Recall: ");
			description.append(report.recall());
			description.append("\n");
			for (Diff diff : report.getDiffs()) {
				description.append(diff);
				description.append("\n");
			}
			return new Message(Type.FAILURE, description.toString());
		}

		// report.recall() == 1.0 && report.precision() == 1.0
		return new Message(Type.SUCCESS, null);
	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {
		int expectedArgCount = 2;
		if (args.length == expectedArgCount) return new ArgsCheckResult(ArgsCheckResult.Type.FINE);
		return new ArgsCheckResult(ArgsCheckResult.Type.ERROR, "Expected number of arguments: "
				+ expectedArgCount + " - found: " + args.length);
	}

	@Override
	public Class<Rdf2GoCore> getTestObjectClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
