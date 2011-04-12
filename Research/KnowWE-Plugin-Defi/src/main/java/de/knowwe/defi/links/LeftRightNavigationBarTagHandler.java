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
package de.knowwe.defi.links;

import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class LeftRightNavigationBarTagHandler extends AbstractTagHandler {

	public LeftRightNavigationBarTagHandler() {
		super("leftrightnavigation");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		String center = "no center data found";
		if (parameters.containsKey("center")) {
			center = parameters.get("center");
		}
		String left = null;
		if (parameters.containsKey("left")) {
			left = parameters.get("left");
		}
		String right = null;
		if (parameters.containsKey("right")) {
			right = parameters.get("right");
		}

		StringBuffer buffy = new StringBuffer();

		buffy.append(KnowWEUtils.maskHTML("<center>"));
		buffy.append(KnowWEUtils.maskHTML("<table>"));
		buffy.append(KnowWEUtils.maskHTML("<tr>"));
		buffy.append(KnowWEUtils.maskHTML("<td >"));
		if (left != null) {
		buffy.append(KnowWEUtils.maskHTML("<a href='Wiki.jsp?page="
				+ left
				+ "' ><img src='KnowWEExtension/images/Pfeil_nach_links.gif' height='60'/></a>"));
		buffy.append(KnowWEUtils.maskHTML("</td>"));
		buffy.append(KnowWEUtils.maskHTML("<td>"));
			// buffy.append(KnowWEUtils.maskHTML("<a href='Wiki.jsp?page=" +
			// left + "' >"));
			buffy.append(left);
			// buffy.append(KnowWEUtils.maskHTML("</a>"));
		}
		buffy.append(KnowWEUtils.maskHTML("</td>"));
		buffy.append(KnowWEUtils.maskHTML("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		buffy.append(center);
		buffy.append(KnowWEUtils.maskHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		buffy.append(KnowWEUtils.maskHTML("</td>"));
		buffy.append(KnowWEUtils.maskHTML("<td>"));
		if (right != null) {
			// buffy.append(KnowWEUtils.maskHTML("<a href='Wiki.jsp?page=" +
			// right + "' >"));
			buffy.append(right);
			// buffy.append(KnowWEUtils.maskHTML("</a>"));
		buffy.append(KnowWEUtils.maskHTML("</td>"));
		buffy.append(KnowWEUtils.maskHTML("<td>"));

		buffy.append(KnowWEUtils.maskHTML("<a href='Wiki.jsp?page="
				+ right
				+ "Wie sich Angst zeigt' > <img src='KnowWEExtension/images/Pfeil_nach_rechts.gif' height='60'/>  </a>"));
		}
		buffy.append(KnowWEUtils.maskHTML("</td>"));
		buffy.append(KnowWEUtils.maskHTML("</tr>"));
		buffy.append(KnowWEUtils.maskHTML("</table>"));
		buffy.append(KnowWEUtils.maskHTML("</center>"));

		return buffy.toString();
	}

}
