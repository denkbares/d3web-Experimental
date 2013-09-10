/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.onte.action;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.util.OntologyFormats;

/**
 *
 *
 * @author Stefan Mark
 * @created 16.10.2011
 */
public class ShowExportTabAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		StringBuilder html = new StringBuilder();
		html.append("<div id=\"onte-export-tab\" class=\"onte-box\">");
		html.append("<p>Export the current version of the local ontology</p>"

				+ "<div id='onte-options' class='onte-options'>"
				+ "    <label class='option'>"
				+ "        <p class='onte-option-label' style='float:left; display: block; width:100px;'>Syntax:</p>"
				+ getFileFormats()
				+ "    </label>"
				+ "    <label class='option'>"
				+ "        <p class='onte-option-label' style='float:left; display: block; width:100px;'>Filename:</p>"
				+ "        <input type='text' value='' name='onte-export-tab-filename' id='onte-export-tab-filename'>"
				+ "    </label>"
				+ "</div>"

				+ " <div class='onte-buttons onte-buttonbar'>"
				+ "    <a href='javascript:KNOWWE.plugin.onte.actions.exportOntology();void(0);' title='Export local ontology' class='left onte-button-txt'>Export</a>"
				+ " </div>");
		html.append("</div>");

		context.getWriter().write(html.toString());
	}

	/**
	 * Returns an HTML select list with the possible file formats for the
	 * export.
	 *
	 * @created 16.10.2011
	 * @return
	 */
	private String getFileFormats() {

		StringBuilder options = new StringBuilder();

		options.append("<select name=\"onte-export-tab-format\" id=\"onte-export-tab-format\">");
		OntologyFormats[] formats = OntologyFormats.values();
		for (OntologyFormats format : formats) {
			options.append("<option value='" + format.getFormat() + "'>"
					+ format.getTitle()
					+ "</option>");
		}
		options.append("</select>");
		return options.toString();
	}
}
