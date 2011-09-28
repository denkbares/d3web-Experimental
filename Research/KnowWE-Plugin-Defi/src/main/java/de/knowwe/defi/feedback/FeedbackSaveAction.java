/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.feedback;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.utils.KnowWEUtils;

/**
 * The FeedbackSaveAction stores the user input of the feedback form in a file
 * for further processing.
 * 
 * @author Stefan Mark
 * @created 26.09.2011
 */
public class FeedbackSaveAction extends AbstractAction {

	private final int QUESTIONS = 10;

	@Override
	public void execute(UserActionContext context) throws IOException {

		String path = KnowWEEnvironment.getInstance().getWikiConnector().getSavePath();
		String filename = context.getUserName().toLowerCase() + ".xml";

		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		xml.append("<feedback>\n");

		// answer have ID FBx where x is a natural number
		// questions have ID QFBx where x is a natural number

		for (int i = 1; i < QUESTIONS; i++) {

			String q = context.getParameter("QFB" + i);
			String a = context.getParameter("FB" + i);

			if (q != null && a != null) {
				xml.append("<question" + i + ">\n");
				xml.append("<question>\n");
				xml.append(q);
				xml.append("</question>\n");
				xml.append("<answer>\n");
				xml.append(a);
				xml.append("<answer>\n");
				xml.append("</question" + i + ">\n");
			}
		}
		xml.append("</feedback>");

		KnowWEUtils.writeFile(path + filename, xml.toString());

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=Main");
	}
}
