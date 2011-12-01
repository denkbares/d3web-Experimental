/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester;

import java.util.Collection;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.manchester.frame.DefaultFrame;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

/**
 * Highlights elements of the Manchester OWL syntax in the article. Also wraps
 * the long lines so no ugly horizontal scrolling is necessary.
 * 
 * @author Stefan Mark
 * @created 10.08.2011
 */
public class ManchesterSyntaxFrameRenderer extends KnowWEDomRenderer<DefaultFrame> {

	/**
	 * Specifies if a link to the article where the current {@link DefaultFrame}
	 * has been found, should be rendered into the resulting page.
	 */
	private boolean renderLink = false;

	@Override
	public void render(KnowWEArticle article, Section<DefaultFrame> sec, UserContext user, StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<pre id=\""
				+ sec.getID()
				+ "\"style=\"white-space:pre-wrap;background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;position:relative;margin:0px\">"));

		renderMessages(article, sec, string);
		string.append(KnowWEUtils.maskHTML("<div style=\"position:absolute;top:0px;right:0px;border-bottom: 1px solid #E5E5E5;border-left: 1px solid #E5E5E5;padding:5px\">"
				// + getFrameName(sec)
				// + getEditorIcon(sec)
				+ renderTools(article, sec, user)
				+ getLink(sec)
				+ "</div>"));

		StringBuilder frame = new StringBuilder();
		DelegateRenderer.getInstance().render(article, sec, user, frame);

		// remove the newlines at the end for nicer rendering
		frame.replace(frame.length() - 2, frame.length(), "");
		string.append(frame);

		string.append(KnowWEUtils.maskHTML("</pre>"));
	}

	/**
	 * Renders possible messages returned by the {@link SubtreeHandler}.
	 * 
	 * @created 18.10.2011
	 * @param KnowWEArticle article
	 * @param Section<? extends Type> section
	 * @param StringBuilder string
	 */
	private void renderMessages(KnowWEArticle article, Section<? extends Type> section, StringBuilder string) {
		Collection<Message> allmsgs = Messages.getMessagesFromSubtree(article, section);
		Collection<Message> errors = Messages.getErrors(allmsgs);
		Collection<Message> warnings = Messages.getWarnings(allmsgs);
		renderKDOMReportMessages(errors, string);
		renderKDOMReportMessages(warnings, string);
	}

	private void renderKDOMReportMessages(Collection<Message> messages, StringBuilder string) {
		if (messages == null) return;
		if (messages.isEmpty()) return;

		Message msg = messages.iterator().next();
		String className = "";
		if (msg.getType() == Message.Type.WARNING) {
			className = "warning";
		}
		else if (msg.getType() == Message.Type.ERROR) {
			className = "error";
		}

		string.append(KnowWEUtils.maskHTML("<span class='" + className + "'>"));
		for (Message error : messages) {
			string.append(error.getVerbalization());
			string.append("\n");
		}
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

	/**
	 * 
	 * 
	 * @created 07.10.2011
	 * @param Section<DefaultFrame> section The current {@link DefaultFrame}
	 * @return
	 */
	private String getEditorIcon(Section<DefaultFrame> section) {
		StringBuilder icon = new StringBuilder();
		icon.append("<a href=\"javascript:KNOWWE.plugin.onte.popEditor('Edit current frame');\">");
		icon.append("<img src=\"KnowWEExtension/images/owl_class_24.png\" width=\"32\"/>");
		icon.append("</a>");
		return icon.toString();

	}

	/**
	 * Returns the name of the current {@link DefaultFrame} for visual
	 * highlighting to the user on the article page.
	 * 
	 * @created 22.09.2011
	 * @param Section<DefaultFrame> section The current {@link DefaultFrame}
	 * @return The name of the {@link DefaultFrame}
	 */
	private String getFrameName(Section<DefaultFrame> section) {
		return section.get().getName();
	}

	/**
	 * Has the section some ToolProvider attached, render the tools into the
	 * resulting HTML output. This is a adaption from the
	 * ToolMenuDecoratingRenderer. This was needed to include some of the
	 * ToolProvider beside the DefaultMarkup. Maybe this can be handled better
	 * in the future.
	 * 
	 * @created 12.11.2011
	 * @param article
	 * @param sec
	 * @param user
	 * @return
	 */
	private String renderTools(KnowWEArticle article, Section<DefaultFrame> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		Tool[] tools = ToolUtils.getTools(article, sec, user);

		for (Tool t : tools) {
			String icon = t.getIconPath();
			String jsAction = t.getJSAction();
			boolean hasIcon = icon != null && !icon.trim().isEmpty();

			string.append("<span class=\"" + t.getClass().getSimpleName() + "\" >"
					+ "<"
					+ (jsAction == null ? "span" : "a")
					+ " class=\"markupMenuItem\""
					+ (jsAction != null
							? " href=\"javascript:" + t.getJSAction() + ";undefined;\""
							: "") +
					" title=\"" + t.getDescription() + "\">" +
					(hasIcon ? ("<img src=\"" + icon + "\"></img>") : "") +
					"</" + (jsAction == null ? "span" : "a") + ">" +
					"</span>");
		}
		return string.toString();
	}

	/**
	 * Renders a link to the current article the {@link DefaultFrame} can be
	 * found.
	 * 
	 * @created 27.09.2011
	 * @param Section<DefaultFrame> section The current {@link DefaultFrame}
	 * @return A link to the article the section can be found
	 */
	private String getLink(Section<DefaultFrame> section) {
		if (renderLink) {
			return " - <a href=\"Wiki.jsp?page=" + section.getTitle()
					+ "\" title=\"View section in occuring article\"/>"
					+ section.getTitle() + "</a>";
		}
		return "";
	}

	public boolean getRenderLink() {
		return renderLink;
	}

	public void setRenderLink(boolean render) {
		renderLink = render;
	}
}
