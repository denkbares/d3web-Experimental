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

package de.knowwe.hermes.taghandler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

public class VersionCountTagHandler extends AbstractHTMLTagHandler {

	public VersionCountTagHandler() {
		super("versionCounts");
	}

	@Override
	public void renderHTML(String web, String topic,
			UserContext user, Map<String, String> values, RenderResult result) {
		Environment env = Environment.getInstance();

		String resultString = "<div class=\"versionCounts\">";
		resultString += "<div class=\"sortable\">";
		resultString += "<table class=\"wikitable\" border=\"1\">";
		resultString += "<tbody>";

		resultString += "<tr>";
		resultString += "<th class=\"sort\" > Seitenname </th>";
		resultString += "<th class=\"sort\" > Editierungen </th>";
		resultString += "</tr>";

		// result += "<table>";
		// result += "<th><td>pagename</td><td>versionCount</td></th>";
		Map<String, Integer> versionsMap = new TreeMap<String, Integer>();
		for (String title : env.getArticleManager(web).getTitles()) {
			versionsMap.put(title, env.getWikiConnector().getVersionCount(title));
		}
		for (Entry<String, Integer> e : versionsMap.entrySet()) {
			resultString += "<tr><td>" + e.getKey() + "</td><td>" + e.getValue()
					+ "</td></tr>";
		}
		// result += "</table>";

		resultString += "</tbody>";
		resultString += "</table>";
		resultString += "</div>";
		resultString += "</div>";
		result.appendHtml(resultString);
	}
}
