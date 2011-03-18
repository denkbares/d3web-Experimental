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
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic NumAnswers.
 * 
 * TODO CHECK: 1) basic properties for answers
 * 
 * @author Martina Freiberg
 * @created 16.01.2011
 */
public class AnswerNumD3webRenderer extends D3webRenderer {

	@Override
	/**
	 * Specifically adapted for rendering NumAnswers
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		QuestionNum nq = (QuestionNum) to;

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, else the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
					super.getTemplateName("NumAnswer"), "html");

		st.setAttribute("fullId", nq.getName().replace(" ", "_"));
		st.setAttribute("realAnswerType", "num");
		st.setAttribute("parentFullId", parent.getName().replace(" ", "_"));

		// set units if available
		if (nq.getInfoStore().getValue(MMInfo.UNIT) != null) {
			st.setAttribute("text",
					nq.getInfoStore().getValue(MMInfo.UNIT));
		}

		Blackboard bb = D3webConnector.getInstance().getSession().getBlackboard();
		Value value = bb.getValue((ValueObject) to);

		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
			st.setAttribute("readonly", "true");
		}
		else if (!isParentIndicated(to, bb)) {
			st.setAttribute("readonly", "true");
		}
		else if (to.getParents() != null && to.getParents().length != 0
				&& to.getParents()[0] instanceof Question
				&& !isIndicated(to, bb)) {
			st.setAttribute("readonly", "true");
		}
		else {
			st.removeAttribute("readonly");
		}

		// / if not undefined and not unknown, value needs to be written into
		// num field
		if (value != null && UndefinedValue.isNotUndefinedValue(value)
				&& !value.equals(Unknown.getInstance())) {

			if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
				st.removeAttribute("readonly");
			}
			st.setAttribute("selection", value);
		}

		// if undefined or unknown value, erase entries from num field
		else if (UndefinedValue.isUndefinedValue(value)
				|| value.equals(Unknown.getInstance())) {
			st.removeAttribute("selection");
			st.setAttribute("selection", "");
		}

		sb.append(st.toString());

		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}
}
