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

import com.denkbares.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * Renders a button into a Wiki article that allows the user to download the
 * current page as PDF file.
 * 
 * @author smark
 * @created 27.04.2011
 */
public class DownloadPageAsPDFTaghandler extends AbstractTagHandler {

	public DownloadPageAsPDFTaghandler() {
		super("pdf");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {

		String title = parameters.get("title");

		if (title == null) {
			title = " Download as PDF ";
		}

		result.appendHtml("<a href=\"wiki.pdf?page=" + section.getTitle() + "&ext=.pdf");
		result.appendHtml("\" title=\"Title:");
		result.append(Strings.encodeHtml(title));
		result.appendHtml("\" rel=\"nofollow\">");
		result.appendHtml("<button class=\"defi-bttn defi-download\">");
		result.append(title);
		result.appendHtml("</button>");
		result.appendHtml("</a>");

	}
}
