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
package de.d3web.we.tables;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;


/**
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableUtils {

	/**
	 * Returns the row of the table in which the current cell occurs.
	 * TODO Header counts as TableLine
	 * 
	 * @param s current section
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static int getRowNumber(Section<? extends TableCell> s) {
		Section<TableLine> tableLine = Sections.findAncestorOfType(s, TableLine.class);

		Section<ITable> table = (Section<ITable>) tableLine.getFather();
		List<Section<TableLine>> lines = Sections.findChildrenOfType(table, TableLine.class);

		return lines.indexOf(tableLine);
	}

	/**
	 * Returns the column of the table in which the current cell occurs.
	 * 
	 * @param s current section
	 * @return
	 */
	public static int getColumnNumber(Section<? extends TableCell> s) {
		Section<TableLine> tableLine = Sections.findAncestorOfType(s, TableLine.class);

		List<Section<TableCell>> cells = Sections.findSuccessorsOfType(tableLine, TableCell.class);
		return cells.indexOf(s);
	}

	/**
	 * The row number of the given table line.
	 * TODO Needed?
	 * 
	 * @created 16.03.2011
	 * @param s
	 * @return
	 */
	public static int getRowNumberOfLine(Section<? extends TableLine> s) {
		return Sections.findAncestorOfType(s, ITable.class).getChildren().indexOf(s);
	}

	/**
	 * Gets all TableCells for a given column. Columns numeration starts at 0.
	 * It includes the TableHeader.
	 * 
	 * @created 18.10.2011
	 * @param rowNumber
	 * @return
	 */
	public static List<Section<TableCell>> getColumnCells(int columnNumber, Section<ITable> table) {
		List<Section<TableCell>> cells = new ArrayList<Section<TableCell>>();

		// get all TableLines
		List<Section<TableLine>> lines = Sections.findSuccessorsOfType(table, TableLine.class);

		for (Section<TableLine> line : lines) {
			cells.add(
					Sections.findSuccessorsOfType(line, TableCell.class).
					get(columnNumber));
		}

		return cells;
	}

	/**
	 * Gets all TableCells for a given rowNumber.
	 * If you want the header use {@see getHeaderCells}.
	 * If you call this method with 0 as argument, it will
	 * return null.
	 * 
	 * @created 18.10.2011
	 * @param row
	 * @param table
	 * @return
	 */
	public static List<Section<TableCell>> getRowCells(int row, Section<ITable> table) {

		// no header return
		if (row == 0) return null;

		List<Section<TableCell>> cells = new ArrayList<Section<TableCell>>();

		// get all TableLines
		List<Section<TableLine>> lines = Sections.findSuccessorsOfType(table, TableLine.class);

		cells.addAll(Sections.findSuccessorsOfType(lines.get(row), TableCell.class));

		return cells;
	}
}
