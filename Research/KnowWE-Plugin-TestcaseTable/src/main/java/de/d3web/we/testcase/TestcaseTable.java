/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.core.session.Session;
import de.d3web.we.basic.WikiEnvironment;
import de.d3web.we.basic.WikiEnvironmentManager;
import de.knowwe.core.KnowWERessourceLoader;
import de.knowwe.core.kdom.InvalidKDOMSchemaModificationOperation;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.table.Table;
import de.knowwe.kdom.table.TableLine;

/**
 * @author Florian Ziegler
 */
public class TestcaseTable extends Table {

	public static final String TESTCASE_INFOSTORE_KEY = "testcasetable";
	public static final String TESTCASE_KEY = "TESTCASE";

	public TestcaseTable() {
		super(new TestcaseTableAttributesProvider());
		KnowWERessourceLoader.getInstance().add("testcasetable.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("testcasetable.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);
		KnowWERessourceLoader.getInstance().add("KnowWE-Plugin-Shared.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);

		try {
			replaceChildType(new HeaderLine(), TableLine.class);
		}
		catch (InvalidKDOMSchemaModificationOperation e) {
			e.printStackTrace();
		}
		addChildType(new TestcaseTableLine());

		addSubtreeHandler(new TestcaseTableSubtreeHandler());
	}

	@Override
	public boolean isSortable() {
		return false;
	}

	public static List<Section<TestcaseTableLine>> getExecutedLinesOfTable(Section<TestcaseTableType> table, UserContext context, Session session) {
		String web = context.getWeb();

		// TODO hotfix: web is null on startup
		if (web == null) {
			web = "default_web";
		}

		WikiEnvironment wiki = WikiEnvironmentManager.getInstance().getEnvironments(web);
		Map<String, Object> sessionInfoStore = wiki.getSessionInfoStore(session);
		Map<String, List<Section<TestcaseTableLine>>> tableExecutions = (Map<String, List<Section<TestcaseTableLine>>>) sessionInfoStore.get(TestcaseTable.TESTCASE_INFOSTORE_KEY);
		if (tableExecutions == null) {
			tableExecutions = new HashMap<String, List<Section<TestcaseTableLine>>>();
			sessionInfoStore.put(TestcaseTable.TESTCASE_INFOSTORE_KEY, tableExecutions);
		}

		List<Section<TestcaseTableLine>> list = tableExecutions.get(table.getID());
		if (list == null) {
			list = new LinkedList<Section<TestcaseTableLine>>();
			tableExecutions.put(table.getID(), list);
		}
		return list;

	}

	/**
	 * 
	 * @created 22.01.2011
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Section<? extends HeaderCell> findHeaderCell(Section<?> s) {
		Section<TableLine> line = Sections.findAncestorOfType(s, TableLine.class);
		int i = 0;
		for (Section<?> section : line.getChildren()) {

			if (s.equalsOrIsSuccessorOf(section)) {
				break;
			}

			i++;
		}

		Section<Table> table = Sections.findAncestorOfType(line, Table.class);
		Section<TableLine> hLine = Sections.findSuccessor(table, TableLine.class);

		if (i >= hLine.getChildren().size()) {
			Logger.getLogger(TestcaseTable.class.getName()).warning("no header cell for: " + s);
			return null;
		}

		Section<? extends HeaderCell> hCell = (Section<? extends HeaderCell>) hLine.getChildren().get(
				i);
		return hCell;
	}
}
