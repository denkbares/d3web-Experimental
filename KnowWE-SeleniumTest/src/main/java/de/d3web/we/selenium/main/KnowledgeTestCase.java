/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.selenium.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Abstract Class containing some special methods being helpful to test
 * knowledge functionality like questionsheet or dialog.
 *
 * @author Max Diez
 *
 */
public abstract class KnowledgeTestCase extends KnowWETestCase {

	/* Some string constants mostly for locators; explanations in property file */
	final String ST_CLEAR = rb.getString("KnowWE.SeleniumTest.Knowledge-Test.st_clear");
	final String OBS_LOC = rb.getString("KnowWE.SeleniumTest.Knowledge-Test.obs_loc");
	final String QPAGE_LOC = rb.getString("KnowWE.SeleniumTest.Knowledge-Test.qpage_loc");
	final String CAT_LOC = rb.getString("KnowWE.SeleniumTest.Knowledge-Test.cat_loc");
	final String CAT_OK = rb.getString("KnowWE.SeleniumTest.Knowledge-Test.cat_ok");

	/**
	 * Hash map to store the input needed for the tests.
	 */
	protected final HashMap<String, String[]> solutions = new HashMap<String, String[]>();

	/**
	 * Stores some information for a particular test.
	 */
	protected String testResult = new String();
	/**
	 * Specifies whether the test is inspection the dialog or not.
	 */
	protected Boolean isDialog;

	/**
	 * @see checkAndUncheckSolutions
	 */
	protected boolean checkSolutions(String[] solutions, Map<String, String[]> input, boolean isDialog) {
		return checkAndUncheckSolutions(solutions, new String[] {}, input, isDialog);
	}

	protected boolean checkAndUncheckSolutions(String[] expSolutions, String[] notExpSolutions,
			Map<String, String[]> input, boolean isDialog) {

		doSelActionAndWait(ST_CLEAR, "click");

		Collection<String> k = input.keySet();
		Iterator<String> itr = k.iterator();

		while (itr.hasNext()) {

			String locator = "";
			String current = itr.next();
			Document doc = Jsoup.parse(selenium.getHtmlSource());
			Elements tables = doc.getElementsByTag("table");

			for (Element table : tables) {
				Elements tds = table.getElementsContainingText(current);
				if (tds.size() > 0) {
					Elements answers = table.select("div");
					for (Element answer : answers) {
						String[] values = input.get(current);
						for (String a : values) {
							if (answer.text().equals(a)) {
								locator = this.getXPath(answer);
								doSelActionAndWait(locator, "click");
							}
						}
					}
				}
			}
		}
		return verifySolutions(expSolutions, notExpSolutions, System.currentTimeMillis());
	}

	/**
	 * After selecting all answers in checkAndUncheckSolution, this method
	 * evaluates if the right solutions are shown in the ShowSolutions panel.
	 *
	 * @param expSolutions This is an array of all the solutions which should
	 *        appear after setting the input,
	 * @param notExpSolutions and these should not appear.
	 * @return true if with the input parameters all expected Solutions are
	 *         shown at ShowSolutions and all not expected not; else false.
	 */
	private boolean verifySolutions(String[] expSolutions, String[] notExpSolutions, long startTime) {
		String showSolutions = "";
		String showSolutionsExcluded = "";
		testResult = "";

		boolean elemenPresent = selenium.isElementPresent("//div[@id='content_LeftMenu/RootType/ShowSolutions']/span");
		assertEquals("No solutions displayed", true, elemenPresent);

		// SOLUTION-SUGGESTED
		// SOLUTION-ESTABLISHED
		Document document = Jsoup.parse(selenium.getHtmlSource());

		Elements suggested = document.select("span.SOLUTION-SUGGESTED");
		Elements established = document.select("span.SOLUTION-ESTABLISHED");

		showSolutions += established.toString();
		showSolutions += suggested.toString();

		boolean result = true;
		for (int i = 0; i < expSolutions.length; i++) {
			boolean hasExpSol = showSolutions.contains(expSolutions[i]);
			if (!hasExpSol) {
				testResult += "Didn't see solution " + expSolutions[i] + ". ";
			}
			result = result && hasExpSol;
		}

		// SOLUTION-EXCLUDED
		Elements excluded = document.select("span.SOLUTION-EXCLUDED");
		showSolutionsExcluded = excluded.toString();

		for (int i = 0; i < notExpSolutions.length; i++) {
			boolean hasntNotExpSol = (showSolutionsExcluded.equals(""))
					? true : showSolutionsExcluded.contains(notExpSolutions[i]);
			hasntNotExpSol = !showSolutions.contains(notExpSolutions[i]) && hasntNotExpSol;
			if (!hasntNotExpSol) {
				testResult += "Saw not expected solution " + notExpSolutions[i] + ". ";
			}
			result = result && hasntNotExpSol;
		}

		// Retry the check if result is false and time isn't expired
		if (System.currentTimeMillis() - startTime <
				Long.parseLong(rb.getString("KnowWE.SeleniumTest.RetryTime")) && !result) {
			refreshAndWait();
			return verifySolutions(expSolutions, notExpSolutions, startTime);
		}
		doSelActionAndWait(ST_CLEAR, "click");
		return result;
	}

	/**
	 * Initializes the Car Diagnosis Selenium test e.g. loads the correct page
	 * and ensure the correct pages had been loaded. Also resets some variables
	 * used for each test.
	 *
	 * @created 02.11.2010
	 */
	protected void initKnowledgeTest() {
		open("Wiki.jsp?page=Demo - Master");
		assertEquals("KnowWE: Demo - Master", selenium.getTitle());
		assertTrue("ShowSolutions not included",
				selenium.isElementPresent(ST_LOC));
		solutions.clear();
	}
}
