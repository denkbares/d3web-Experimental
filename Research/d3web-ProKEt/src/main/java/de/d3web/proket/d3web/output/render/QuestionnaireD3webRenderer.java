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
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic Questionnaires.
 * 
 * TODO CHECK: 1) basic properties for questionnaires 2) d3web resulting
 * properties, e.g. is indicated, is shown etc.
 * 
 * @author Martina Freiberg
 * @created 15.01.2011
 */
public class QuestionnaireD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

	@Override
	/**
	 * Adapted specifically for questionnaire rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, otherwise, the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
				super.getTemplateName("Questionnaire"), "html");

		st.setAttribute("fullId", getID(to));// to.getName().replace(" ",
												// "_"));
		st.setAttribute("title", to.getName());
		st.setAttribute("count", D3webConnector.getInstance().getID(to));

		String resString = to.getInfoStore().getValue(ProKEtProperties.POPUP);
		if (resString != null) {
			st.setAttribute("tooltip", resString);
		}

		Blackboard bb = AbstractD3webRenderer.d3webSession.getBlackboard();

		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(to)
				|| isIndicated(to, bb)) {

			// st.removeAttribute("questInactive");
			st.removeAttribute("hidden");
		}
		else {
			// st.setAttribute("questInactive", "true");
			st.setAttribute("hidden", "true");
		}

		// render the children
		super.renderChildren(st, cc, to);

		sb.append(st.toString());

		// make tables "around" the to
		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}

}
