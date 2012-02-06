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
package de.knowwe.sessiondebugger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * An abstract TestCaseFile Subtreehandler that generates
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 02.02.2012
 */
public abstract class TestCaseFilesSubtreeHandler<T extends DefaultMarkupType> extends SubtreeHandler<T> {

	@Override
	public Collection<Message> create(KnowWEArticle article, Section<T> section) {
		String[] fileNames = DefaultMarkupType.getAnnotations(section, "file");
		TestCaseProviderStorage testCaseProviderStorage = new TestCaseProviderStorage();
		List<Message> messages = new LinkedList<Message>();
		// TODO: reuse old testCases
		for (String fileName : fileNames) {
			addTestCaseProvider(article, testCaseProviderStorage, messages, fileName,
					section.getArticle());
		}
		section.getSectionStore().storeObject(article, TestCaseProviderStorage.KEY,
				testCaseProviderStorage);
		return messages;
	}

	protected abstract void addTestCaseProvider(KnowWEArticle article, TestCaseProviderStorage testCaseProviderStorage, List<Message> messages, String fileName, KnowWEArticle fileArticle);

}
