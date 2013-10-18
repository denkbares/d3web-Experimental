/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.timeline.export;

import java.io.IOException;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.testcase.model.TestCase;
import de.d3web.utils.Triple;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.testcases.TestCaseProvider;

/**
 * 
 * @author Tobias Bleifuss, Steffen Hoefner
 */
public class TimelineJSONExportAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String string = getContent(context);

		context.setContentType("application/json");
		context.setContentLength(string.length());

		context.getWriter().write(string);

	}

	private String getContent(UserActionContext context) throws IOException {
		Section<? extends TimelineType> section = getSection(context);
		if (section == null) {
			return "";
		}

		String testCaseName = context.getParameter("testCase");

		Triple<TestCaseProvider, Section<?>, Article> provider = getTestCaseProvider(
				section, testCaseName);
		if (provider == null) {
			return "";
		}
		TestCase testCase = provider.getA().getTestCase();
		KnowledgeBase kb = provider.getA().getActualSession(context)
				.getKnowledgeBase();
		
		return section.get().drawTimeline(section, provider, testCase, kb);
	}


	private Triple<TestCaseProvider, Section<?>, Article> getTestCaseProvider(
			Section<?> section,
			String testCaseName) {
		List<Triple<TestCaseProvider, Section<?>, Article>> providers = TimelineRenderer
				.getTestCaseProviders(section);

		for (Triple<TestCaseProvider, Section<?>, Article> triple : providers) {
			if (triple.getA().getTestCase() == null) continue;

			String id = triple.getC().getTitle() + "/"
					+ triple.getA().getName();

			if (id.equals(testCaseName)) return triple;
		}

		return null;
	}

	private Section<TimelineType> getSection(UserActionContext context)
			throws IOException {
		String sectionID = context.getParameter("kdomid");
		Section<TimelineType> section = Sections.getSection(sectionID, TimelineType.class);

		if (section == null) {
			context.sendError(409, "Section '" + sectionID
					+ "' could not be found, possibly because somebody else"
					+ " has edited the page.");
		}
		return section;
	}
}
