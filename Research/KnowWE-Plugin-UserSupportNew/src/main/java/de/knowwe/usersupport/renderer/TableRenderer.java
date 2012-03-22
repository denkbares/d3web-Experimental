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
package de.knowwe.usersupport.renderer;

import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.usersupport.tables.InnerTable;
import de.knowwe.usersupport.tables.TableCell;
import de.knowwe.usersupport.tables.TableCellFirstColumn;
import de.knowwe.usersupport.tables.TableHeaderCell;
import de.knowwe.usersupport.tables.TableHeaderLine;
import de.knowwe.usersupport.tables.TableLine;
import de.knowwe.usersupport.tables.TableNormalCell;
import de.knowwe.usersupport.tables.TableUtils;

/**
 * For Rendering the Tables to HTML.
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class TableRenderer implements Renderer {

	private static ResourceBundle bundle = ResourceBundle.getBundle("Usersupport_messages");

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string) {
		string.append(KnowWEUtils.maskHTML("<span id='" + section.getID() + "'>"));

		StringBuilder buildi = new StringBuilder();
		buildi.append("<table border=\"1\">");

		int maxCellLength =
				TableUtils.getWidestTableCellLength(
						Sections.findSuccessorsOfType(section, TableCell.class));

		int averageCellCount = TableUtils.getMaximumTableCellCount(section);

		renderTableHeader(buildi, section, maxCellLength, averageCellCount, user);

		buildi.append("<tfoot></tfoot>");

		renderTableBody(buildi, section, maxCellLength, user);

		buildi.append("</table>");

		string.append(KnowWEUtils.maskHTML("</span>"));

		string.append(KnowWEUtils.maskHTML(buildi.toString()));
	}

	private static void renderTableHeader(
			StringBuilder buildi, Section<?> section, int maxCellLength, int averageCellCount,
			UserContext user) {

		Section<TableHeaderLine> sec = Sections.findChildOfType(section, TableHeaderLine.class);

		if (sec != null) {
			buildi.append("<thead>");
			buildi.append("<tr>");

			// Add a dummy cell, when the table contains one cell less then the
			// rest lines
			if (averageCellCount > Sections.findSuccessorsOfType(sec, TableHeaderCell.class).size()) {
				buildi.append("<th>");
				buildi.append(TableUtils.generateStringWithLength(
						Math.abs(maxCellLength), ' '));
				buildi.append("</th>");
			}

			for (Section<TableHeaderCell> cell : Sections.findChildrenOfType(sec,
					TableHeaderCell.class)) {
				String cellText = cell.getText().trim();
				buildi.append("<th>");
				TableHeaderCell.INDIVIDUAL_RENDERER.render(cell, user, buildi);
				// buildi.append(cellText);
				buildi.append(TableUtils.generateStringWithLength(
						Math.abs(cellText.length() - maxCellLength), ' '));
				buildi.append("</th>");
			}

			buildi.append("</tr>");
			buildi.append("</thead>");
		}
	}

	private static void renderTableBody(
			StringBuilder buildi, Section<?> section, int maxCellLength,
			UserContext user) {
		buildi.append("<tbody>");

		List<Section<TableLine>> lines = Sections.findChildrenOfType(section, TableLine.class);

		// First line is header
		for (Section<TableLine> line : lines) {

			buildi.append("<tr>");

			// First Column is to delegate to children
			Section<TableCellFirstColumn> firstColumn = Sections.findChildOfType(line,
					TableCellFirstColumn.class);
			if (firstColumn != null) {
				buildi.append("<td>");
				TableCellFirstColumn.INDIVIDUAL_RENDERER.render(firstColumn, user, buildi);
				buildi.append("</td>");
			}

			for (Section<TableNormalCell> cell : Sections.findChildrenOfType(line,
					TableNormalCell.class)) {
				String cellText = cell.getText().trim();

				buildi.append("<td>");
				TableNormalCell.INDIVIDUAL_RENDERER.render(cell, user, buildi);
				// buildi.append(cellText);
				buildi.append(TableUtils.generateStringWithLength(
						Math.abs(cellText.length() - maxCellLength), ' '));
				buildi.append("</td>");
			}

			buildi.append("</tr>");
		}

		buildi.append("</tbody>");
	}

	public static void renderExportImportButton(StringBuilder buildi, Section<InnerTable> section) {

		String rel = "rel=\"{ " +
				"		objectId : '" + section.getID() + "',"
				+ "}\" ";
		// + "namespace : '" + java.net.URLEncoder.encode(namespace) + "',"
		// + Attributes.WEB + ": '" + webname + "',"
		// + Attributes.TOPIC + ": '" + topic + "',";

		String exportButton =
				"<div class=\"table_export_div\">" +
						"<input class=\"button table-export\" type=\"button\" name=\"Export\" " +
						"value=\"" + bundle.getString("export_button") + "\" id=\""
						+ section.getID() + "-Export\"" + rel + ">" +
						"<span class=\"table_export_result\" id=\"export-download"
						+ section.getID() + "\"> </span>" +
						"</div>";

		// String inputForm =
		// "<form action=\"\" method=\"post\" enctype=\"multipart/form-data\">"
		// +
		// "<input name=\"import-textfield-" + section.getID()
		// +"\" type=\"file\" size=\"50\" maxlength=\"100000\" accept=\"application/msexcel/*\">"
		// +
		// "</form>";
		//
		// String importButton =
		// "<div>" +
		// "<input class=\"button table-import\" type=\"submit\" name=\"Import\""
		// +
		// " value=\"Import\" id=\"" + section.getID() + "-Import\"" + rel + ">"
		// +
		// "<span id=\"import-upload" + section.getID() + "\">" +
		// inputForm +
		// "</span>" +
		// "</div>";

		String importButton = "<form class=\"file_upload_form\" id=\"file_upload_form\" action=\"uploadreceptor?"
				+
				"tableId=" + section.getID() +
				"&article=" + section.getTitle() +
				"&filetype=excel" + "\" " +
				"method=\"post\" enctype=\"multipart/form-data\" accept-charset=\"UTF-8\">" +
				"<P>" +
				bundle.getString("excel-upload") +
				"<P>" +
				"<INPUT type=\"file\" size=\"60\" name=\"excelcontent\">" +
				"<P>" +
				"<INPUT type=\"submit\" name=\"enter\" value=\"" +
				bundle.getString("upload_button") + "\">" +
				"</form>";

		StringBuilder buttons = new StringBuilder();
		buttons.append("<div class=\"table_export_frame\">");
		buttons.append("<div class=\"defaultMarkup\">");
		buttons.append(exportButton + importButton);
		buttons.append("</div>");
		buttons.append("</div>");

		buildi.append(buttons);
	}
}
