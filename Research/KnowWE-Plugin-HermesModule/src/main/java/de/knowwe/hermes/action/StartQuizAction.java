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
import java.util.Map;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.TagHandlerAttributeSubTreeHandler;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.hermes.quiz.QuizSessionManager;
import de.knowwe.hermes.taghandler.QuizHandler;

public class StartQuizAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		String user = context.getUserName();

		String kdomid = context.getParameter("kdomid");
		Section<? extends Type> sec = Sections.getSection(kdomid);

		// boundaries necessary here ?
		Integer from = null;
		Integer to = null;

		if (kdomid != null) {
			Object storedValues = KnowWEUtils.getStoredObject(sec.getArticle(), sec,
					TagHandlerAttributeSubTreeHandler.ATTRIBUTE_MAP);
			if (storedValues != null) {
				if (storedValues instanceof Map) {
					Map<String, String> attValues = (Map<String, String>) storedValues;
					if (attValues.containsKey("from")) {
						try {
							from = Integer.parseInt(attValues.get("from"));
						}
						catch (NumberFormatException e) {

						}
					}
					if (attValues.containsKey("to")) {
						try {
							to = Integer.parseInt(attValues.get("to"));
						}
						catch (NumberFormatException e) {

						}
					}
				}
			}
		}

		QuizSessionManager.getInstance().createSession(user, from, to);
		return QuizHandler.renderQuizPanel(context.getUserName(),
				QuizSessionManager.getInstance().getSession(user), kdomid);
	}

}
