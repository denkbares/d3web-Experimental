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
package de.d3web.we.renderer;

import java.util.List;
import java.util.ResourceBundle;

import de.d3web.we.tables.InnerTable;
import de.d3web.we.tables.TableCell;
import de.d3web.we.tables.TableLine;
import de.d3web.we.tables.TableUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * For Rendering the Tables in HTML.
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class TableRenderer extends KnowWEDomRenderer<InnerTable> {

	private static ResourceBundle bundle = ResourceBundle.getBundle("Usersupport_messages");

	@Override
	public void render(KnowWEArticle article, Section<InnerTable> section,
			UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<span id='" + section.getID() + "'>"));

		StringBuilder buildi = new StringBuilder();
		buildi.append("<table border=\"1\">");

		// Get max cell size
		int maxCellLength =
				TableUtils.getWidestTableCellLength(
						Sections.findSuccessorsOfType(section,TableCell.class));

		// Get average cell count for line
		int averageCellCount = TableUtils.getMaximumTableCellCount(section);

		// Render the Header
		renderTableHeader(buildi, section, maxCellLength, averageCellCount, user);

		buildi.append("<tfoot></tfoot>");

		// Render the Body
		renderTableBody(buildi, section, maxCellLength, user);

		buildi.append("</table>");

		// Render import und export buttons
		renderExportImportButton(buildi, section);

		string.append(KnowWEUtils.maskHTML(buildi.toString()));

		string.append(KnowWEUtils.maskHTML("</span>"));

	}

	private static void renderTableHeader(
			StringBuilder buildi, Section<InnerTable> section, int maxCellLength, int averageCellCount,
			UserContext user) {

		Section<TableLine> sec = Sections.findChildOfType(section, TableLine.class);

		if (sec != null) {
			buildi.append("<thead>");
			buildi.append("<tr>");


			// Add a dummy cell, when the table contains one cell less then the rest lines
			if (averageCellCount > Sections.findSuccessorsOfType(sec, TableCell.class).size()) {
				buildi.append("<th>");
				buildi.append(TableUtils.generateStringWithLength(
						Math.abs(maxCellLength), ' '));
				buildi.append("</th>");
			}

			for (Section<TableCell> cell : Sections.findChildrenOfType(sec, TableCell.class)) {
				String cellText = cell.getText().trim();
				buildi.append("<th>");
				TableCell.INDIVIDUAL_RENDERER.render(cell.getArticle(), cell, user, buildi);
				//				buildi.append(cellText);
				buildi.append(TableUtils.generateStringWithLength(
						Math.abs(cellText.length()-maxCellLength), ' '));
				buildi.append("</th>");
			}

			buildi.append("</tr>");
			buildi.append("</thead>");
		}
	}

	private static void renderTableBody(
			StringBuilder buildi, Section<InnerTable> section, int maxCellLength,
			UserContext user) {
		buildi.append("<tbody>");

		List<Section<TableLine>> lines = Sections.findChildrenOfType(section, TableLine.class);

		// First line is header
		if ( lines.size() >= 2 ) {
			lines.remove(0);

			for (Section<TableLine> line : lines) {

				buildi.append("<tr>");

				for (Section<TableCell> cell : Sections.findChildrenOfType(line, TableCell.class)) {
					String cellText = cell.getText().trim();
					buildi.append("<td>");
					TableCell.INDIVIDUAL_RENDERER.render(cell.getArticle(), cell, user, buildi);
					//				buildi.append(cellText);
					buildi.append(TableUtils.generateStringWithLength(
							Math.abs(cellText.length()-maxCellLength), ' '));
					buildi.append("</td>");
				}

				buildi.append("</tr>");
			}
		}

		buildi.append("</tbody>");
	}

	private static void renderExportImportButton(StringBuilder buildi, Section<InnerTable> section) {

		String rel = "rel=\"{ " +
				"		objectId : '" + section.getID() + "',"
				+ "}\" ";
		//		+ "namespace : '" + java.net.URLEncoder.encode(namespace) + "',"
		//		+ KnowWEAttributes.WEB + ": '" + webname + "',"
		//		+ KnowWEAttributes.TOPIC + ": '" + topic + "',";

		String exportButton =
				"<div>" +
						"<input class=\"button table-export\" type=\"button\" name=\"Export\" " +
						"value=\"" + bundle.getString("export_button") + "\" id=\"" + section.getID()  + "-Export\"" + rel  + ">" +
						"<span id=\"export-download" + section.getID() + "\"> </span>" +
						"</div>"
						;

		//		String inputForm = "<form action=\"\" method=\"post\" enctype=\"multipart/form-data\">" +
		//				"<input name=\"import-textfield-" + section.getID() +"\" type=\"file\" size=\"50\" maxlength=\"100000\" accept=\"application/msexcel/*\">" +
		//				"</form>";
		//
		//		String importButton =
		//				"<div>" +
		//						"<input class=\"button table-import\" type=\"submit\" name=\"Import\"" +
		//						" value=\"Import\" id=\"" + section.getID()  + "-Import\"" + rel + ">" +
		//						"<span id=\"import-upload" + section.getID() + "\">" +
		//						inputForm +
		//						"</span>" +
		//						"</div>";

		String importButton = "<form id=\"file_upload_form\" action=\"uploadreceptor?" +
				"tableId=" + section.getID() +
				"&article=" + section.getTitle() + "\" " +
				"method=\"post\" enctype=\"multipart/form-data\" accept-charset=\"UTF-8\">" +
				"<P>" +
				bundle.getString("excel-upload") +
				"<P>" +
				"<INPUT type=\"file\" size=\"60\" name=\"excelcontent\">" +
				"<P>" +
				"<INPUT type=\"submit\" name=\"enter\" value=\"" +
				bundle.getString("upload_button") + "\">" +
				"</form>";

		buildi.append(exportButton + importButton);
	}
}
