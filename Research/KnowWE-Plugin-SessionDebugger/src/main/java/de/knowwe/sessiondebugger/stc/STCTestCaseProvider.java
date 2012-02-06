/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.sessiondebugger.stc;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.testcase.stc.STCWrapper;
import de.d3web.we.basic.D3webModule;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.report.Messages;
import de.knowwe.sessiondebugger.AttachmentTestCaseProvider;

/**
 * Wraps an Attachment containing an {@link SequentialTestCase}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.01.2012
 */
public class STCTestCaseProvider extends AttachmentTestCaseProvider {

	public STCTestCaseProvider(KnowWEArticle article, String fileName, KnowWEArticle fileArticle) {
		super(article, fileName, fileArticle);
	}

	@Override
	protected void parse() {
		KnowledgeBase kb = D3webModule.getKnowledgeBase(article.getWeb(), article.getTitle());
		if (kb == null) {
			messages.add(Messages.error("Kb not found."));
			return;
		}
		try {
			List<SequentialTestCase> cases = TestPersistence.getInstance().loadCases(
					attachment.getInputStream(), kb);
			if (cases.size() != 1) {
				messages.add(Messages.error("The attached SequentialTestCase file "
						+ attachment.getFileName()
							+ " has " + cases.size()
							+ " cases. Only files with exactly one case are allowed."));
				return;
			}
			else {
				testCase = new STCWrapper(cases.get(0));
			}
		}
		catch (XMLStreamException e) {
			messages.add(Messages.error("File " + attachment.getFileName()
					+ " does not contain correct xml markup."));
		}
		catch (IOException e) {
			messages.add(Messages.error("File " + attachment.getFileName() + " is not accessible."));
		}
	}

}
