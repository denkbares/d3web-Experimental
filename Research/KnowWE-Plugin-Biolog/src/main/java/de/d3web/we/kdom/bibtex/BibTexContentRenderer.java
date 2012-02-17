/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.we.kdom.bibtex;

import java.util.HashMap;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexToplevelComment;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * A renderer for rendering the bibtex sections. @see BibTexContent
 * 
 * @author Jochen
 * @created 16.09.2010
 */
public class BibTexContentRenderer implements KnowWERenderer {

	@Override
	public void render(Section sec, UserContext user, StringBuilder string) {
		String parseerror = (String) KnowWEUtils.getStoredObject(sec,
				BibTexContent.PARSEEXCEPTION);
		String ioerror = (String) KnowWEUtils.getStoredObject(sec,
				BibTexContent.IOEXCEPTION);
		BibtexFile bibtexs = (BibtexFile) KnowWEUtils.getStoredObject(sec,
				BibTexContent.BIBTEXs);

		if (parseerror != null && parseerror.length() > 0) {
			string.append(KnowWEUtils.maskHTML("<p class\"=box error\">"));
			string.append(KnowWEUtils.maskHTML(parseerror.replaceAll("\n",
					"<br />")));
			string.append(KnowWEUtils.maskHTML("</p>"));
		}
		else if (ioerror != null && ioerror.length() > 0) {
			string.append(KnowWEUtils.maskHTML("<p class\"=box error\">"));
			string.append(KnowWEUtils.maskHTML(ioerror.replaceAll("\n",
					"<br />")));
			string.append(KnowWEUtils.maskHTML("</p>"));
		}
		else if (bibtexs == null) {
			string.append(KnowWEUtils.maskHTML("<p class\"=box error\">"));
			string.append("bibtex is null");
			string.append(KnowWEUtils.maskHTML("</p>"));
		}
		else {
			String header = "<div id='knoffice-panel' class='panel'>";
			header += "<h3>" + "BibTex" + "</h3>";
			header += ("<p id='knoffice-show-extend' class='show-extend pointer extend-panel-right'>"
					+ "BibTex entries</p>");
			header += ("<div id='knoffice-panel-extend' class='hidden' style='display:inline'>");
			String dl = "<p> (download all <a href=\"BibTexDownload.jsp?nodeID="
					+ sec.getID()
					+ "&"
					+ KnowWEAttributes.TOPIC
					+ "="
					+ sec.getTitle()
					+ "&filename=bibtex.bib\"><img width=16 height=16 title=\"Bibtex download\" src=\"KnowWEExtension/images/bibtex.png\" /></a>)"
					+ "</p>";
			String footer = "</div></div>";
			string.append(KnowWEUtils.maskHTML(header));
			string.append(KnowWEUtils.maskHTML(dl));
			string.append(KnowWEUtils.maskHTML("<p>\n"));
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("nodeID", sec.getID());
			params.put("topic", sec.getTitle());
			for (Object cur : bibtexs.getEntries()) {
				if (cur instanceof BibtexEntry) {
					string.append(KnowWEUtils.maskHTML((String) BibTexRenderManager
							.getInstance().render(cur, RenderingFormat.HTML, params)));
				}
				else if (cur instanceof BibtexToplevelComment) {
					string.append(KnowWEUtils.maskHTML("<p class=\"box error\"> Non parsable content: "
							+ ((BibtexToplevelComment) cur).getContent() + "</p>"));
				}
			}
			string.append(KnowWEUtils.maskHTML("</p>\n"));
			string.append(KnowWEUtils.maskHTML(footer));

		}

	}

}
