/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * @author Jochen
 * 
 *         This action allows to append any String to the article source of an
 *         article
 * 
 */
public class AppendToPageContentAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String web = context.getWeb();
		String name = context.getTopic();
		String appendText = context.getParameter(Attributes.TEXT);
		ArticleManager mgr = Environment.getInstance().getArticleManager(web);
		Article art = mgr.getArticle(name);

		// replaces old article content with old article content + text to
		// append
		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(art.getSection().getID(), art.getSection().getText() + appendText);
		Sections.replaceSections(context, nodesMap);

	}

}
