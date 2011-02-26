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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic NumAnswers.
 * 
 * TODO CHECK: 1) basic properties for answers 2) d3web resulting properties,
 * e.g. is indicated, is shown etc.
 * 
 * @author Martina Freiberg
 * @created 16.01.2011
 */
public class AnswerDateD3webRenderer extends D3webRenderer {

	@Override
	/**
	 * Specifically adapted for rendering NumAnswers
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		QuestionDate dq = (QuestionDate) to;

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, else the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
					super.getTemplateName("DateAnswerPure"), "html");

		st.setAttribute("fullId", dq.getName());
		st.setAttribute("realAnswerType", "date");
		st.setAttribute("parentFullId", dq.getId());

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

		if (value != null && UndefinedValue.isNotUndefinedValue(value)
				&& !value.equals(Unknown.getInstance())) {
			Date d = ((DateValue) value).getDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			String val = dateFormat.format(d).toString();
			st.setAttribute("selection", val);
		}
		else if (UndefinedValue.isUndefinedValue(value) ||
				value.equals(Unknown.getInstance())) {
			st.removeAttribute("selection"); // don't want to have "undefined"
			st.setAttribute("selection", ""); // displayed in the input field
		}

		st.setAttribute("text", "dd.mm.yyyy");

		sb.append(st.toString());

		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}
}