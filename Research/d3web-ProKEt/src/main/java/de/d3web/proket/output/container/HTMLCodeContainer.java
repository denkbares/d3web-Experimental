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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Container for html code.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class HTMLCodeContainer implements ICodeContainer {

	// TODO maybe not needed anymore
	public static String putIntoDiv(String string) {
		return "<div>" + string + "</div>";
	}

	private boolean debug = false;
	public StringBuilder footText = new StringBuilder();
	public StringBuilder headText = new StringBuilder();

	private boolean isSelectOpen = false;
	public StringBuilder leftText = new StringBuilder();
	public StringBuilder mainText = new StringBuilder();

	// potentially opended tables are stored in a stack as to be able to append
	// always at the one open table the deepest in the hierarchy
	public Stack<String> openTables = new Stack<String>();
	public StringBuilder rightText = new StringBuilder();
	private Map<String, LinkedList<Integer>> rowspannings = new HashMap<String, LinkedList<Integer>>();
	private Map<String, Integer> tableCellCounter = new HashMap<String, Integer>();
	public Map<String, StringBuilder> tableContent = new HashMap<String, StringBuilder>();
	private Map<String, Integer> tableNumColumns = new HashMap<String, Integer>();
	private List<String> tableOpenCells = new LinkedList<String>();
	private Map<String, Integer> tableRowCellCounter = new HashMap<String, Integer>();
	private String title = "";

	@Override
	public void add(String data) {
		if (data == null)
			return;

		// no table open? then append to main text
		if (openTables.isEmpty()) {
			mainText.append(data);
		} else {
			// fill open table
			tableContent.get(openTables.peek()).append(data);
		}
	}

	public void addFoot(String data) {
		if (data == null)
			return;

		footText.append(data);
	}

	public void addHead(String data) {
		if (data == null)
			return;

		headText.append(data);
	}

	public void addLeft(String data) {
		if (data == null)
			return;

		leftText.append(data);
	}

	public void addRight(String data) {
		if (data == null)
			return;

		rightText.append(data);
	}

	@Override
	public String generateOutput() {
		// dummy
		return "";
	}

	public boolean generateOutput(String folder, String filename) {
		// dummy
		return false;
	}

	public int getNumOpenTables() {
		return tableNumColumns.size();
	}

	public String getTitle() {
		return title;
	}

	public void selectAddItem(String itemName) {
		if (isSelectOpen) {
			add("<option>" + itemName + "</option>");
		}
	}

	public void selectClose() {
		if (isSelectOpen) {
			add("</select>");
			isSelectOpen = false;
		}
	}

	public void selectOpen(String name, String height, String options) {
		if (!isSelectOpen) {
			isSelectOpen = true;
			add("<select name=\"" + name + "\" size=\"" + height + "\"");
			if (options != null) {
				add(" " + options);
			}
			add(">");
		}
	}

	public void setTitle(String title) {
		if (title != null) {
			this.title = title;
		} else {
			this.title = "";
		}
	}

	public void tableAddCell(String id, String data) {
		tableAddCell(id, data, 1, null);
	}

	public void tableAddCell(String id, String data, int columns,
			String tableAttributes) {
		// close if open
		tableCloseCell(id);
		// write my cell
		tableOpenCell(id);
		add(data);
		tableCloseCell(id);
	}

	public void tableCloseCell(String id) {
		// check
		if ((tableCellCounter.get(id) == null)
				|| (tableNumColumns.get(id) == null)) return;

		if (tableOpenCells.contains(id)) {
			add("</td>");

			int span = 0;
			if (rowspannings.get(id).size() > 0) {
				span = rowspannings.get(id).get(0);
			}
			if ((tableRowCellCounter.get(id) + span) >= tableNumColumns.get(id)) {
				add("</tr>");
				if (rowspannings.get(id).size() > 0) {
					rowspannings.get(id).pop();
				}
				tableRowCellCounter.put(id, 0);
			}
			tableOpenCells.remove(id);
		}
	}

	public void tableEnd(String id) {
		// close
		tableCloseCell(id);

		// check: close only if open
		if ((tableCellCounter.get(id) == null)
				|| (tableNumColumns.get(id) == null)) return;

		// if empty, don't add empty cells
		if (tableCellCounter.get(id) == 0) return;

		// fill row
		int span = 0;
		if (rowspannings.get(id).size() > 0) {
			span = rowspannings.get(id).get(0);
		}
		if (tableRowCellCounter.get(id) > 0) {
			for (int i = tableRowCellCounter.get(id) + span; i < tableNumColumns
					.get(id); i++) {
				tableAddCell(id, "&nbsp;"); // empty cell
			}
		}

		// close
		add("</table>");

		openTables.pop();
		// add table if not empty
		if (tableCellCounter.get(id) > 0) {
			if (openTables.isEmpty()) {
				mainText.append(tableContent.get(id));
			} else {
				// fill open tables
				tableContent.get(openTables.peek())
						.append(tableContent.get(id));
			}
		}

		if (debug) {
			System.out.println("closing table " + id);
		}

		// and kick data structures
		tableCellCounter.remove(id);
		tableNumColumns.remove(id);
		tableContent.remove(id);
	}

	public void tableOpenCell(String id) {
		tableOpenCell(id, 1, null, null, -1, -1);
	}

	public void tableOpenCell(String id, int columns, String tableAttributes) {
		tableOpenCell(id, columns, tableAttributes, null, -1, -1);
	}

	public void tableOpenCell(String id, int columns, String tableAttributes,
			String cellAttributes, int colspan, int rowspan) {
		// close
		tableCloseCell(id);
		if (debug) {
			System.out.println("opening cell " + id);
		}

		// open table if necessary
		if (tableCellCounter.get(id) == null) {
			tableStart(id, columns, tableAttributes);
			if (debug) {
				System.out.println("have to open table for " + id);
			}
		}

		// write
		if (tableRowCellCounter.get(id) == 0) {
			add("<tr>");
		}
		add("<td");
		if (debug) {
			add(" id=\"td-" + id + "\"");
		}
		if (cellAttributes != null) {
			add(" " + cellAttributes);
		} else {
			add(" width=\""
					+ String.format("%d", 100 / tableNumColumns.get(id))
					+ "%\"");
		}

		if (colspan == -1) {
			colspan = 1;
		}
		if (colspan > 1) {
			add(" colspan=\"" + colspan + "\"");
			tableCellCounter.put(id, tableCellCounter.get(id) + colspan);
		} else {
			tableCellCounter.put(id, tableCellCounter.get(id) + 1);
		}
		if (rowspan > 1) {
			add(" rowspan=\"" + rowspan + "\"");
			// remeber rowspans
			if (rowspannings.get(id).size() < 1) {
				rowspannings.get(id).add(0);
			}
			for (int i = 1; i < rowspan; i++) {
				if (rowspannings.get(id).size() < i + 1) {
					rowspannings.get(id).add(0);
				}
				// add
				rowspannings.get(id).set(i,
						rowspannings.get(id).get(i) + colspan);
			}
		}
		// count
		tableCellCounter.put(id, tableCellCounter.get(id) + colspan);
		tableRowCellCounter.put(id, tableRowCellCounter.get(id) + colspan);
		add(">");

		tableOpenCells.add(id);
	}

	public void tableStart(String id, int columns) {
		tableStart(id, columns, null);
	}

	public void tableStart(String id, int columns, String tableAttributes) {
		tableNumColumns.put(id, columns);
		tableCellCounter.put(id, 0);
		tableRowCellCounter.put(id, 0);
		rowspannings.put(id, new LinkedList<Integer>());
		tableContent.put(id, new StringBuilder());
		// ID auf den Stack pushen
		openTables.push(id);

		add("<table");
		if (debug) {
			add(" id=\"table-" + id + "\"");
		}
		if (tableAttributes != null) {
			add(" " + tableAttributes);
		}
		add(">");

	}

	@Override
	public String toString() {
		return mainText.toString();
	}

}
