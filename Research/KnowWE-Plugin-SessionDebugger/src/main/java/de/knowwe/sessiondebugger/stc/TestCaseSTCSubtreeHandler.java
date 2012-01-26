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

import java.util.Collection;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.sessiondebugger.TestCaseProvider;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.01.2012
 */
public class TestCaseSTCSubtreeHandler extends SubtreeHandler<TestCaseSTCType> {

	public TestCaseSTCSubtreeHandler() {
		setIgnorePackageCompile(true);
	}

	@Override
	public Collection<Message> create(KnowWEArticle article, Section<TestCaseSTCType> section) {
		String masterName = DefaultMarkupType.getAnnotation(section, "master");
		String fileName = DefaultMarkupType.getAnnotation(section, "file");

		STCTestCaseProvider provider = new STCTestCaseProvider(masterName,
				article.getWeb(), fileName, article.getTitle());
		section.getSectionStore().storeObject(TestCaseProvider.KEY, provider);
		return provider.getMessages();
	}

}
