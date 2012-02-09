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

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.diaflux.coverage.CoverageSessionObject;
import de.d3web.diaflux.coverage.PSMDiaFluxCoverage;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestListener;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;


/**
 * 
 * @author Reinhard Hatko
 * @created 13.09.2011
 */
public class CoverageTestListener implements TestListener {

	private final Map<SequentialTestCase, CoverageSessionObject> results;

	public CoverageTestListener() {
		this.results = new HashMap<SequentialTestCase, CoverageSessionObject>();
	}

	@Override
	public void testcaseStarting(TestCase tc) {
	}

	@Override
	public void testcaseFinished(TestCase tc, TestCaseAnalysisReport report) {
	}

	@Override
	public void sequentialTestcaseStarting(SequentialTestCase stc, Session session) {
//		System.out.println("Starting testcase: " + stc.getName() + " (" + stc.getCases().size()
//				+ " RTCs)");
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

	public Map<SequentialTestCase, CoverageSessionObject> getResults() {
		return results;
	}

}
