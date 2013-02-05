/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ophtovis;

import java.io.IOException;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author adm_rieder
 * @created 30.01.2013
 */
public class AjaxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		// TODO Auto-generated method stub

		String title = "foo";
		Article article = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB).getArticle(
				title);

		Section<RootType> rootSection = article.getRootSection();

		String html = "AjaxTesting";

		context.setContentType("text/html; charset=UTF-8");
		context.getWriter().write(html);
		System.out.println("AjaxAction ausgeführt !!!");

	}

}
