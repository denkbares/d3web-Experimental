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

import de.d3web.we.tables.TableCell;
import de.d3web.we.tables.TableLine;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
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
public class TableRenderer extends KnowWEDomRenderer<Type> {

	@Override
	public void render(KnowWEArticle article, Section<Type> section,
			UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<span id='" + section.getID() + "'>"));

		StringBuilder buildi = new StringBuilder();
		buildi.append("<table border=\"1\">");

		// Render the Header
		renderTableHeader(buildi, section);

		buildi.append("<tfoot></tfoot>");

		// Render the Body
		renderTableBody(buildi, section);

		buildi.append("</table>");

		string.append(KnowWEUtils.maskHTML(buildi.toString()));

		string.append(KnowWEUtils.maskHTML("</span>"));

	}

	private static void renderTableHeader(StringBuilder buildi, Section<Type> section) {
		Section<TableLine> sec = Sections.findChildOfType(section, TableLine.class);
		if (sec != null) {
			buildi.append("<thead>");
			buildi.append("<tr>");

			for (Section<TableCell> cell : Sections.findChildrenOfType(sec, TableCell.class)) {
				buildi.append("<th>");
				buildi.append(cell.getText());
				buildi.append("</th>");
			}

			buildi.append("</tr>");
			buildi.append("</thead>");
		}
	}

	private static void renderTableBody(StringBuilder buildi, Section<Type> section) {
		buildi.append("<tbody>");

		List<Section<TableLine>> lines = Sections.findChildrenOfType(section, TableLine.class);

		// First line is header
		if ( lines.size() >= 2 ) {
			lines.remove(0);

			for (Section<TableLine> line : lines) {

				buildi.append("<tr>");

				for (Section<TableCell> cell : Sections.findChildrenOfType(line, TableCell.class)) {
					buildi.append("<td>");
					buildi.append(cell.getText());
					buildi.append("</td>");
				}

				buildi.append("</tr>");
			}
		}

		buildi.append("</tbody>");
	}
}
