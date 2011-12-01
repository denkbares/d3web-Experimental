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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 27.05.2011
 */
public class TestcaseTableSubtreeHandler extends SubtreeHandler<TestcaseTable> {

	@Override
	public Collection<Message> create(KnowWEArticle article, Section<TestcaseTable> s) {

		TestCase testcase = new TestCase();
		SequentialTestCase stc = new SequentialTestCase();

		testcase.getRepository().add(stc);

		List<Section<TestcaseTableLine>> lines = new LinkedList<Section<TestcaseTableLine>>();
		Sections.findSuccessorsOfType(s, TestcaseTableLine.class, lines);

		for (Section<TestcaseTableLine> section : lines) {
			RatedTestCase rtc = (RatedTestCase) KnowWEUtils.getStoredObject(article, section,
					TestcaseTableLine.TESTCASE_KEY);

			if (rtc != null) {
				stc.add(rtc);
			}

		}

		KnowWEUtils.storeObject(article, s, TestcaseTable.TESTCASE_KEY, testcase);

		return null;
	}

}
