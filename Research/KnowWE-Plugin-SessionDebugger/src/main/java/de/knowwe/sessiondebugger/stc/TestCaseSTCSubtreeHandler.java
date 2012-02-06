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

import java.util.List;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.sessiondebugger.AttachmentTestCaseProvider;
import de.knowwe.sessiondebugger.TestCaseFilesSubtreeHandler;
import de.knowwe.sessiondebugger.TestCaseProviderStorage;

/**
 * {@link SubtreeHandler} for creating an {@link STCTestCaseProvider}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.01.2012
 */
public class TestCaseSTCSubtreeHandler extends TestCaseFilesSubtreeHandler<TestCaseSTCType> {

	@Override
	protected void addTestCaseProvider(KnowWEArticle article, TestCaseProviderStorage testCaseProviderStorage, List<Message> messages, String fileName, KnowWEArticle filearArticle) {
		AttachmentTestCaseProvider provider = new STCTestCaseProvider(article, fileName,
				filearArticle);
		testCaseProviderStorage.addProvider(provider);
		messages.addAll(provider.getMessages());
	}

}
