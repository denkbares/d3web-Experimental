/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions.manager.action;

import java.io.IOException;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 22.04.2013
 */
public class UploadedTextDiff extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String perform(UserActionContext context) throws IOException {
		Map<String, String> params = context.getParameters();
		if (params.containsKey("title")) {
			String title = params.get("title");
			Article uploadedArticle = RevisionManager.getRM(context).getUploadedRevision().getArticleManager().getArticle(
					title);
			String t1 = uploadedArticle.getRootSection().getText();
			String t2 = Environment.getInstance().getWikiConnector().getArticleText(title, -1);

			String diff = SimpleTextDiff.getTextDiff(title, 0, -1, t1, t2, "\n");
			if (!diff.isEmpty()) {
				return "<pre>" + diff
						+ "</pre>";
			}
			else {
				return "<p class=\"box error\">No differences to current page version.</p>";
			}
		}
		return "<p class=\"box error\">Error while getting text diff.</p>";
	}
}
