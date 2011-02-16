/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcase;

import java.io.IOException;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.ActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEParameterMap;

/**
 * @author Florian Ziegler
 * @created 04.07.2010
 */
public class GetNewQuickEditAnswersAction extends AbstractAction {

	public static final String NO_CHOICES_STRING = "[:]EMPTY[:]";
	public static final String SEPARATOR = "[:;:]";
	public static final String UNKNOWN_VALUE_STRING = "Unknown";
	public static final String UNCHANGED_VALUE_STRING = "-";

	@Override
	/**
	 * returns the new answers (separated by [:;:]) for a given headerElement if
	 * the question is mc/oc/yn or [:]EMPTY[:] if the question
	 * is of a different type
	 */
	public void execute(ActionContext context) throws IOException {
		context.setContentType("text/plain; charset=UTF-8");

		KnowWEParameterMap map = context.getKnowWEParameterMap();
		String web = map.getWeb();
		String topic = map.getTopic();
		String element = context.getParameter("element");

		KnowledgeBaseManagement kbm = D3webModule.getKnowledgeRepresentationHandler(web).getKBM(
				topic);

		Question question = kbm.getKnowledgeBase().getManager().searchQuestion(element);

		if (question != null) {
			writeAnswersForQuestion(context, question);
		}
		else {
			Solution solution = kbm.getKnowledgeBase().getManager().searchSolution(element);
			if (solution != null) {
				context.getWriter().write(
						UNCHANGED_VALUE_STRING + SEPARATOR + "established" + SEPARATOR
								+ "suggested" + SEPARATOR + "excluded");
			}
			else {
				// TODO Do some errorHandling here.
				// object must have been removed
			}

		}
	}

	/**
	 * 
	 * @created 18.01.2011
	 * @param context
	 * @param element
	 * @param question
	 * @param questions
	 * @return
	 * @throws IOException
	 */
	private void writeAnswersForQuestion(ActionContext context, Question q) throws IOException {
		if (q instanceof QuestionChoice) {
			StringBuffer buffy = new StringBuffer(UNCHANGED_VALUE_STRING + SEPARATOR);
			for (Choice c : ((QuestionChoice) q)
					.getAllAlternatives()) {
				buffy.append(c.getName() + SEPARATOR);
			}
			buffy.append(UNKNOWN_VALUE_STRING);
			context.getWriter().write(buffy.toString());
		}
		else { //
			context.getWriter().write(NO_CHOICES_STRING);
		}
	}

}
