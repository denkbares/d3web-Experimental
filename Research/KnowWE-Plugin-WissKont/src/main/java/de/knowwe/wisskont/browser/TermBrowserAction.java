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
package de.knowwe.wisskont.browser;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.rendering.RenderResult;

/**
 * 
 * @author jochenreutelshofer
 * @created 10.12.2012
 */
public class TermBrowserAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	/**
	 * 
	 * @created 10.12.2012
	 * @param context
	 * @return
	 */
	private String perform(UserActionContext context) {
		String command = context.getParameter("command");
		String term = context.getParameter("term");
		if (term == null) {
			term = "";
		}
		else {
			if (command.equals("searched")) {
				// update ranking weights
				TermRecommender.getInstance().termSearched(context, term);
			}
			else if (command.equals("remove")) {
				TermRecommender.getInstance().clearTerm(context, term);
			}
			else if (command.equals("expand")) {
				TermRecommender.getInstance().expandTerm(context, term);
			}
			else if (command.equals("collapse")) {
				TermRecommender.getInstance().collapseTerm(context, term);
			}
			else if (command.equals("open")) {
				// is handled by a link
			}
		}
		return RenderResult.unmask(TermBrowserRenderUtils.renderTermBrowser(context, term), context);
	}
}
