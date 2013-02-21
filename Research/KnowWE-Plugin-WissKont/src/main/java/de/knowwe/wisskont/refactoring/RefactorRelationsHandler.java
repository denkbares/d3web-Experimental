/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.wisskont.refactoring;

import java.util.Map;

import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author jochenreutelshofer
 * @created 07.12.2012
 */
public class RefactorRelationsHandler extends AbstractTagHandler {

	public RefactorRelationsHandler() {
		super("refactorRelations");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		getButtonHTML(userContext, result);
	}

	public static void getButtonHTML(UserContext userContext, RenderResult html) {

		String description = "Konzeptdefinitionen refaktorisieren";
		String jsAction = "window.location='action/RefactoringAction" +
				"?" + Attributes.TOPIC + "=" + userContext.getTitle() +
				"&" + Attributes.USER + "=" + userContext.getUserName() +
				"&" + Attributes.WEB + "=" + Environment.DEFAULT_WEB + "'";

		html.appendHTML("<a href=\"javascript:");
		html.appendHTML(jsAction);
		html.appendHTML(";void(0);\" ");
		html.appendHTML("\" title=\"");
		html.appendHTML(Strings.encodeHtml(description));
		html.appendHTML("\" class=\"onte-button left small\">");
		html.appendHTML("<img src=\"KnowWEExtension/images/dt_icon_realisation2.gif\" style=\"");
		html.appendHTML("background: url('").appendHTML("KnowWEExtension/images/disk.png").appendHTML(
				"') no-repeat scroll center 6px transparent; height: 16px;width: 16px;");
		html.appendHTML("\" /></a>");

	}

}
