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

import de.d3web.we.selenium.main.KnowledgeTestCase;

/**
 * Testing the questionsheet's functionality on the CarDiagnosis example.
 * 
 * @author Max Diez
 * 
 */
public class CarDiagnosisTest extends KnowledgeTestCase {

	private boolean result;

	public CarDiagnosisTest() {
		super();
		this.isDialog = false;
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD1() {
		initKnowledgeTest();
		solutions.put("Check: Battery", new String[] { "not ok" });
		solutions.put("Check: Ignition timing", new String[] { "not ok" });
		solutions.put("Check: Air filter", new String[] { "not ok" });
		solutions.put("Check: Air intake system", new String[] { "not ok" });
		solutions.put("Check: Idle speed system", new String[] { "not ok" });
		result = checkSolutions(new String[] {
				"Flat battery", "Dead battery", "Bad ignition timing",
				"Clogged air filter", "Leaking air intake system",
				"Damaged idle speed system" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD2() {
		initKnowledgeTest();
		solutions.put("Engine noises", new String[] { "ringing" });
		result = checkSolutions(new String[] { "Bad ignition timing" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD3() {
		initKnowledgeTest();
		solutions.clear();
		solutions.put("Engine start", new String[] { "engine barely starts" });
		solutions.put("Check: Battery", new String[] { "ok" });
		result = checkAndUncheckSolutions(new String[] { "Damaged idle speed system" },
				new String[] { "Flat battery" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD4() {
		initKnowledgeTest();
		solutions.put("Exhaust pipe color", new String[] { "grey" });
		solutions.put("Driving", new String[] {
				"insufficient power on partial load", "insufficient power on full load",
				"unsteady idle speed" });
		result = checkAndUncheckSolutions(new String[] { "Leaking air intake system" },
				new String[] { "Clogged air filter" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD5() {
		initKnowledgeTest();
		solutions.put("Exhaust pipe color", new String[] { "sooty black" });
		solutions.put("Fuel", new String[] { "unleaded gasoline" });
		result = checkSolutions(new String[] { "Clogged air filter" }, solutions, isDialog);
		assertEquals("Covering-List not working:" + testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD6() {
		initKnowledgeTest();
		solutions.put("Exhaust fumes", new String[] { "black" });
		solutions.put("Fuel", new String[] { "unleaded gasoline" });
		result = checkSolutions(new String[] { "Clogged air filter" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}

	/**
	 * @created 02.11.2010
	 */
	public void testCD7() {
		initKnowledgeTest();
		solutions.put("Exhaust fumes", new String[] { "black" });
		solutions.put("Fuel", new String[] { "unleaded gasoline" });
		solutions.put("Check: Battery", new String[] { "not ok" });
		result = checkSolutions(new String[] {
				"Clogged air filter", "Flat battery", "Dead battery" }, solutions, isDialog);
		assertEquals(testResult, true, result);
	}
}
