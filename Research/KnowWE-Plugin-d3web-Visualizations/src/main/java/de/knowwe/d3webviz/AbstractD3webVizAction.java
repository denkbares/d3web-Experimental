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
package de.knowwe.d3webviz;

import java.io.IOException;
import java.util.Iterator;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 17.05.2013
 */
public abstract class AbstractD3webVizAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) throws IOException {
		String sectionID = context.getParameter(Attributes.SECTION_ID);
	
		Section<?> section = Sections.getSection(sectionID);
	
		if (section == null) {
			// TODO error handling
			return;
		}
		
		Iterator<Article> iterator = KnowWEUtils.getCompilingArticles(section).iterator();
		if (!iterator.hasNext()) return;
		
		Article article = iterator.next();
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(section.getWeb(), article.getTitle());
		
		String result = createOutput(kb, section, context);
		
		context.setContentType("text/json");
		context.getWriter().write(result);
		
	}

	protected abstract String createOutput(KnowledgeBase kb, Section<?> section, UserActionContext context);

}