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
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
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
public class AnswerTextD3webRenderer extends AbstractD3webRenderer implements AnswerD3webRenderer {

	@Override
	/**
	 * Specifically adapted for rendering NumAnswers
	 */
	public String renderTerminologyObject(ContainerCollection cc, Session d3webSession, Choice c,
			TerminologyObject to, TerminologyObject parent) {

		QuestionText tq = (QuestionText) to;

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, else the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
					super.getTemplateName("TextAnswer"), "html");

		st.setAttribute("fullId", getID(tq));
		st.setAttribute("realAnswerType", "text");
		st.setAttribute("parentFullId", getID(parent));

		Boolean useAsFilename = to.getInfoStore().getValue(
				ProKEtProperties.USE_AS_FILENAME);

		if (useAsFilename != null && useAsFilename) {
			st.setAttribute("useasfilename", true);
		}

		Blackboard bb = d3webSession.getBlackboard();
		Value value = bb.getValue((ValueObject) to);

		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
			st.setAttribute("readonly", "true");
		}

		String dropdownMenuOptions = to.getInfoStore().getValue(
				ProKEtProperties.DROPDOWN_MENU_OPTIONS);

		if (dropdownMenuOptions != null) {
			String dropdownDefault = to.getInfoStore().getValue(
					ProKEtProperties.DROPDOWN_MENU_DEFAULT);
			if (dropdownDefault == null) dropdownDefault = "Please select...";
			String dropdownMenu = "<select  type='text'>"
					+ createDropDownOptions(dropdownDefault, value.toString(),
							dropdownMenuOptions.split(",")) + "<select/>";
			st.setAttribute(
					"dropdown_menu", dropdownMenu);
		}
		else {
			// QContainer indicated
			if ((bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent)
					|| isIndicated(parent, bb))
					&& ((D3webUtils.isFollowUpToQCon(to, parent) && isIndicated(to, bb))
						|| (!D3webUtils.isFollowUpToQCon(to, parent)))) {
				st.removeAttribute("readonly");
			}
			else {
				st.setAttribute("readonly", "true");
				// also remove possible set values
				st.removeAttribute("selection");
				st.setAttribute("selection", "");
			}

			if (value != null && UndefinedValue.isNotUndefinedValue(value)
					&& !value.equals(Unknown.getInstance())) {
				st.setAttribute("selection", value);
			}
			else if (value.equals(Unknown.getInstance())
					|| UndefinedValue.isUndefinedValue(value)) {
				st.removeAttribute("selection"); // don't want to have
													// "undefined"
				st.setAttribute("selection", ""); // displayed in the input
													// field
			}
		}

		// Description of the input to provide is read from the knowledge base
		st.setAttribute("text", tq.getInfoStore().getValue(MMInfo.DESCRIPTION));

		sb.append(st.toString());

		super.makeTables(to, to, cc, sb);

		return sb.toString();
	}

	protected String createDropDownOptions(String dropdownDefault, String selectedValue, String... options) {
		StringBuilder builder = new StringBuilder();
		if (dropdownDefault != null) {
			builder.append("<option>" + dropdownDefault + "</option>\n");
		}
		for (String option : options) {
			option = option.trim();
			builder.append("<option value='" + option + "'"
					+ (option.equals(selectedValue) ? "selected='selected'" : "")
					+ ">" + option
					+ "</option>\n");
		}
		return builder.toString();
	}
}
