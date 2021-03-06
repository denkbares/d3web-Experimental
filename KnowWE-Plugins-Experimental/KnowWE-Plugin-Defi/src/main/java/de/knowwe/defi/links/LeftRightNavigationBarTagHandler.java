/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.links;

import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

public class LeftRightNavigationBarTagHandler extends AbstractTagHandler {

	public LeftRightNavigationBarTagHandler() {
		super("leftrightnavigation");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		String center = null;
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

		RenderResult buffy = new RenderResult(result);

		buffy.appendHtml("<center>");
		buffy.appendHtml("<table>");
		buffy.appendHtml("<tr>");
		if (left != null) {
			buffy.appendHtml("<td >");

			buffy.appendHtml("<a href='Wiki.jsp?page="
					+ extractPageName(left)
					+ "' ><img src='KnowWEExtension/images/Pfeil_nach_links.gif' height='60'/></a>");
			buffy.appendHtml("</td>");
			buffy.appendHtml("<td>");
			buffy.append(left);

			buffy.appendHtml("</td>");
		}
		if (center != null) {
			buffy.appendHtml("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buffy.append(center);
			buffy.appendHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			buffy.appendHtml("</td>");
		}

		if (right != null) {
			buffy.appendHtml("<td>");
			buffy.append(right);
			buffy.appendHtml("</td>");
			buffy.appendHtml("<td>");
			buffy.appendHtml("<a href='Wiki.jsp?page="
					+ extractPageName(right)
					+ "' > <img src='KnowWEExtension/images/Pfeil_nach_rechts.gif' height='60'/>  </a>");
		}
		buffy.appendHtml("</td>");
		buffy.appendHtml("</tr>");
		buffy.appendHtml("</table>");
		buffy.appendHtml("</center>");

		result.append(buffy);
	}

	private String extractPageName(String left) {
		String text = left.trim();
		if (text.startsWith("[") && text.endsWith("]")) {
			text = text.substring(1, text.length() - 1);
			if (text.contains("|")) {
				String[] split = text.split("\\|");
				text = split[1];
			}
		}
		return text.trim();
	}

}
