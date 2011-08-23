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

package de.d3web.we.action;

import java.io.IOException;

import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;

public class HighlightNodeAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		String nodeID = context.getParameter(KnowWEAttributes.JUMP_ID);
		String article = context.getTopic();
		String web = context.getWeb();
		if (web == null) {
			web = KnowWEEnvironment.DEFAULT_WEB;
		}
		if (nodeID != null) {
			if (article != null) {
				KnowWEArticle art = KnowWEEnvironment.getInstance().getArticleManager(web).getArticle(
						article);
				Section<?> sec = Sections.getSection(nodeID);
				if (sec != null) {
					// sec.setRenderer(new OneTimeRenderer(sec,
					// HighlightRenderer.getInstance()));
					return "renderer set";
				}
				else {
					return "no section found for id: " + nodeID;
				}
			}
			else {
				return "no article found";
			}
		}
		else {
			return "no nodeID found";
		}
	}
}
