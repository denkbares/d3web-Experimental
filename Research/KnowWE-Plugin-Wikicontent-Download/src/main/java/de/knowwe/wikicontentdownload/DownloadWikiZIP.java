/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.wikicontentdownload;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.knowRep.KnowledgeRepresentationHandler;

/**
 * 
 * @author Johanna
 * @created 16.04.2012
 */
public class DownloadWikiZIP extends AbstractAction {

	public static final String PARAM_FILENAME = "filename";

	@Override
	public void execute(UserActionContext context) throws IOException {
		String filename = context.getParameter(PARAM_FILENAME);
		String topic = context.getParameter(Attributes.TOPIC);
		String web = context.getParameter(Attributes.WEB);

		if (!Environment.getInstance().getWikiConnector().userCanViewPage(topic,
				context.getRequest())) {
			context.sendError(HttpServletResponse.SC_FORBIDDEN,
					"You are not allowed to download this knowledgebase.");
		}

		KnowledgeRepresentationHandler handler = Environment.getInstance()
				.getKnowledgeRepresentationManager(web).getHandler("d3web");

		// before writing, check if the user defined a desired filename
		// KnowledgeBase base = D3webUtils.getKnowledgeBase(web, topic);
		// String desired_filename =
		// base.getInfoStore().getValue(BasicProperties.FILENAME);
		// if (desired_filename != null) {
		// filename = desired_filename;
		// }
	}

}
