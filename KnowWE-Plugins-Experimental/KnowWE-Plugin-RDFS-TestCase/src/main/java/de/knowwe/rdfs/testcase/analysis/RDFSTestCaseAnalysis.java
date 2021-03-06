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
import java.util.HashSet;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.testcase.Binding;
import de.knowwe.rdfs.testcase.RDFSTestCase;
import de.knowwe.rdfs.testcase.diff.MissingBindingDiff;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCaseAnalysis {

	public static RDFSTestCaseAnalysisReport runAndAnalyseTestCase(RDFSTestCase testCase) {

		RDFSTestCaseAnalysisReport report = new RDFSTestCaseAnalysisReport(testCase);

		Collection<Binding> queryResult = executeQuery(testCase);
		report.setQueryResult(queryResult);
		createDiffs(testCase, queryResult, report);

		return report;
	}

	private static void createDiffs(RDFSTestCase testCase, Collection<Binding> derivedBindings, RDFSTestCaseAnalysisReport report) {

		// expected but not derived
		for (Binding expectedBinding : testCase.getExpectedBindings()) {
			if (!derivedBindings.contains(expectedBinding)) {
				report.addDiff(new MissingBindingDiff(expectedBinding));
			}
		}

		// derived but not expected
		// we actually dont need this, as derived but not expected is not an
		// issue
		// for (Binding derivedBinding : derivedBindings) {
		// if (!testCase.getExpectedBindings().contains(derivedBinding)) {
		// report.addDiff(new UnexpectedBindingDiff(derivedBinding));
		// }
		// }

	}

	private static Collection<Binding> executeQuery(RDFSTestCase testCase) {
		// execute SPARQL-Query
		QueryResultTable result = Rdf2GoCore.getInstance().sparqlSelect(testCase.getSparqlQuery());

		// derived bindings
		Collection<Binding> bindings = new HashSet<Binding>();

		// convert result table to bindings
		if (result != null) {

			// add the actual bindings
			ClosableIterator<QueryRow> iter = result.iterator();
			while (iter.hasNext()) {
				QueryRow row = iter.next();
				Binding binding = new Binding();

				for (String var : result.getVariables()) {
					Node valueNode = row.getValue(var);
					if (valueNode != null) {
						String value = valueNode.toString();
						value = processValue(value);
						if (value != null) {
							binding.addURI(value);
						}
					}
				}

				bindings.add(binding);
			}
		}

		// return bindings
		return bindings;
	}

	private static String processValue(String value) {
		value = Strings.decodeURL(value);
		value = Rdf2GoUtils.reduceNamespace(Rdf2GoCore.getInstance(), value);
		if (value.startsWith("lns:")) {
			return value.substring(4);
		}
		return value;
	}

}
