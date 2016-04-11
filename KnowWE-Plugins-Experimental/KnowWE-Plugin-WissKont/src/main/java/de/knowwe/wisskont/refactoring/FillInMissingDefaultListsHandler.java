/*
 * Copyright (C) 2014 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.wisskont.refactoring;

import java.util.Map;

import de.d3web.strings.Strings;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * @author Johanna Latt
 * @created 08.01.2014
 */
public class FillInMissingDefaultListsHandler extends AbstractTagHandler {

	public FillInMissingDefaultListsHandler() {
		super("fillInMissingDefaultLists");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		getButtonHTML(userContext, result);
	}

	public static void getButtonHTML(UserContext userContext, RenderResult html) {

		String description = "Fehlende Default-Listen werden ergänzt (kann:, muss:, cave, Assoziation:)";
		String jsAction = "window.location='action/FillInMissingDefaultListsRefactoring" +
				"?" + Attributes.TOPIC + "=" + userContext.getTitle() +
				"&amp;" + Attributes.USER + "=" + userContext.getUserName() +
				"&amp;" + Attributes.WEB + "=" + Environment.DEFAULT_WEB + "'";

		html.appendHtml("<a href=\"javascript:");
		html.appendHtml(jsAction);
		html.appendHtml(";void(0);\" ");
		html.appendHtml("\" title=\"");
		html.appendHtml(Strings.encodeHtml(description));
		html.appendHtml("\" class=\"onte-button left small\">");
		html.appendHtml("<img src=\"KnowWEExtension/images/dt_icon_realisation2.gif\" style=\"");
		html.appendHtml("background: url('").appendHtml("KnowWEExtension/images/disk.png").appendHtml(
				"') no-repeat scroll center 6px transparent; height: 16px;width: 16px;");
		html.appendHtml("\" /></a>");
	}
}