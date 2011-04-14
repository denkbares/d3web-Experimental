/*
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

package de.d3web.we.kdom.namespaces.rdf2go;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.rdf2go.Rdf2GoCore;

public class NamespacesContentRenderer extends KnowWEDomRenderer {

	@Override
	public void render(KnowWEArticle article, Section sec, UserContext user, StringBuilder string) {
		StringBuffer buffy = new StringBuffer();
		ResourceBundle rb = KnowWEEnvironment.getInstance()
				.getKwikiBundle(user);
		String content = sec.getOriginalText();
		HashMap<String, String> namespaces = Rdf2GoCore.getInstance().getNameSpaces();
		if (content.trim().length() > 0) {
			for (String line : content.split("\r\n|\r|\n")) {
				if (line.contains(":")) {
					int i = line.indexOf(":");
					String key = line.substring(0, i).trim();
					String val = line.substring(i + 1).trim();
					if (!namespaces.containsKey(key)) {
						Rdf2GoCore.getInstance().addNamespace(key, val);
					}
				}
			}
		}
		namespaces = Rdf2GoCore.getInstance().getNameSpaces();

		buffy.append("<div id='knoffice-panel' class='panel'>");
		buffy.append("<h3>" + rb.getString("KnowWE.Namespaces.Default.header")
				+ "</h3>");

		buffy.append("<table>");
		buffy.append("<tr><th>Custom Namespaces</th></tr>");
		for (Entry<String, String> cur : namespaces.entrySet()) {
			buffy.append("<tr><td>");
			buffy.append(cur.getKey() + "</td><td>" + cur.getValue());
			buffy.append("</td></tr>");
		}
		buffy.append("</table>");
		buffy.append("</div>");
		string.append(KnowWEUtils.maskHTML(buffy.toString()));
	}

}
