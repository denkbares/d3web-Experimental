/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import java.util.List;
import java.util.Map;

import de.d3web.core.session.Session;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;


/**
 * 
 * @author Florian Ziegler
 * @created 11.03.2011
 */
public class TestcaseTableLineRenderer extends KnowWEDomRenderer<TestcaseTableLine> {

	private final String testcaseExecuted = "testcaseExecuted";
	private final String testcaseSkipped = "testcaseSkipped";

	@Override
	public void render(KnowWEArticle article, Section<TestcaseTableLine> sec, UserContext user, StringBuilder string) {
		StringBuilder b = new StringBuilder();
		StringBuilder buffi = new StringBuilder();
		DelegateRenderer.getInstance().render(article, sec, user, b);

		// get execution status
		Session session = D3webUtils.getSession(sec.getTitle(), user, sec.getWeb());
		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(sec.getWeb());
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		Object o = sessionInfoStore.get(sec.getTitle());

		if (o != null) {
			buffi.append("<tr class=\"" + getStatus(sec, o) + "\">");
		}
		else {
			buffi.append("<tr>");
		}

		buffi.append(b.toString());
		buffi.append("\n</tr>");

		string.append(KnowWEUtils.maskHTML(buffi.toString()));
	}

	/**
	 * Returns the status as String which is then inserted as css class
	 */
	@SuppressWarnings("unchecked")
	private String getStatus(Section<TestcaseTableLine> line, Object sessionInfoStoreForArticle) {
		if (line == null || sessionInfoStoreForArticle == null) {
			return "";
		}
		Section<TimeStampType> timestamp = Sections.findSuccessor(line, TimeStampType.class);
		long time = TimeStampType.getTimeInMillis(timestamp);
		long currentMax = -1;

		if (sessionInfoStoreForArticle instanceof List) {
			if (((List) sessionInfoStoreForArticle).contains(line)) {
				return testcaseExecuted;
			}

			List list = (List) sessionInfoStoreForArticle;
			Section parentTable = Sections.findAncestorOfType(line, TestcaseTable.class);

			for (Object item : list) {
				if (item instanceof Section) {
					Section s = (Section) item;
					if (s.get() instanceof TestcaseTableLine) {
						Section p = Sections.findAncestorOfType(s, TestcaseTable.class);
						if (parentTable.equals(p)
								&& TimeStampType.getTimeInMillis((Section<TimeStampType>) item) > currentMax) {
							currentMax = TimeStampType.getTimeInMillis((Section<TimeStampType>) item);
						}
					}
				}
			}
		}

		if (time < currentMax) {
			return testcaseSkipped;
		}
		else {
			return "";
		}
	}

}