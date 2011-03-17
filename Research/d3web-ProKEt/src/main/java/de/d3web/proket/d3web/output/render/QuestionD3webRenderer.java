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

import java.io.IOException;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.utils.AttachmentHandlingD3webUtils;
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
public class QuestionD3webRenderer extends D3webRenderer {

	@Override
	/**
	 * Adapted specifically for question rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		D3webConnector.getInstance().setQuestionCount(
				D3webConnector.getInstance().getQuestionCount() + 1);

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, otherwise, the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
				super.getTemplateName("Question"), "html");

		// set some basic properties
		st.setAttribute("fullId", "q_" + to.getName());
		st.setAttribute("title", to.getName());
		st.setAttribute("count", D3webConnector.getInstance().getQuestionCount());

		// handling popups, defined in textfiles in the KB
		String popupResName = "popup" + to.getName();
		System.out.println(popupResName);
		Resource res = D3webConnector.getInstance().getKb().getResource(popupResName);
		if (res != null) {
			try {
				String resString =
					AttachmentHandlingD3webUtils.getTextfileContentsFromTextfileAttachment(res);
				System.out.println(resString);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (to instanceof QuestionOC) {
			st.setAttribute("type", "oc");
		}
		else if (to instanceof QuestionMC) {
			st.setAttribute("type", "mc");
		}
		else if (to instanceof QuestionNum) {
			st.setAttribute("type", "num");
			// st.setAttribute("sendButton", "true");
		}
		else if (to instanceof QuestionDate) {
			st.setAttribute("type", "date");
		}
		else if (to instanceof QuestionText) {
			st.setAttribute("type", "text");
		}

		// get d3web properties
		Session sess = D3webConnector.getInstance().getSession();
		Form current = sess.getInterview().nextForm();
		Blackboard bb = sess.getBlackboard();
		Value val = bb.getValue((ValueObject) to);
		Indication ind = bb.getIndication((InterviewObject) to);

		to.getInfoStore().getValue(MMInfo.DESCRIPTION);

		/* the following handles follow-up questions that get activated */
		/* in the course of the interview (by indication) */
		// check if parent had been a question --> so this is follow up q
		if (to.getParents() != null && to.getParents().length != 0
				&& to.getParents()[0] instanceof Question) {

			// check whether question has been indicated so far
			if (ind.getState() == Indication.State.INDICATED
					|| ind.getState() == Indication.State.INSTANT_INDICATED) {
				st.removeAttribute("inactive");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "");
			}
			// otherwise follow-ups should be displayed inactive
			else {
				st.setAttribute("inactive", "true");
			}
		}

		/*
		 * the following handles abstraction questions that get activated during
		 * the interview when previous corresponding questions are answered
		 */
		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {

			st.setAttribute("inactive", "true");
			st.setAttribute("abstract", "true");

			// check whether abstraction question has been implicitly answered
			// by other, corresponding questions
			if (val != null && UndefinedValue.isNotUndefinedValue(val) &&
					!val.equals(Unknown.getInstance())) {
				st.removeAttribute("inactive");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "question-d");
			}
		}

		// check answer - if answered, mark question as done
		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.removeAttribute("qstate");
			st.setAttribute("qstate", "question-d");
		}

		// currently active question (next interview object)
		// is marked highlighted
		if (current != null && current.getInterviewObject() != null
				&& current.getInterviewObject().equals(to)) {
			st.setAttribute("qstate", "question-c");
		}

		// underneath="within" a rendered question, always answers are rendered
		super.renderChoices(st, cc, to);

		sb.append(st.toString());

		// handle tables for multi-column styles
		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}
}
