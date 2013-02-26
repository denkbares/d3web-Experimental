/*
 * < * Copyright (C) 2009 Chair of Artificial Intelligence and Applied
 * Informatics Computer Science VI, University of Wuerzburg
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
package de.knowwe.kdom.namespaces.rdf2go;

import java.util.Map;
import java.util.Map.Entry;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ShowNamespacesType extends DefaultMarkupType {

	private static final String GLOBAL_ANNOTATION = "global";
	private static final String GLOBAL_TRUE = "true";
	private static final String MASTER_ANNOTATION = "master";

	private static DefaultMarkup MARKUP = null;

	static {
		MARKUP = new DefaultMarkup("ShowNamespaces");
		MARKUP.addAnnotation(GLOBAL_ANNOTATION, false, GLOBAL_TRUE, "false");
		MARKUP.addAnnotation(MASTER_ANNOTATION, false);
	}

	public ShowNamespacesType() {
		super(MARKUP);
		this.setRenderer(new NamespacesRenderer());
	}

	private static class NamespacesRenderer extends DefaultMarkupRenderer {

		@Override
		protected void renderContents(Section<?> section, UserContext user, RenderResult string) {

			String global = DefaultMarkupType.getAnnotation(section, GLOBAL_ANNOTATION);
			String master = DefaultMarkupType.getAnnotation(section, MASTER_ANNOTATION);
			Rdf2GoCore core;
			if (master == null || GLOBAL_TRUE.equals(global)) {
				core = Rdf2GoCore.getInstance();
			}
			else {
				core = Rdf2GoCore.getInstance(section.getWeb(), master);
			}

			Map<String, String> namespaces = core.getNameSpaces();

			string.appendHtml("<table>");
			string.appendHtml("<tr><th align='left'>Appreviations</th><th align='left'>Namespaces</th></tr>");
			for (Entry<String, String> cur : namespaces.entrySet()) {
				string.appendHtml("<tr><td>");
				string.append(cur.getKey());
				string.appendHtml("</td><td>");
				string.append(cur.getValue());
				string.appendHtml("</td></tr>");
			}
			string.appendHtml("</table>");

		}
	}

}
