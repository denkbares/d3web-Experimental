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

package de.d3web.we.core.semantic.tagging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class TagCloud extends AbstractHTMLTagHandler {

	public TagCloud() {
		super("tagcloud");
	}

	@Override
	public String renderHTML(String topic, KnowWEUserContext user,
			Map<String, String> values, String web) {
		HashMap<String, Integer> weightedlist = TaggingMangler.getInstance().getCloudList(8, 20);
		String output = "<p>";
		// TagSearch.jsp?query=test+auto&ok=Find!&start=0&maxitems=20
		List<String> tlist = new ArrayList<String>();
		tlist.addAll(weightedlist.keySet());
		Collections.sort(tlist);

		for (String cur : tlist) {
			output += " <a href =\"Wiki.jsp?page=TagSearch&query=" + cur
					+ "&ok=Find!&start=0&maxitems=20\" style=\"font-size:" + weightedlist.get(cur)
					+ "px\">" + cur + "</a>";
		}
		return KnowWEEnvironment.maskHTML(output + "</p>");
	}
}
