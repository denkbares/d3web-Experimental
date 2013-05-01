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
package de.knowwe.revisions.manager;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.revisions.RevisionType;

/**
 * This renderer shows a revision manager. It displays all revisions stored on
 * the actual wiki page. this includes a javascript timeline.
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class RevisionManagerRenderer extends DefaultMarkupRenderer {

	private static final String defaultNameValue = "Revision name";
	private static final String defaultCommentValue = "Comment";

	public RevisionManagerRenderer() {
		super();
	}

	@Override
	protected void renderContents(Section<?> section, UserContext user, RenderResult string) {
		appendNewRevisionPanel(string);

		appendJsonDataTableAndTimeline(section, string, user);

		string.appendHtml("<a onClick='showCurrentRev();'>Current revision</a>");
		string.appendHtml(", <span id='uploadedLink'>");
		if (RevisionManager.getRM(user).getUploadedRevision() != null) {
			string.appendHtml("<a id='uploadedLink' onClick='showUploadedRev();'>Uploaded revision</a>");
		}
		string.appendHtml("</span>");

		// Div for save warning message
		string.appendHtml("<div id=\"reverror\"></div>");

		// Div for revision details
		string.appendHtml("<div id=\"revdetails\"></div>");
	}

	/**
	 * Create and append text inputs and button to create a new revision
	 * 
	 * @created 22.04.2013
	 * @param html
	 * @return
	 */
	private static void appendNewRevisionPanel(RenderResult string) {
		string.appendHtml("<div class='collapsebox'>");
		string.appendHtmlElement("h4", "Add new revision", "class='collapsetitle'");
		string.appendHtml("<div>");
		string.appendHtml("<input  style=\"color:#999999;\" size=\"40\" type=\"text\" value=\""
				+ defaultNameValue
				+ "\" id=\"newRevName\" autocomplete=\"off\" " +
				"onblur=\"if (this.value == '') {this.value = '" + defaultNameValue
				+ "';this.style.color='#999999';}\"" +
				"onfocus=\"if (this.value == '" + defaultNameValue
				+ "') {this.value = '';this.style.color='#000000';}\" />");
		string.appendHtml("<input style=\"color:#999999;\" size=\"30\" type=\"text\" value=\""
				+ defaultCommentValue
				+ "\" id=\"newRevComment\" autocomplete=\"off\" " +
				"onblur=\"if (this.value == '') {this.value = '" + defaultCommentValue
				+ "';this.style.color='#999999';}\"" +
				"onfocus=\"if (this.value == '" + defaultCommentValue
				+ "') {this.value = '';this.style.color='#000000';}\" />");
		string.appendHtml("<input id=\"addrevbtn\" autocomplete=\"off\" type=\"button\" value=\"Add\" title=\"Add New Revision\" onclick=\"addRev();\">");
		string.appendHtml("</div>");
		string.appendHtml("</div>");
	}

	/**
	 * Create and append Panel for adding a new revision
	 * 
	 * @created 22.04.2013
	 * @param section
	 * @param html
	 * @return
	 */
	private static void appendJsonDataTableAndTimeline(Section<?> section, RenderResult string, UserContext user) {
		string.appendHtml("<script type=\"text/javascript\">\n");
		string.appendHtml("data = [\n");
		for (Section<RevisionType> sec : Sections.findSuccessorsOfType(section.getFather(),
				RevisionType.class)) {
			String line = RevisionType.toTimelineString(sec);
			string.appendHtml(line);
		}
		// if (RevisionManager.getRM(user).getUploadedRevision() != null) {
		// string.appendHtml("{\n" +
		// "\'start\': new Date(),\n" +
		// "\'content\': \'Uploaded Revision\',\n" +
		// // "\'group\': \'Uploaded\',\n" +
		// "},\n");
		// }
		string.deleteCharAt(string.length() - 2);
		string.appendHtml("];");

		// string.appendHtml("selection = [");
		// if (RevisionManager.getRM(user).getUploadedRevision() != null) {
		// string.appendHtml("{'row': data.length-1}");
		// }
		// string.appendHtml("];");

		string.appendHtml("</script>");
		string.appendHtml("<div id=\"mytimeline\"></div>");
	}
}
