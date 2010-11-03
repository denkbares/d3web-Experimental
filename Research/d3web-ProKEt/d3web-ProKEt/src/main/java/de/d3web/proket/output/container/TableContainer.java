/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.output.container;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal table representation.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
class Table {
	public int columns = 0;
	public int elements = 0;
}

/**
 * Manages HTML tables and multicolumn layout options.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class TableContainer {

	Map<String, Table> tables = new HashMap<String, Table>();

	public void addNextCell(String id) {
		addNextCell(id, 1);
	}


	public void addNextCell(String id, int colspan) {
		if (tables.get(id) == null)
			return;
		colspan = limitColspan(id, colspan);

		// set tables-element value at table.elements plus colspan
		tables.get(id).elements = tables.get(id).elements + colspan;
	}

	/**
	 * Closes the complete table of element id
	 * 
	 * @param id
	 * @return table opening code.
	 */
	public String closeTable(String id) {
		if (!isTableOpen(id))
			return "";

		Table t = tables.get(id);
		tables.remove(id);

		if (t.elements % t.columns < (t.columns - 1)) return "</tr></table>";

		return "</table>";
	}

	/**
	 * Get opening String of the next row
	 * 
	 * @created 09.10.2010
	 * @param id
	 * @param colspan
	 * @return
	 */
	public String getCellNextRowOpeningString(String id, int colspan) {
		StringBuilder sb = new StringBuilder();
		Table table = tables.get(id);

		// finish current row
		for (int i = table.elements % table.columns; i < table.columns; i++) {
			sb.append(getNextCellOpeningString(id)).append(
					getNextCellClosingString(id));

			// add table cell to table into table container
			addNextCell(id);
		}
		// create new table cell
		sb.append(getNextCellOpeningString(id, colspan));

		return sb.toString();
	}

	/**
	 * Get the table data/row closing String, set default colspan of 1
	 * 
	 * @created 09.10.2010
	 * @param id
	 * @return
	 */
	public String getNextCellClosingString(String id) {
		return getNextCellClosingString(id, 1);
	}

	/**
	 * Get table data/row closing String with a give colspan
	 * 
	 * @created 09.10.2010
	 * @param id of the object of the table
	 * @param colspan the colspan
	 * @return the String
	 */
	public String getNextCellClosingString(String id, int colspan) {

		// if element with id id is not an open table, return
		if (!isTableOpen(id)) return "";

		// get the maximum possible colspan value
		colspan = limitColspan(id, colspan);

		// generate the string
		Table t = tables.get(id);

		// if elements + colspan divided by number of columns is zero, all
		// elements are distributed equally over the table, so close td and tr
		if ((t.elements + colspan) % t.columns == 0) return "</td></tr>";

		// else just return a closing tabledata
		return "</td>";
	}

	/**
	 * If table opening string is asked only for an id, assume default colspan 1
	 * 
	 * @created 09.10.2010
	 * @param id of the object that needs a table opening
	 * @return String the table opening String
	 */
	public String getNextCellOpeningString(String id) {
		return getNextCellOpeningString(id, 1);
	}

	/**
	 * Get table opening String for an id of an object and for a given colspan
	 * 
	 * @created 09.10.2010
	 * @param id of the object needing a table
	 * @param colspan the desired colspan
	 * @return String the table opening String
	 */
	public String getNextCellOpeningString(String id, int colspan) {

		// if the table is not yet contained in this container
		if (!isTableOpen(id)) return "";

		// get max possible colspan
		colspan = limitColspan(id, colspan);

		StringBuilder result = new StringBuilder();

		// generate the table String
		Table t = tables.get(id);
		
		// formats the String as a Float with NONE after-comma values, followed by %
		String widthString = String.format("%.0f%%", 100.0 / t.columns * colspan);
		
		// if current elments modulo columns is 0, table is equally filled and a
		// new row needs to be opened
		if (t.elements % t.columns == 0) {
			result.append(MessageFormat.format("<tr><td width=\"{0}\"", widthString));

			// otherwise just append one new table data
		} else {
			result.append(MessageFormat.format("<td width=\"{0}\"", widthString));
		}

		// if larger colspan is desired, add colspan val
		if (colspan > 1) {
			result.append(" colspan=\"" + colspan + "\"");
		}

		// close table data/row tag and return
		result.append(">");
		return result.toString();
	}

	/**
	 * Table is open when questioned id is not null and when id is contained in
	 * the tables HashMap
	 */
	private boolean isTableOpen(String id) {
		return id != null && tables.containsKey(id);
	}

	/**
	 * Limits the column span of a table to ensure, that nothing is spanned over
	 * the limits of a table with fiven number of cols.
	 * 
	 * @created 09.10.2010
	 * @param id id of the table element
	 * @param colspan the desired colspan
	 * @return the maximum possible colspan
	 */
	private int limitColspan(String id, int colspan) {
		Table t = tables.get(id);

		// maximum left span: number of elements modulo number of columns is
		// what is left to be displayed in the "last" row; take number of
		// cols and subtract those elments. This is left for maximum colspan
		int spanMax = t.columns - t.elements % t.columns;

		// return the minimum value of both
		return Math.min(Math.abs(colspan), spanMax);
	}

	/**
	 * Returns a String representing the opening of a multicolumn table.
	 * 
	 * @param id
	 * @param columns
	 * @return table opening code.
	 */
	public String openTable(String id, int columns) {
		if (isTableOpen(id))
			return "";

		Table t = new Table();
		t.columns = columns;
		tables.put(id, t);

		return "<table class=\"multicol\">";
	}
}
