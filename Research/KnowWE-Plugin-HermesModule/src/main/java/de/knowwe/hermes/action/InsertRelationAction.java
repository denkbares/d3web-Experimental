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

package de.knowwe.hermes.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.hermes.kdom.TimeEventDescriptionType;

public class InsertRelationAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) throws IOException {

		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB);
		String topic = context.getTopic();
		Section<?> event = Sections.getSection(context.getParameter("kdomid"));

		if (event != null) {
			Section<?> description = Sections.findAncestorOfType(event,
					TimeEventDescriptionType.class);
			if (description != null) {
				String property = context.getParameter("property");
				String object = context.getParameter("object");
				if (property != null && object != null) {

					StringBuffer insertion = new StringBuffer();
					insertion.append("[");
					insertion.append(property);
					insertion.append("::");
					insertion.append(object);
					insertion.append("]");

					if (!description.getOriginalText().contains(insertion.toString())) {
						Map<String, String> nodesMap = new HashMap<String, String>();
						nodesMap.put(description.getID(), description.getOriginalText() + " - "
								+ insertion.toString());
						Sections.replaceSections(context, nodesMap);
					}
					return "done";
				}
			}
		}

		return "false";
	}

}
