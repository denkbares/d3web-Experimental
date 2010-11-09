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

package de.d3web.we.selenium.tests;

import de.d3web.we.selenium.main.KnowWETestCase;

/**
 * This class creates all the pages being needed for the Selenium-Test of KnowWE
 * especially to test the car diagnosis.
 * 
 * @author Max Diez
 * 
 */
public class SetUpWikiPages extends KnowWETestCase {

	public void testCreateWikiPages() throws Exception {

		String[] links = null;

		open("Wiki.jsp?page=Main");
		assertEquals("KnowWE: Main", selenium.getTitle());

		// Add ShowSolutions if necessary
		if (!selenium.isElementPresent(ST_LOC)) {
			open("/KnowWE/Wiki.jsp?page=LeftMenu");
			loadAndWait(B_EDIT);
			doSelActionAndWait(EA, "type",
					selenium.getValue(EA)
							+ "\n\n%%ShowSolutions\n@master: Demo - Master\n@show_established: true\n@show_suggested: true\n@show_excluded: true\n@show_abstractions: true\n%");
			loadAndWait(B_SAVE);
			open("/KnowWE/Wiki.jsp?page=Main");
		}

		assertTrue("ShowSolutions wasn't integrated",
				selenium.isElementPresent("LeftMenu/RootType/ShowSolutions"));

		open("Wiki.jsp?page=Test-Selenium-Main");
		if (selenium.isElementPresent("link=Test-Renaming-Tool")) {
			return;
		}

		loadAndWait(B_EDIT);
		doSelActionAndWait(EA, "type", selenium.getValue(EA)
				+ "\n\n[Selenium-Test]");
		loadAndWait(B_SAVE);


		// Selenium Main Page
		loadAndWait("link=Selenium-Test");
		doSelActionAndWait(EA, "type", rb.getString("KnowWE.SeleniumTest.MainPage"));
		loadAndWait(B_SAVE);

		// Car-Diagnosis-Test Page
		loadAndWait("link=Car-Diagnosis-Test");
		doSelActionAndWait(EA, "type",
				rb.getString("KnowWE.SeleniumTest.Test-Demo-Car-Diagnosis"));
		loadAndWait(B_SAVE);

		loadAndWait("link=Selenium-Test");
		loadAndWait("link=Car-Diagnosis-Test");
		assertTrue("Die 'Car Diagnosis' Seite wurde nicht richtig erstellt",
				selenium.isTextPresent("Car Diagnosis"));

		// create all Car Diagnosis pages
		// and all the solution pages
		links = new String[] {
				"Car-Diagnosis-Test", "Damaged idle speed system" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-DamagedIdleSpeedSystem"));

		links = new String[] {
				"Car-Diagnosis-Test", "Leaking air intake system" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-LeakingAirIntakeSystem"));

		links = new String[] {
				"Car-Diagnosis-Test", "Clogged air filter" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-CloggedAirFilter"));


		links = new String[] {
				"Car-Diagnosis-Test", "Bad ignition timing" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-BadIgnitionTiming"));

		links = new String[] {
				"Car-Diagnosis-Test", "Flat battery" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-FlatBattery"));

		links = new String[] {
				"Car-Diagnosis-Test", "Master Knowledge Base" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-Master"));

		links = new String[] {
				"Car-Diagnosis-Test", "Terminology" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-Terminology"));
		links = new String[] {
				"Car-Diagnosis-Test", "Testing" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Test-Demo-Test-Cases"));

	
		// create the Quick-Edit test page
		links = new String[] { "Test-Quick-Edit" };
		this.createPage(links, rb.getString("KnowWE.SeleniumTest.Quick-Edit-Test"));

		// Hermes-Test Page
		open("Edit.jsp?page=TimeLineEntries");
		doSelActionAndWait(EA, "type", rb.getString("KnowWE.SeleniumTest.TimeLineEntries"));
		loadAndWait(B_SAVE);
		
		// Renaming-Tool-Test Page
		open("Edit.jsp?page=Test-Renaming-Tool");
		doSelActionAndWait(EA, "type", rb.getString("KnowWE.SeleniumTest.Renaming"));
		loadAndWait(B_SAVE);
		
	}

	/**
	 * 
	 * 
	 * @created 02.11.2010
	 */
	private void createPage(String[] links, String pagecontent) {
		String baseURL = rb.getString("KnowWE.SeleniumTest.url");
		open(baseURL + "/Wiki.jsp?page=Test-Selenium-Main");

		// navigate to page
		for (String link : links) {
			loadAndWait("link=" + link);
		}
		doSelActionAndWait(EA, "type", pagecontent);
		loadAndWait(B_SAVE);
	}

}
