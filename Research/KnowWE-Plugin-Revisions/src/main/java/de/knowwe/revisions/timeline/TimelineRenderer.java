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
package de.knowwe.revisions.timeline;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.revisions.RevisionType;

/**
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class TimelineRenderer extends DefaultMarkupRenderer {

	public TimelineRenderer() {
		super("asdf");
	}

	@Override
	protected void renderContents(Section<?> section, UserContext user, RenderResult string) {
		String html = "";

		// JSON Data Table
		html += "<script type=\"text/javascript\">";
		html += "data = [";
		for (Section<RevisionType> sec : Sections.findSuccessorsOfType(section.getFather(),
				RevisionType.class)) {
			String line = RevisionType.toTimelineString(sec);
			html += line;
		}
		html = html.substring(0, html.length() - 2);
		html += "];";
		html += "</script>";
		html += "<div id=\"mytimeline\"></div>";

		// Panel for adding a new revision
		String defaultValue = "New Revision";

		html += "<div>";
		html += "<input type=\"text\" value=\"" + defaultValue + "\" id=\"txtContent\"" +
				"onblur=\"if (this.value == '') {this.value = '" + defaultValue + "';}\"" +
				"onfocus=\"if (this.value == '" + defaultValue + "') {this.value = '';}\" />";
		html += "<input type=\"button\" value=\"Add\" title=\"Add New Revision\" onclick=\"addRev();\">";
		html += "</div>";

		// Div for revision details
		html += "<div id=\"revdetails\"></div>";
		// html += after;
		string.appendHtml(html);
	}
}
