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
package de.knowwe.debugger;

import java.io.IOException;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;


/**
 * 
 * @author dupke
 */
public class DebugAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(context.getWeb(),
				context.getTitle());

		DebugHandler dh = new DebugHandler();
		String result = dh.render(article, article.getSection(), context,
				context.getParameters());
		result = cleanHTML(result);

		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String cleanHTML(String html) {
		html = html.replace("KNOWWEHTML_SMALLERTHAN", "<");
		html = html.replace("KNOWWEHTML_GREATERTHAN", ">");
		html = html.replace("KNOWWEHTML_QUOTE", "'");
		html = html.replace("KNOWWE_BRACKET_OPEN", "(");
		html = html.replace("KNOWWE_BRACKET_CLOSE", ")");
		html = html.replace("KNOWWEHTML_DOUBLEQUOTE", "\"");

		return html;
	}

}
