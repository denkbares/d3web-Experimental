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
package de.knowwe.sessiondebugger;

import java.util.HashMap;

import de.d3web.core.utilities.Pair;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Volker Belli & Markus Friedrich (denkbares GmbH)
 * @created 20.02.2012
 */
public class TableModel {

	private HashMap<Pair<Integer, Integer>, String> cells = new HashMap<Pair<Integer, Integer>, String>();
	private HashMap<Integer, Integer> columnWidths = new HashMap<Integer, Integer>();
	private int rowCount = 0;
	private int columnCount = 0;

	private static String SIZE_SELECTOR_KEY = "size_selector";
	private static String FROM_KEY = "from_position";

	public void addCell(int row, int column, String value, int width) {
		rowCount = Math.max(rowCount, row);
		columnCount = Math.max(columnCount, column);
		cells.put(new Pair<Integer, Integer>(row, column), value);
		Integer actualWidth = columnWidths.get(column);
		if (actualWidth == null || actualWidth < width) {
			columnWidths.put(column, width);
		}
	}

	public int getWidth(int column) {
		Integer width = columnWidths.get(column);
		if (width == null) {
			return 0;
		}
		return width;
	}

	public String getCell(int row, int column) {
		String cell = cells.get(new Pair<Integer, Integer>(row, column));
		if (cell == null) {
			return "";
		}
		return cell;
	}

	/**
	 * <pre>
	 * {
	 * 			widths: [x, y, ...],
	 * 			cells: [
	 * 			        [...],
	 * 			        [...],
	 * 			       ],
	 * 		}
	 * </pre>
	 * 
	 * @created 20.02.2012
	 * @return
	 */
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("widths: [");
		for (int i = 0; i <= columnCount; i++) {
			sb.append(getWidth(i));
			if (i != columnCount) {
				sb.append(",");
			}
		}
		sb.append("],");
		sb.append("cells: [");
		for (int i = 0; i <= columnCount; i++) {
			sb.append("[");
			for (int j = 0; j <= rowCount; j++) {
				sb.append(getCell(i, j));
				if (j != columnCount) {
					sb.append(",");
				}
			}
			sb.append("]");
			if (i != columnCount) {
				sb.append(",");
			}
		}
		sb.append("],");
		return sb.toString();
	}

	public String toHtml(Section<?> section, UserContext user) {
		StringBuilder string = new StringBuilder();
		String sizeKey = SIZE_SELECTOR_KEY + "_" + section.getID();
		String selectedSizeString = (String) user.getSession().getAttribute(
				sizeKey);
		if (selectedSizeString == null) {
			selectedSizeString = "10";
		}
		int selectedSize = Integer.parseInt(selectedSizeString);
		String fromKey = FROM_KEY + "_" + section.getID();
		String fromString = (String) user.getSession().getAttribute(
				fromKey);
		int from = 1;
		if (fromString != null) {
			from = Integer.parseInt(fromString);
		}
		int to = from + selectedSize - 1;
		if (to > rowCount) {
			int tempfrom = from - (to - rowCount);
			if (tempfrom > 0) {
				from = tempfrom;
			}
			else {
				from = 1;
			}
			user.getSession().setAttribute(fromKey, String.valueOf(from));
			to = rowCount;
		}
		string.append(KnowWEUtils.maskHTML("<div style='overflow:auto'>"));
		string.append(KnowWEUtils.maskHTML("<table class='wikitable' border='1' width='100%'>"));
		string.append(KnowWEUtils.maskHTML("<thead>"));
		// headline
		string.append(KnowWEUtils.maskHTML("<tr>"));
		for (int i = 0; i <= columnCount; i++) {
			String cell = getCell(0, i);
			string.append(KnowWEUtils.maskHTML("<th>"));
			string.append(cell);
			string.append(KnowWEUtils.maskHTML("</th>"));
		}
		string.append(KnowWEUtils.maskHTML("</tr>\n"));
		string.append(KnowWEUtils.maskHTML("</thead>\n"));
		string.append(KnowWEUtils.maskHTML("<tbody>"));
		for (int i = from; i <= to; i++) {
			string.append(KnowWEUtils.maskHTML("<tr>"));
			for (int j = 0; j <= columnCount; j++) {
				string.append(KnowWEUtils.maskHTML("<td>"));
				string.append(getCell(i, j));
				string.append(KnowWEUtils.maskHTML("</td>"));
			}
			string.append(KnowWEUtils.maskHTML("</tr>\n"));
		}
		string.append(KnowWEUtils.maskHTML("</tbody>"));
		string.append(KnowWEUtils.maskHTML("</table>"));
		string.append(KnowWEUtils.maskHTML("</div>"));
		string.append(renderTableSizeSelector(section, user, sizeKey, selectedSize));
		string.append(renderNavigation(section, from, selectedSize, fromKey));
		return string.toString();
	}

	private String renderTableSizeSelector(Section<?> section, UserContext user, String key, int selectedSize) {
		StringBuilder builder = new StringBuilder();

		int[] sizeArray = new int[] {
				5, 10, 20, 50 };
		builder.append("<select id=sizeSelector"
				+ section.getID()
				+ " onchange=\"SessionDebugger.change('"
				+ key
							+ "', this.options[this.selectedIndex].value);\">");
		for (int size : sizeArray) {
			if (size == selectedSize) {
				builder.append("<option selected='selected' value='" + size + "'>"
						+ size + "</option>");
			}
			else {
				builder.append("<option value='" + size + "'>" + size
						+ "</option>");
			}
		}
		builder.append("</select>");
		return KnowWEUtils.maskHTML(builder.toString());
	}

	private Object renderNavigation(Section<?> section, int from, int selectedSize, String key) {
		StringBuilder builder = new StringBuilder();
		int previous = Math.max(1, from - selectedSize);
		int next = from + selectedSize;
		builder.append("<input type=\"button\" onclick=\"SessionDebugger.change('" + key
				+ "', " + 1 + ");\" value='start'>");
		builder.append("<input type=\"button\" onclick=\"SessionDebugger.change('" + key
				+ "', " + previous + ");\" value='previous'>");
		builder.append("<input type=\"button\" onclick=\"SessionDebugger.change('" + key
				+ "', " + next + ");\" value=\"next\">");
		builder.append("<input type=\"button\" onclick=\"SessionDebugger.change('" + key
				+ "', " + rowCount + ");\" value='end'>");
		return KnowWEUtils.maskHTML(builder.toString());
	}
}
