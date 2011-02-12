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

import de.d3web.we.core.KnowWERessourceLoader;
import de.d3web.we.kdom.InvalidKDOMSchemaModificationOperation;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.kdom.table.TableLine;

/**
 * @author Florian Ziegler
 */
public class TestcaseTable extends Table {

	public TestcaseTable() {
		super(new TestcaseTableAttributesProvider());
		KnowWERessourceLoader.getInstance().add("testcasetable.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("testcasetable.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);

		try {
			replaceChildType(new HeaderLine(), TableLine.class);
		}
		catch (InvalidKDOMSchemaModificationOperation e) {
			e.printStackTrace();
		}
		addChildType(new TestcaseTableLine());
	}

	@Override
	public boolean isSortable() {
		return true;
	}

	/**
	 * 
	 * @created 22.01.2011
	 * @param s
	 * @return
	 */
	public static Section<? extends HeaderCell> findHeaderCell(Section<?> s) {
		Section<TableLine> line = s.findAncestorOfType(TableLine.class);
		boolean found = false;
		int i = 0;
		for (Section<?> section : line.getChildren()) {

			if (s.equalsOrIsSuccessorOf(section)) {
				found = true;
				break;
			}

			i++;
		}

		if (!found) {
			System.out.println("no header cell for: " + s);
			return null;
		}

		Section<Table> table = line.findAncestorOfType(Table.class);
		Section<TableLine> headerline = table.findSuccessor(TableLine.class);
		Section<? extends HeaderCell> headerCell = (Section<? extends HeaderCell>) headerline.getChildren().get(
				i);
		return headerCell;
	}

}
