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
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.report.KDOMError;
import de.knowwe.core.report.KDOMNotice;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.KDOMWarning;
import de.knowwe.core.utils.KnowWEUtils;

public class Utils {

	private static final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	public static ResourceBundle getBundle() {
		return bundle;
	}

	public static void renderKDOMReportMessageBlock(
			Collection<? extends KDOMReportMessage> messages, StringBuilder string) {
		if (messages == null) return;
		if (messages.size() == 0) return;

		Class<? extends KDOMReportMessage> type = messages.iterator().next().getClass();
		String className = "";
		if (KDOMNotice.class.isAssignableFrom(type)) {
			className = "information";
		}
		else if (KDOMWarning.class.isAssignableFrom(type)) {
			className = "warning";
		}
		else if (KDOMError.class.isAssignableFrom(type)) {
			className = "error";
		}

		string.append(KnowWEUtils.maskHTML("<span class='" + className + "'>"));
		for (KDOMReportMessage error : messages) {
			string.append(error.getVerbalization());
			string.append(KnowWEUtils.maskHTML("<br/>"));
		}
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

	public static String wikiSyntaxToHtml(String syntax) {
		syntax = KnowWEEnvironment.getInstance().getWikiConnector().wikiSyntaxToHtml(syntax);
		syntax = Cleaner.removeTagsExceptIUB(syntax);
		return syntax;
	}

}