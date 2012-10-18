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
package de.knowwe.diaflux.review;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.diaflux.type.FlowchartType;


/**
 * 
 * @author Reinhard Hatko
 * @created 17.10.2012
 */
public class LoadReviewAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String kdomid = context.getParameter("kdomid");
		Section<FlowchartType> flowSec = Sections.getSection(kdomid, FlowchartType.class);
		Article article = flowSec.getArticle();
		String flowName = FlowchartType.getFlowchartName(flowSec);
		WikiConnector connector = Environment.getInstance().getWikiConnector();
		List<WikiAttachment> attachments = connector.getAttachments(article.getTitle());

		String filename = article.getTitle() + "/" + flowName + ".review.xml";
		WikiAttachment attachment = connector.getAttachment(filename);

		if (attachment == null) {
			// TODO error handling

		}
		else {
			InputStream in = attachment.getInputStream();
			context.setContentType("text/xml");

			OutputStream outs = context.getOutputStream();

			int bit;
			try {
				while ((bit = in.read()) >= 0) {
					outs.write(bit);
				}

			}
			catch (IOException ioe) {
				ioe.printStackTrace(System.out);
				context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.valueOf(ioe));
			}
			finally {
				in.close();
			}

		}

	}

}
