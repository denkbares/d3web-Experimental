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
package de.knowwe.we.action;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;

/**
 * Renders the section in QuickEdit mode.
 *
 * @author smark
 * @created 22.06.2011
 */
public class RenderAsQuickEditAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = handle(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	/**
	 * Wraps the text of a section in a HTML textarea fur quick editing.
	 *
	 * @created 15.06.2011
	 * @param context
	 * @return success JSON string
	 */
	private String handle(UserActionContext context) {

		String web = context.getWeb();
		String sectionID = context.getParameter("KdomNodeId");
		String topic = context.getTopic();

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		KnowWEArticle article = mgr.getArticle(topic);

		Section<? extends Type> sec = article.findSection(sectionID);

		StringBuilder html = new StringBuilder();
		html.append("<div id=\"" + sec.getID() + "\">");
		html.append("<textarea style=\"white: 100%; height:"
				+ getHeight(sec)
				+ "px;display: inline; border: 1px dashed rgb(0, 0, 0); background-color: rgb(255, 255, 119);\">");
		html.append(sec.getOriginalText());
		html.append("</textarea>");
		html.append("<a href=\"javascript:KNOWWE.plugin.quickedit.save('" + sec.getID()
				+ "', '" + sec.getOriginalText()
				+ "')\" style=\"display: inline; margin: 0px 4px;\">save</a>");
		html.append("<a href=\"javascript:KNOWWE.plugin.quickedit.cancel('" + sec.getID()
				+ "')\" style=\"display: inline; margin: 0px 4px;\">cancel</a>");

		html.append("</div>");

		return html.toString();
	}

	/**
	 * Calculates the height of the HTML textarea.
	 *
	 * Note: Copied from the EditSectonRenderer
	 *
	 * @param str - The string used to calculate the height.
	 * @param isInline - If true the textarea gets no additional newlines.
	 * @return The height of the HTML textarea element.
	 */
	private Integer getHeight(Section<? extends Type> section) {

		String text = section.getOriginalText();
		boolean isInline = this.isInline(section);

		int additionallines = 2;
		if (isInline) additionallines = 0;
		int linebreaks = text.split("\n|\f").length;
		int lineHeight = 18;
		return (linebreaks + additionallines) * lineHeight;
	}

	/**
	 * Searches the first ancestor Section of section with some text right in
	 * front of the section's one, and checks whether both are separated by '\n'
	 * or '\f' or not (inline).
	 *
	 * Note: Copied from the EditSectonRenderer
	 *
	 * @created 07.08.2010
	 * @param sec The section used by the ESR which could be inline.
	 * @return True if the section (its OrignialText) is in the same line as the
	 *         text before; false if they are separated by '\n' or '\f'.
	 */
	private boolean isInline(Section<? extends Type> sec) {
		String text = sec.getOriginalText();
		if (text.startsWith("\n") || text.startsWith("\f") || text.length() == 0) return false;
		KnowWEArticle rootTypeObj = sec.getArticle().getSection().get();
		// Move up the Section-DOM till you find one with 'more' OriginalText
		while (sec.getFather().get() != rootTypeObj) {
			sec = sec.getFather();
			Matcher m = Pattern.compile(text, Pattern.LITERAL).matcher(sec.getOriginalText());
			m.find();
			// Text BEFORE section shouldn't end with '\n' or '\f'
			if (m.start() != 0) {
				String textBefore = sec.getOriginalText().substring(0, m.start());
				return !textBefore.endsWith("\n") && !textBefore.endsWith("\f");
			}
		}
		return false;
	}
}
