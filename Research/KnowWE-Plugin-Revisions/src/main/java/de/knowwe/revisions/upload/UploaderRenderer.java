/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions.upload;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;

/**
 * 
 * @author grotheer
 * @created 23.04.2013
 */
public class UploaderRenderer extends DefaultMarkupRenderer {

	public UploaderRenderer() {
		super();
	}

	@Override
	protected void renderContents(Section<?> section, UserContext user, RenderResult string) {
		appendRevisionUploadForm(section, string);

		// Div for upload details
		string.appendHtml("<div id=\"uploaddetails\"></div>");
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param section
	 * @param string
	 */
	private static void appendRevisionUploadForm(Section<?> section, RenderResult string) {
		string.appendHtml("<div class='collapsebox'>");
		string.appendHtmlElement("h4", "Upload revision", "class='collapsetitle'");
		string.appendHtml("<form id='my_form' target='_self' autocomplete=\"off\" action=\"action/UploadRevisionZip?KWiki_Topic="
				+ section.getTitle() + "&KWikiWeb=" + section.getWeb()
				+ "&\" method=\"POST\" enctype=\"multipart/form-data\">");
		string.appendHtmlElement("label", "Select file: ", "");
		string.appendHtml("<input id=\"fileinput\" type=\"file\" name=\"file\" size=\"50\"/>");
		string.appendHtml("<input type=\"submit\" value=\"Upload\" />");
		string.appendHtml("</form>");
		string.appendHtml("</div>");
	}
}