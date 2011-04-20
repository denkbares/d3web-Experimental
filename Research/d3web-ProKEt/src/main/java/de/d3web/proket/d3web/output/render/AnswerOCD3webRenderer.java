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
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic OCAnswers.
 * 
 * TODO CHECK: 1) basic properties for answers 2) d3web resulting properties,
 * e.g. is indicated, is shown etc.
 * 
 * @author Martina Freiberg
 * @created 15.01.2011
 */
public class AnswerOCD3webRenderer extends D3webRenderer {

	@Override
	/**
	 * Specifically adapted for OCAnswer rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, Choice c,
			TerminologyObject to) {

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		StringTemplate st = null;

		// get the template. In case user prefix was specified, the specific
		// TemplateName is returned, otherwise the base object name.
		st = TemplateUtils.getStringTemplate(
				super.getTemplateName("OcAnswerTabular"), "html");

		st.setAttribute("fullId", c.getName().replace(" ", "_"));
		st.setAttribute("realAnswerType", "oc");
		st.setAttribute("parentFullId", to.getName().replace(" ", "_"));
		st.setAttribute("text", c.getName());

		Blackboard bb = super.d3webSession.getBlackboard();
		Value value = bb.getValue((ValueObject) to);

		// if question is an abstraction question --> readonly
		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
			st.setAttribute("readonly", "true");
			st.setAttribute("inactive", "true");
		}
		// if to=question has parents readonly state depends on parent
		else if (to.getParents()[0] != null &&
				to.getParents().length != 0) {

			// if direct parent is qcontainer
			// if the qcontainer is not indicated --> question readonly
			if (to.getParents()[0] instanceof QContainer) {
				if (!isParentIndicated(to, bb)) {
					st.setAttribute("readonly", "true");
					st.setAttribute("inactive", "true");
				}
			}
			// if direct parent is question and if the to=question is
			// follow up question of one of parents answers
			else if (to.getParents()[0] instanceof Question) {

				// if parent is indicated, to indicated too --> no readonly
				if (isIndicated(to, bb) ||
						(isParentOfFollowUpQuIndicated(to, bb) &&
							isIndicated(to, bb))) {
					st.removeAttribute("readonly");
					st.removeAttribute("inactive");
				} 
				// otherwise to=question not indicated and readonly
				else {
					st.setAttribute("readonly", "true");
					st.setAttribute("inactive", "true");

					// also remove possible set values
					st.removeAttribute("selection");
					st.setAttribute("selection", "");
				}
			}
		}
		// in all other cases display question/answers --> no readonly
		else {
			st.removeAttribute("readonly");
			st.removeAttribute("inactive");
		}

		// else if (!isParentIndicated(to, bb)) {
		// st.setAttribute("readonly", "true");

			// if parent is a question and current to was a follow up
			// question of parent and parent is indicated --> no readonly
		// if (isParentOfFollowUpQuIndicated(to, bb)) {
		// st.removeAttribute("readonly");
		// }
		// }
		// else if (to.getParents() != null && to.getParents().length != 0
		// && to.getParents()[0] instanceof Question
		// && !isIndicated(to, bb)) {
		// st.setAttribute("readonly", "true");

			// st.removeAttribute("selection");
		// st.setAttribute("selection", "");
		// }

		// if value of the to=question equals this choice
		if (value.toString().equals(c.toString())) {

			// if to=question is abstraction question, was readonly before, but
			// value
			// has been set (e.g. by other answer & Kb indication), remove
			// readonly
			if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
				st.removeAttribute("readonly");
				st.removeAttribute("inactive");
			}

			// set selected
			st.removeAttribute("selection");
			st.setAttribute("selection", "checked=\'checked\'");
		}

		// if value of question is undefined or unknown remove previous choice
		else if (UndefinedValue.isUndefinedValue(value)
				|| value.equals(Unknown.getInstance())) {
			st.removeAttribute("selection");
			st.setAttribute("selection", "");
		}

		sb.append(st.toString());

		super.makeTables(c, to, cc, sb);

		return sb.toString();
	}
}
