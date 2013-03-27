/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3webviz.diafluxCity;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;


/**
 * 
 * @author Reinhard Hatko
 * @created 14.03.2013
 */
public class DiaFluxCityRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, RenderResult bob) {

		bob.appendHtml("<div class=\"diafluxCity\">");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/city.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/scenejs.js\"></script>");
		bob.appendHtml("<script type=\"text/javascript\" src=\"KnowWEExtension/scripts/d3webviz.js\"></script>");
		// bob.appendHtml("<link href=\"KnowWEExtension/css/d3webviz.css\" type=\"text/css\" rel=\"stylesheet\">");
		bob.appendHtml("<canvas id=\"diafluxCity" + section.getID()
				+ "\" width=\"1000\" height=\"900\">");
		bob.appendHtml("</canvas>\n");

		bob.appendHtml("<script type=\"text/javascript\">KNOWWE.d3webViz.createDiaFluxCity(\""
				+ section.getID() + "\",'DiaFluxCityAction');</script>");
		bob.appendHtml("</div>\n");

	}

}
