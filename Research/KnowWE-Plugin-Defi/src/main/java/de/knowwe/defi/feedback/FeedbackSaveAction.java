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
 * @author Stefan Mark, dupke
 * @created 26.09.2011
 */
public class FeedbackSaveAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String path = Environment.getInstance().getWikiConnector().getSavePath();
		String filename = context.getUserName().toLowerCase() + "_feedback.xml";

		StringBuilder xml = new StringBuilder();

		String entry = context.getParameter("entries");
		String[] entries = entry.split(":::");
		String[] parts;

		//
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		xml.append("<feedback>\n");
		for (int i = 0; i < entries.length; i++) {
			parts = entries[i].split("###");
			xml.append("\t<question id='q" + (i + 1) + "'>\n");
			xml.append("\t\t<topic>"
					+ parts[0].replace("<br><i>(Bitte alle zutreffenden Antworten markieren!)</i>",
							"") + "</topic>\n");
			for (int j = 1; j < parts.length; j++) {
				xml.append("\t\t<answer id='" + parts[j].split("---")[0] + "'>"
						+ parts[j].split("---")[1] + "</answer>\n");
			}
			xml.append("\t</question>\n");
		}
		xml.append("</feedback>");

		KnowWEUtils.writeFile(path + "/" + filename, xml.toString());

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=BefragungAbgeschlossen");
	}
}
