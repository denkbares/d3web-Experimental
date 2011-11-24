/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.output.render;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic Questions.
 * 
 * TODO CHECK: 1) basic properties for questions
 * 
 * TODO LATER: 1) further question types needed?
 * 
 * @author Martina Freiberg
 * @created 15.01.2011
 */
public class QuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

	@Override
	/**
	 * Adapted specifically for question rendering
	 */
	public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
			TerminologyObject to, TerminologyObject parent) {

		Boolean hidden = to.getInfoStore().getValue(ProKEtProperties.HIDE);
		// return if the InterviewObject is null
		if (to == null || (hidden != null && hidden)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, otherwise, the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
				super.getTemplateName("Question"), "html");

		// set some basic properties
		st.setAttribute("fullId", getID(to));
		st.setAttribute("title", to.getName());
		st.setAttribute("count", D3webConnector.getInstance().getID(to));

		// read html popups from properties
		String resString = to.getInfoStore().getValue(ProKEtProperties.POPUP);
		if (resString != null) {
			st.setAttribute("tooltip", resString);
		}

		if (to instanceof QuestionOC) {
			st.setAttribute("type", "oc");
		}
		else if (to instanceof QuestionMC) {
			st.setAttribute("type", "mc");
			// st.setAttribute("sendButton", "true");
		}
		else if (to instanceof QuestionNum) {
			st.setAttribute("type", "num");
		}
		else if (to instanceof QuestionDate) {
			st.setAttribute("type", "date");
		}
		else if (to instanceof QuestionText) {
			st.setAttribute("type", "text");
		}
		else if (to instanceof QuestionZC) {
			st.setAttribute("type", "info");
		}

		// get d3web properties
		Form current = d3webSession.getInterview().nextForm();
		Blackboard bb = d3webSession.getBlackboard();
		Value val = bb.getValue((ValueObject) to);

		/* the following handles follow-up questions that get activated */
		/* in the course of the interview (by indication) */

		// QContainer indicated
		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent) ||
				isIndicated(parent, bb)) {

			// show, if indicated follow up
			if ((D3webUtils.isFollowUpToQCon(to, parent) && isIndicated(to, bb))
					|| (!D3webUtils.isFollowUpToQCon(to, parent))) {
				st.removeAttribute("inactiveQuestion");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "");
			}
			else {
				st.setAttribute("inactiveQuestion", "true");
			}
		}

		else {
			st.setAttribute("inactiveQuestion", "true");
		}

		/*
		 * the following handles abstraction questions that get activated during
		 * the interview when previous corresponding questions are answered
		 */
		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {

			st.setAttribute("inactiveQuestion", "true");
			st.setAttribute("abstractQuestion", "true");

			// check whether abstraction question has been implicitly answered
			// by other, corresponding questions
			if (val != null && UndefinedValue.isNotUndefinedValue(val) &&
					!val.equals(Unknown.getInstance())) {
				st.removeAttribute("inactiveQuestion");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "question-d");
			}
		}

		// check answer - if answered, mark question as done
		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.removeAttribute("qstate");
			st.setAttribute("qstate", "question-d");
		}
		else
		// currently active question (next interview object)
		// is marked highlighted
		if (current != null && current.getInterviewObject() != null
				&& current.getInterviewObject().equals(to)) {
			st.setAttribute("qstate", "question-c");
		}

		// underneath="within" a rendered question, always answers are rendered
		super.renderChoices(st, cc, to, parent, d3webSession);

		sb.append(st.toString());

		// handle tables for multi-column styles
		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}

}
