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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * The FeedbackSaveAction stores the user input of the feedback form in a file
 * for further processing.
 * 
 * @author Stefan Mark
 * @created 26.09.2011
 */
public class FeedbackSaveAction extends AbstractAction {

	private final int QUESTIONS = 27;

	@Override
	public void execute(UserActionContext context) throws IOException {

		String path = Environment.getInstance().getWikiConnector().getSavePath();
		String filename = context.getUserName().toLowerCase() + ".xml";

		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		xml.append("<feedback>\n");

		// answer have ID FBx where x is a natural number
		// questions have ID QFBx where x is a natural number

		for (int i = 1; i < QUESTIONS; i++) {

			String q = context.getParameter("QFB" + i);
			String a = context.getParameter("FB" + i);

			if (q != null) {
				xml.append("<question" + i + ">\n");
				xml.append("    <question>");
				xml.append(q);
				xml.append("</question>\n");
				xml.append("    <answer>");

				if (a != null) {
					xml.append(a);
				}
				else {
					// QFB1-1-1 FB1-1-1
					for (int j = 1; j < 6; j++) {
						String sub = context.getParameter("QFB" + i + "-" + j);
						if (sub != null) {
							xml.append("\n        <subquestion>");
							xml.append(sub);
							xml.append("</subquestion>\n");
						}
						for (int k = 1; k < 6; k++) {
							String sa = context.getParameter("FB" + i + "-" + j + "-" + k);
							if (sa != null) {
								xml.append("        <subanswer>");
								xml.append(sa);
								xml.append("</subanswer>\n");
							}
						}
					}
				}

				xml.append("</answer>\n");
				xml.append("</question" + i + ">\n");
			}

		}
		xml.append("</feedback>");

		KnowWEUtils.writeFile(path + "/" + filename, xml.toString());

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=BefragungAbgeschlossen");
	}
}
