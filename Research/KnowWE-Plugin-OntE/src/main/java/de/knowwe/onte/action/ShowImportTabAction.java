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
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.manchester.compile.utils.ImportedOntologyManager;
import de.knowwe.kdom.manchester.frame.ImportFrame;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarUtils;

/**
 *
 *
 * @author Stefan Mark
 * @created 16.10.2011
 */
public class ShowImportTabAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		StringBuilder html = new StringBuilder();
		html.append("<p>Imported Ontologies:</p>");

		Map<IRI, Section<ImportFrame>> imports = ImportedOntologyManager.getInstance().getImportedOntologies();
		for (IRI iri : imports.keySet()) {

			Section<ImportFrame> section = imports.get(iri);

			html.append("<div id=\"onte-import-tab\" class=\"onte-box\" style=\"padding-bottom:3px;\">");

			html.append("<div style=\"float:right;\">")
					.append(ToolbarUtils.getButtonHTML(getDeleteButton(section)))
					.append("</div>");

			html.append("<p><strong>Document IRI: ");
			html.append(iri).append("</strong></p>");
			html.append("<p><small style=\"padding-left:10px;\">(<a href=\"")
					.append(section.get().getImportIRI(section).getOriginalText()).append(
							"\" title=\"Visit imported ontology\">")
					.append(section.get().getImportIRI(section)).append("</a>)</small></p>");
			html.append(OnteRenderingUtils.renderHyperlink(section, false));
			html.append("</div>");
		}

		html.append("<div class='onte-buttons onte-buttonbar'>"
				+ "    <a href='javascript:KNOWWE.plugin.onte.actions.importOntology();void(0);' title='Import HTTP ontology' class='left onte-button-txt'>Import</a>"
				+ " </div>");


		context.getWriter().write(html.toString());
	}

	private ToolbarButton getDeleteButton(Section<? extends AbstractType> section) {
		String image = "KnowWEExtension/images/onte/delete.png";
		String name = "Delete";
		String description = "Delete selected ontology!";
		String action = "KNOWWE.plugin.onte.actions.removeImportedOntology('"
											+ section.getID() + "')";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
