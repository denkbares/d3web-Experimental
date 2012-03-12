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
package de.knowwe.casetrain.util;

import java.util.Collection;
import java.util.ResourceBundle;

import de.casetrain.cleanup.Cleaner;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

public class Utils {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	public static ResourceBundle getBundle() {
		return bundle;
	}

	public static Message invalidArgumentError(String message) {
		return Messages.error(getBundle().getString("INVALID_ARGUMENT_ERROR") + message);
	}

	public static Message invalidArgumentNotice(String message) {
		return Messages.notice(getBundle().getString("INVALID_ARGUMENT_NOTICE") + message);
	}

	public static Message invalidArgumentWarning(String message) {
		return Messages.warning(getBundle().getString("INVALID_ARGUMENT_WARNING") + message);
	}

	public static Message invalidAttributeError(String message) {
		return Messages.error(getBundle().getString("INVALID_ATTRIBUTE_ERROR") + message);
	}

	public static Message missingAttributeError(String message) {
		return Messages.error(getBundle().getString("MISSING_ATTRIBUTE_ERROR") + message);
	}

	public static Message missingAttributeWarning(String message) {
		return Messages.warning(Utils.getBundle().getString("MISSING_ATTRIBUTE_WARNING") + message);
	}

	public static Message missingAudioError(String message) {
		return Messages.error(getBundle().getString("MISSING_AUDIO_ERROR") + message);
	}

	public static Message missingComponentError(String message) {
		return Messages.error(getBundle().getString("MISSING_COMPONENT_ERROR") + message);
	}

	public static Message missingContentWarning(String message) {
		return Messages.warning(getBundle().getString("MISSING_CONTENT_WARNING") + message);
	}

	public static Message missingComponentWarning(String message) {
		return Messages.warning(getBundle().getString("MISSING_COMPONENT_WARNING") + message);
	}

	public static Message missingPictureError(String message) {
		return Messages.error(getBundle().getString("MISSING_PICTURE_ERROR") + message);
	}

	public static Message missingPictureNotice(String message) {
		return Messages.notice(getBundle().getString("MISSING_PICTURE_NOTICE") + message);
	}

	public static Message missingTitleError(String message) {
		return Messages.error(Utils.getBundle().getString("MISSING_TITLE_ERROR") + message);
	}

	public static Message missingVideoError(String message) {
		return Messages.error(Utils.getBundle().getString("MISSING_VIDEO_ERROR") + message);
	}

	public static void renderKDOMReportMessageBlock(
			Collection<? extends Message> messages, StringBuilder string) {
		if (messages == null) return;
		if (messages.size() == 0) return;

		Message msg = messages.iterator().next();
		String className = "";
		if (msg.getType() == Message.Type.INFO) {
			className = "info";
		}
		else if (msg.getType() == Message.Type.WARNING) {
			className = "warning";
		}
		else if (msg.getType() == Message.Type.ERROR) {
			className = "error";
		}

		string.append(KnowWEUtils.maskHTML("<span class='" + className + "'>"));
		for (Message error : messages) {
			string.append(error.getVerbalization());
			string.append(KnowWEUtils.maskHTML("<br/>"));
		}
		string.append(KnowWEUtils.maskHTML("</span>"));
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
	public static String renderTools(Section<?> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		Tool[] tools = ToolUtils.getTools(sec, user);

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

	public static String wikiSyntaxToHtml(String syntax) {
		syntax = Environment.getInstance().getWikiConnector().wikiSyntaxToHtml(syntax);
		syntax = Cleaner.removeTagsExceptIUB(syntax);
		return syntax;
	}

}