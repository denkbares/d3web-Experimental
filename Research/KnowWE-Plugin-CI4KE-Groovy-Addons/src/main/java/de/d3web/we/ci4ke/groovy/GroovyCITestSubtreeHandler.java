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
import java.util.Collection;

import de.d3web.testing.Test;
import de.d3web.we.ci4ke.build.CIConfig;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class GroovyCITestSubtreeHandler extends SubtreeHandler<GroovyCITestType> {

	/**
	 * Prepend the groovy-code with some import statements
	 */
	public static final String PREPEND = "import " + CIConfig.class.getName() + ";\n" +
			"import " + Message.class.getName() + ";\n" +
			"import static " + de.d3web.testing.Message.Type.class.getName()
			+ ".*;\n";

	@Override
	public Collection<Message> create(Article article, Section<GroovyCITestType> s) {
		// create collection for return messages
		Collection<Message> messages = new ArrayList<Message>();
		// parse name of test and check if its name is unique in the wiki
		String testname = DefaultMarkupType.getAnnotation(s, GroovyCITestType.ANNOTATION_NAME);
		for (Section<GroovyCITestType> section : GroovyDynamicCITestHandler.getAllGroovyCITestSectionsByList()) {
			String annotationName = DefaultMarkupType.getAnnotation(section,
					GroovyCITestType.ANNOTATION_NAME);
			if (testname.equals(annotationName) && !s.getID().equals(section.getID())) {
				// found other CITest with the same name!
				messages.add(Messages.error("The name '" + testname
						+ "' of this CITest is not unique. " +
						"Please select another name!"));
			}
		}
		String sectionContent = DefaultMarkupType.getContent(s);
		Message result = null;
		try {
			// TODO: how to make this working in combination with the
			// TestingFramework?
			Class<? extends Test<?>> testClazz = GroovyDynamicCITestHandler.
					parseGroovyCITest(sectionContent);
			// result = testClazz.newInstance().execute();
		}
		catch (Exception e) {
			String errorMessageMasked = e.getLocalizedMessage();
			messages.add(Messages.error(errorMessageMasked));
		}
		if (messages.size() == 0 && result == null) {
			// only report this error if no other error was found until now!
			messages.add(Messages.error("The test didn't returned a CITestResult!"));
		}
		// now check for errors and return the appropriate result.
		if (messages.size() > 0) {// there were errors!
			return messages;
		}
		else {
			return Messages.asList(Messages.objectCreatedNotice(
					"CITest successfully created!"));
		}
	}
}
