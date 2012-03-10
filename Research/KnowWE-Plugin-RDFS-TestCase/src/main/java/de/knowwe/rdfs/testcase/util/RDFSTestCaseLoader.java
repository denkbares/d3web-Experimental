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

import java.util.List;
import java.util.logging.Logger;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
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

		Article a = Environment.getInstance().getArticleManager(web).getArticle(article);

		RDFSTestCase testCase = null;

		// try to get RDFSTestCase section in article
		if (a != null) {

			// Get all RDFSTestCaseSections
			List<Section<RDFSTestCaseType>> sections = Sections.findSuccessorsOfType(
					a.getSection(), RDFSTestCaseType.class);

			if (sections != null && !sections.isEmpty()) {
				for (Section<RDFSTestCaseType> s : sections) {

					// get name of RDFSTestCase
					String name = DefaultMarkupType.getAnnotation(s,
							RDFSTestCaseType.ANNOTATION_NAME);

					// load test case if name matches
					if (name.equals(testCaseName)) {
						testCase = (RDFSTestCase) KnowWEUtils.getStoredObject(a, s,
								RDFSTestCaseType.MARKUP_NAME + testCaseName);
					}
				}

				// test case wasn't found
				if (testCase == null) {
					Logger.getLogger(RDFSTestCaseLoader.class.getName()).warning(
							"Article: \"" + article + "\" doesn't contain a test case named \""
									+ testCaseName + "\".");
				}
			}

			else {
				Logger.getLogger(RDFSTestCaseLoader.class.getName()).warning(
						"Article: \"" + article + "\" doesn't contain any test case");
			}

		}
		else {
			Logger.getLogger(RDFSTestCaseLoader.class.getName()).warning(
					"Article: \"" + article + "\" wasn't found. Unable to load test case!");
		}

		return testCase;
	}
}
