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
package de.knowwe.rdfs.testcase.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.knowwe.rdfs.testcase.Binding;
import de.knowwe.rdfs.testcase.RDFSTestCase;
import de.knowwe.rdfs.testcase.diff.Diff;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCaseAnalysisReport {

	private final RDFSTestCase testCase;
	private final Collection<Diff> diffs = new HashSet<Diff>();
	private Collection<Binding> queryResult;
	private double correctlyDerived = Double.NaN;

	public RDFSTestCaseAnalysisReport(RDFSTestCase testCase) {
		if (testCase == null) {
			throw new NullPointerException("The test case is null!");
		}
		this.testCase = testCase;
	}

	public void addDiff(Diff diff) {
		if (diff == null) {
			throw new IllegalArgumentException("The diff is null!");
		}
		diffs.add(diff);
	}

	public RDFSTestCase getTestCase() {
		return testCase;
	}

	public double precision() {
		double derived = queryResult.size();
		return correctlyDerived() / derived;
	}

	public double recall() {
		double expected = testCase.getExpectedBindings().size();
		return correctlyDerived() / expected;
	}

	public double correctlyDerived() {

		// compute this only once...
		if (Double.isNaN(correctlyDerived)) {

			// union(derived, expected)
			Set<Binding> derivedUnionExpected = new HashSet<Binding>();
			derivedUnionExpected.addAll(testCase.getExpectedBindings());
			derivedUnionExpected.addAll(queryResult);

			/*
			 * diffs = intersect(exp, not der) + intersect(not exp, der)
			 * intersect(der, exp) = union(derived, expected) \ diffs
			 */
			correctlyDerived = derivedUnionExpected.size() - diffs.size();
		}

		return correctlyDerived;
	}

	public Collection<Diff> getDiffs() {
		return Collections.unmodifiableCollection(diffs);
	}

	public boolean hasDiffs() {
		return !diffs.isEmpty();
	}

	public void setQueryResult(Collection<Binding> queryResult) {
		if (queryResult == null) {
			throw new NullPointerException("The query result is null!");
		}
		this.queryResult = queryResult;
	}

}
