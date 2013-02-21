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

package de.knowwe.hermes.util;

import java.util.List;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.hermes.TimeEvent;

public class TimeLineEventRenderer {

/**
		 * Render to html.
		 * 
		 * @param te the TimelineEvent to be rendered
		 * @param maskHTMLTags signals, if '>', '<' and '"' should be masked.
		 * 
		 * @return the string
		 */
	public static void renderToHTML(TimeEvent te, boolean maskHTMLTags, RenderResult result) {
		// return cached result, if possible
		// if (te.getRenderedOutput() != null) return te.getRenderedOutput();

		String styleTag = "";

		if (te.getImportance() == 1) {
			styleTag = " style=\"color: #AA2244; ";
		}
		else if (te.getImportance() == 2) {
			styleTag = " style=\"color: #BBAA00; ";
		}
		else if (te.getImportance() == 3) {
			styleTag = " style=\"color: #338888; ";
		}

		styleTag += " background-color: transparent;\"";

		// if no renderedString is cached, render now
		StringBuffer sb = new StringBuffer("<div class='panel'>\n");

		String encodedKDOMID = te.getTextOriginNode();
		// try {
		// encodedKDOMID = URLEncoder.encode(te.getTextOriginNode(), "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// +"&highlight="+encodedKDOMID
		// +"#"+encodedKDOMID

		sb.append("<h3 "
				+ styleTag
				+ "><i>"
				+ te.getTime().getDescription()
				+ "</i> - "
				+ te.getTitle()
				+ " <a class=\"wikipage\" href=\"Wiki.jsp?page="
				+ te.getTopic()
				+ "&highlight="
				+ encodedKDOMID
				+ "#"
				+ encodedKDOMID
				+ "\"><img src=\"./KnowWEExtension/images/hermes/page_go.png\" alt=\"Zum Wiki\" title=\"Zum Wiki\"/></a>"
				+ " <a class=\"wikipage\" href=\"Wiki.jsp?page="
				+ te.getTopic()
				+ "&edit="
				+ encodedKDOMID
				+ "#"
				+ encodedKDOMID
				+ "\"><img src=\"./KnowWEExtension/images/hermes/page_edit.png\" alt=\"Bearbeiten\" title=\"Bearbeiten\"/></a>"
				+ "</h3>");

		sb.append("\n<div>" + te.getDescription() + "<br>");
		List<String> sources = te.getSources();
		if (sources != null) {
			if (sources.size() == 1) {
				sb.append("<b>Quelle:</b><br>");
			}
			else if (sources.size() > 1) {
				sb.append("<b>Quellen:</b><br> ");
			}
			for (String aSource : sources) {
				sb.append(aSource + "<br>");
			}
		}
		//
		// sb.append("<br>textOrigin:" + te.getTopic() + "<br>");
		// sb.append("<br>textOrigin:" + te.getTextOriginNode() + "<br>");

		sb.append("</div>\n</div>\n");

		if (maskHTMLTags) {
			result.appendHTML(sb.toString());
		}
		else {
			result.append(sb.toString());
		}
	}
}