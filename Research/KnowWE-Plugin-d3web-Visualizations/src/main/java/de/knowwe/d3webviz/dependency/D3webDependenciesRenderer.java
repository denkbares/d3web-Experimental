/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3webviz.dependency;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;


/**
 * 
 * @author Reinhard Hatko
 * @created 04.11.2012
 */
public class D3webDependenciesRenderer extends DefaultMarkupRenderer {

	@Override
	protected void renderContents(Section<?> section, UserContext user, RenderResult bob) {

		bob.appendHtml("<div class=\"d3webdependencies\">");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/d3.v2.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/dagre.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/d3webviz.js\"></script>");
		bob.appendHtml("<link href=\"KnowWEExtension/css/d3webviz.css\" type=\"text/css\" rel=\"stylesheet\">");
		bob.appendHtml("<div class=\"graph\">");
		bob.appendHtml("</div>\n");
		bob.appendHtml("<script type=\"text/javascript\">KNOWWE.d3webViz.createDependencyGraph(\""
				+ section.getID() + "\");</script>");
		bob.appendHtml("</div>\n");

	}
}
