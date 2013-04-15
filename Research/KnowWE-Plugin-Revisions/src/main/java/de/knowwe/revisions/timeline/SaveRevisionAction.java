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
package de.knowwe.revisions.timeline;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.revisions.DateType;

/**
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class SaveRevisionAction extends AbstractAction {

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
		if (params.containsKey("rev") && params.containsKey("date")) {
			String rev = params.get("rev");
			Date date = new Date(Long.parseLong(params.get("date")));

			String markup = "%%Revision\n@name = " + rev + "\n@date = "
					+ DateType.DATE_FORMAT.format(date) + "\n%";

			Article a = Environment.getInstance().getArticle(context.getWeb(), context.getTitle());
			HashMap<String, String> sectionsMap = new HashMap<String, String>();
			Section<RootType> s = a.getRootSection();
			sectionsMap.put(s.getID(), s.getText().concat(markup));
			Sections.replaceSections(context, sectionsMap);
			return "<p class=\"box ok\">Revision '" + rev + "' successfully saved.";
		}
		return "<p class=\"box error\">Error while saving revision.";
	}
}
