/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeStore;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.utils.D3webUtils;

/**
 * @author Florian Ziegler
 * @created 17.07.2011
 */
public class DiaFluxDialogGetRequiredQuestions extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String question = context.getParameter("question");
		String topic = context.getTopic();
		String web = context.getWeb();
		String type = context.getParameter("type");

		String response = "";
		Session session = D3webUtils.getSession(topic, context.getUserName(),
				web);
		Question abstractQuestion = session.getKnowledgeBase().getManager().searchQuestion(question);
		if (type.equals("getQuestions")) {
			List<Question> neededQuestions = getNeededUnansweredQuestions(abstractQuestion, session);
			response = createQuestionString(neededQuestions);
		}
		else if (type.equals("getStatus")) {
			Value v = session.getBlackboard().getValue(abstractQuestion);
			response = v.toString();
		}

		context.getWriter().write(response);

	}

	private String createQuestionString(List<Question> questions) {
		StringBuilder bob = new StringBuilder();

		for (Question q : questions) {
			bob.append(q.getName());
			if (q instanceof QuestionNum) {
				bob.append("+-+-+");
				bob.append("num");
			}
			else if (q instanceof QuestionMC) {
				bob.append("+-+-+");
				bob.append("mc");
				bob.append("+-+-+");
				QuestionMC mc = (QuestionMC) q;
				for (Choice c : mc.getAllAlternatives()) {
					bob.append(c.getName());
					bob.append("+#+#+");
				}
			}
			else if (q instanceof QuestionOC) {
				bob.append("+-+-+");
				bob.append("oc");
				bob.append("+-+-+");
				QuestionOC mc = (QuestionOC) q;
				for (Choice c : mc.getAllAlternatives()) {
					bob.append(c.getName());
					bob.append("+#+#+");
				}
			}
			bob.append("#####");
		}
		return bob.toString();
	}


	private List<Question> getNeededUnansweredQuestions(Question abstractQuestion, Session session) {
		ArrayList<Question> neededQuestions = new ArrayList<Question>();
		List<Question> answeredQuestions = session.getBlackboard().getAnsweredQuestions();

		KnowledgeStore ks = abstractQuestion.getKnowledgeStore();
		KnowledgeSlice[] slices = ks.getKnowledge();

		for (KnowledgeSlice s : slices) {
			if (s instanceof RuleSet) {
				List<Rule> rules = ((RuleSet) s).getRules();
				for (Rule r : rules) {
					List<? extends TerminologyObject> forward = r.getAction().getForwardObjects();
					List<? extends TerminologyObject> backward = r.getAction().getBackwardObjects();
					if (!backward.contains(abstractQuestion)) {
						continue;
					}
					for (TerminologyObject t : forward) {
						if (t instanceof Question) {
							if (!answeredQuestions.contains(t)) {
								boolean abs = BasicProperties.isAbstract((Question) t);
								if (abs) {
									neededQuestions.addAll(getNeededUnansweredQuestions(
													(Question) t, session));
								}
								else {
									neededQuestions.add((Question) t);
								}

							}

						}

					}
				}
			}

		}

		return neededQuestions;
	}

}
