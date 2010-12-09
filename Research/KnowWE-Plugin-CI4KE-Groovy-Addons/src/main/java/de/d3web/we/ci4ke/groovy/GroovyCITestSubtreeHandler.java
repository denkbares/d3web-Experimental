/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.d3web.we.ci4ke.handling.CIConfig;
import de.d3web.we.ci4ke.testing.CITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.TestResultType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SimpleMessageError;
import de.d3web.we.kdom.report.message.ObjectCreatedMessage;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;

public class GroovyCITestSubtreeHandler extends SubtreeHandler<GroovyCITestType> {

	/**
	 * Prepend the groovy-code with some import statements
	 */
	public static final String PREPEND = "import " + CIConfig.class.getName() + ";\n" +
						"import " + CITestResult.class.getName() + ";\n" +
						"import static " + TestResultType.class.getName() + ".*;\n";

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<GroovyCITestType> s) {
		// create collection for return messages
		Collection<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();
		// parse name of test and check if its name is unique in the wiki
		String testname = DefaultMarkupType.getAnnotation(s, GroovyCITestType.ANNOTATION_NAME);
		for (Section<GroovyCITestType> section : GroovyDynamicCITestHandler.getAllGroovyCITestSectionsByList()) {
			String annotationName = DefaultMarkupType.getAnnotation(section,
					GroovyCITestType.ANNOTATION_NAME);
			if (testname.equals(annotationName) && !s.getID().equals(section.getID())) {
				// found other CITest with the same name!
				messages.add(new SimpleMessageError("The name '" + testname
						+ "' of this CITest is not unique. " +
						"Please select another name!"));
			}
		}
		String sectionContent = DefaultMarkupType.getContent(s);
		CITestResult result = null;
		try {
			Class<? extends CITest> testClazz = GroovyDynamicCITestHandler.
					parseGroovyCITest(sectionContent);
			result = testClazz.newInstance().call();
		}
		catch (Exception e) {
			String errorMessageMasked = KnowWEUtils.maskHTML(
					KnowWEUtils.maskNewline(e.getLocalizedMessage()));
			messages.add(new SimpleMessageError(errorMessageMasked));
		}
		if (messages.size() == 0 && result == null) {
			// only report this error if no other error was found until now!
			messages.add(new SimpleMessageError("The test didn't returned a CITestResult!"));
		}
		// now check for errors and return the appropriate result.
		if (messages.size() > 0) {// there were errors!
			return messages;
		}
		else {
			return Arrays.asList((KDOMReportMessage) new ObjectCreatedMessage(
					"CITest successfully created!"));
		}
	}
}
