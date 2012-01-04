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
package de.knowwe.rdfs.testcase.util;

import java.util.logging.Logger;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.rdfs.testcase.RDFSTestCase;
import de.knowwe.rdfs.testcase.kdom.RDFSTestCaseType;

/**
 * 
 * @author Sebastian Furth
 * @created 22.12.2011
 */
public class RDFSTestCaseLoader {

	/**
	 * Loads a RDFS-Test Case which was stored in KnowWE's object store with the
	 * specified parameters.
	 * 
	 * @created 22.12.2011
	 * @param article
	 * @param web
	 * @param testCaseName
	 * @return
	 */
	public static RDFSTestCase loadTestCase(String article, String web, String testCaseName) {

		KnowWEArticle a = KnowWEEnvironment.getInstance().getArticleManager(web).getArticle(article);
		Section<RDFSTestCaseType> s = null;
		RDFSTestCase testCase = null;

		// try to get RDFSTestCase section in article
		if (a != null) {
			s = Sections.findSuccessor(a.getSection(), RDFSTestCaseType.class);
		}
		else {
			Logger.getLogger(RDFSTestCaseLoader.class.getName()).warning(
					"Article: \"" + article + "\" wasn't found. Unable to load test case!");
		}

		// try to load RDFSTestCase
		if (s != null) {
			testCase = (RDFSTestCase) KnowWEUtils.getStoredObject(a, s,
					RDFSTestCaseType.MARKUP_NAME + testCaseName);
		}
		else {
			Logger.getLogger(RDFSTestCaseLoader.class.getName()).warning(
					"Article: \"" + article + "\" doesn't contain a test case with name \""
							+ testCaseName + "\"");
		}

		return testCase;
	}

}