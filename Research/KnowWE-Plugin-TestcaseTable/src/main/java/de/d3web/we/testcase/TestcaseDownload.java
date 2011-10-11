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
package de.d3web.we.testcase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.writer.TestSuiteXMLWriter;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.05.2011
 */
public class TestcaseDownload extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String topic = context.getParameter(KnowWEAttributes.TOPIC);
		String web = context.getParameter("web");
		String filename = context.getParameter("filename");
		String nodeID = context.getParameter("nodeid");

		Section<TestcaseTableType> tableDMType = (Section<TestcaseTableType>) Sections.getSection(nodeID);

		Section<TestcaseTable> table = Sections.findSuccessor(tableDMType, TestcaseTable.class);

		KnowWEArticle master = KnowWEEnvironment.getInstance().getArticleManager(
				web).getArticle(TestcaseTableType.getMaster(tableDMType, topic));

		TestCase t = (TestCase) KnowWEUtils.getStoredObject(master, table,
				TestcaseTable.TESTCASE_KEY);

		if (t == null) {
			context.getWriter().write(
					"Error: There is no test case to download. Probably  errors  exist in your test case.");

		}
		else {

			// Get the file content
			TestSuiteXMLWriter c = new TestSuiteXMLWriter();
			ByteArrayOutputStream bstream = c.getByteArrayOutputStream(t.getRepository());

			// Response
			context.setContentType("text/xml");
			context.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
			context.setContentLength(bstream.size());

			// Write the data from the ByteArray to the ServletOutputStream of
			// the response
			bstream.writeTo(context.getOutputStream());

		}

	}
}
