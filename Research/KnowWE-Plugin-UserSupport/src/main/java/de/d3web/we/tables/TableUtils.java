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
import java.util.Arrays;
import java.util.List;

import de.d3web.we.action.TableExportAction;
import de.d3web.we.renderer.TableRenderer;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;


/**
 * Utils class that offers static-methods for tables.
 * 
 * @author Johannes Dienst
 * @created 18.10.2011
 */
public class TableUtils {

	/**
	 * Returns the row of the table in which the current cell occurs.
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
	public static List<Section<TableCell>> getColumnCells(
			int columnNumber, Section<InnerTable> table) {
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

	/**
	 * Return the ITable with a given Section-Id.
	 * If not found null.
	 * Used in {@link TableExportAction}
	 * 
	 * @created 18.10.2011
	 * @param article
	 * @param id
	 * @return
	 */
	public static Section<ITable> getTableWithId(KnowWEArticle article, String id) {
		Section<KnowWEArticle> articleSection = article.getSection();

		List<Section<ITable>> tables = new ArrayList<Section<ITable>>();
		Sections.findSuccessorsOfType(articleSection, ITable.class, tables);

		for ( Section<ITable> table : tables ) {
			if (table.getID().equals(id) )
				return table;
		}

		return null;
	}

	public static int getWidestTableCellLength(List<Section<TableCell>> cells) {
		int max = 0;

		for (Section<TableCell> cell : cells)
			if (cell.getText().length() > max)
				max = cell.getText().length();

		return max;
	}

	public static int getWidestTableCellLengthPoiUtils(List<String[]> rowsList) {
		int max = 0;

		// Iterate over all rows and cells
		for (String[] row : rowsList)
			for (String cell : row)
				if (cell.length() > max)
					max = cell.length();

		return max;
	}

	public static String generateStringWithLength( int len, char fill ) {
		if ( len < 0 )
			return null;
		char[] cs = new char[ len ];
		Arrays.fill( cs, fill );
		return new String( cs );
	}

	/**
	 * 
	 * @created 04.11.2011
	 * @param toFill
	 * @param maxCellLength
	 * @return
	 */
	public static String generateFillString(String toFill, int maxCellLength) {
		if (toFill.length() < maxCellLength)
			return TableUtils.generateStringWithLength(
					Math.abs(toFill.length()-maxCellLength), ' ');
		return "";
	}

	/**
	 * Used to get an maximum cell count for rows of a table.
	 * Used in {@link TableRenderer}
	 * 
	 * @created 08.11.2011
	 * @param section
	 * @return
	 */
	public static int getMaximumTableCellCount(Section<InnerTable> section) {
		List<Section<TableLine>> tableLines =
				Sections.findSuccessorsOfType(section, TableLine.class);
		int maxCount = 0;
		for (Section<TableLine> line : tableLines) {
			if (Sections.findSuccessorsOfType(line, TableCell.class).size() > maxCount)
				maxCount = Sections.findSuccessorsOfType(line, TableCell.class).size();
		}

		return maxCount;
	}
}
