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
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
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
public class ImageQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

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
				super.getTemplateName("ImageQuestionD3web"), "html");

		// set some basic properties
		st.setAttribute("fullId", "q_" + to.getName().replace(" ", "_"));
		st.setAttribute("title", to.getName());
		st.setAttribute("count", D3webConnector.getInstance().getQuestionCount());

		// TODO extend for HERNIA
		// read html popups from properties
		String resString = to.getInfoStore().getValue(ProKEtProperties.POPUP);
		if (resString != null) {
			st.setAttribute("tooltip", resString);
		}

		// get d3web properties
		Session sess = super.d3webSession;
		Form current = sess.getInterview().nextForm();
		Blackboard bb = sess.getBlackboard();
		Value val = bb.getValue((ValueObject) to);

		// QContainer indicated
		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent) ||
				isIndicated(parent, bb)) {

			// show, if indicated follow up
			if ((D3webUtils.isFollowUpTOinQCon(to, parent) && isIndicated(to, bb))
					|| (!D3webUtils.isFollowUpTOinQCon(to, parent))) {
				st.removeAttribute("inactive");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "");
			}
			else {
				st.setAttribute("inactive", "true");
			}
		}

		else {
			st.setAttribute("inactive", "true");
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

		String[] split;
		String imgName = "";
		String width = "";
		String desc = to.getInfoStore().getValue(MMInfo.DESCRIPTION);
		if (desc.contains("IMG#####")) {
			split = desc.split("WIDTH#####");
			imgName = split[0].replace("IMG#####", "");

			if (split.length == 2) {
				width = split[1];
			}

		}
		st.setAttribute("image", imgName);
		st.setAttribute("width", width);

		// read clicable-area coordinates from KB
		String areas = getChoiceAreas(to);
		st.setAttribute("areas", areas);

		if (to instanceof QuestionMC) {
			st.setAttribute("sendButton", "true");
		}

		// underneath="within" a rendered question, always answers are rendered
		super.renderChoices(st, cc, to, parent);

		sb.append(st.toString());

		// handle tables for multi-column styles
		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}

	/**
	 * Get all the defined clickable areas of an image question and assemble
	 * them to represent a html area map String: <area shape="circle"
	 * coords="110,110,25" alt="Bremen.gif" href="Bremen.html" /> Therefore, a
	 * basic StringTemplate representing one area tag is filled with coords and
	 * shape.
	 * 
	 * @created 21.04.2011
	 * @param to The terminologyObject (usually a question) the areas are
	 *        defined for
	 * @return HTML area map representing String
	 */
	protected String getChoiceAreas(TerminologyObject to) {

		StringBuilder bui = new StringBuilder();

		// imagequestions make sense for choice questions only so far
		if (to instanceof QuestionChoice) {

			for (Choice c : ((QuestionChoice) to).getAllAlternatives()) {

				// basic StringTemplate file the shape and coords are inserted
				// into
				StringTemplate st = TemplateUtils.getStringTemplate(
						getTemplateName("ImageAnswerD3web"), "html");

				st.setAttribute("fullId", c.getName().replace(" ", "_"));

				// if description = shape and coords for current Choice exist
				if (c.getInfoStore().getValue(MMInfo.DESCRIPTION) != null) {

					// split shape and coords
					String[] asplit =
							c.getInfoStore().getValue(MMInfo.DESCRIPTION).split("SHAPE");

					if (!asplit[0].equals("")) {
						st.setAttribute("coords", asplit[0]);
					}
					if (!asplit[1].equals("")) {
						st.setAttribute("shape", asplit[1]);
					}

					// if no description given at all or part of the description
					// is empty String then set defaults to avoid HTML errors
				}
				else {
					st.setAttribute("coords", "0,0,0,0");
					st.setAttribute("shape", "rect");
				}
				bui.append(st.toString());
			}
		}
		return bui.toString();
	}

}
