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
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

public class AnswerUnknownD3webRenderer extends AbstractD3webRenderer implements AnswerD3webRenderer {

	@Override
	/**
	 * Specifically adapted for Unknown Option rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, Session d3webSession, Choice c,
			TerminologyObject to, TerminologyObject parent) {

		StringBuilder sb = new StringBuilder();
		StringTemplate st = null;

		// Get the fitting template.
		st = TemplateUtils.getStringTemplate(
					super.getTemplateName("UnknownTabular"), "html");

		// set basic properties
		st.setAttribute("fullId", getID(new Choice("Unknown")));// .getName().replace(" ",
																// "_"));
		st.setAttribute("parentFullId", getID(parent));// .getName().replace(" ",		// "_"));

                st.setAttribute("title", D3webUtils.getUnknownPrompt()); 
                
		st.setAttribute("count", D3webConnector.getInstance().getID(to));

		Blackboard bb = d3webSession.getBlackboard();
		Value value = bb.getValue((ValueObject) to);

		// abstraction question --> readonly
		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
			st.setAttribute("readonly", "true");
			st.setAttribute("inactive", "true");
		}

		if (c != null) {
			String resString = c.getInfoStore().getValue(ProKEtProperties.POPUP);
			if (resString != null) {
				st.setAttribute("tooltip", resString);
			}
		}

		// QContainer indicated
		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent) ||
				isIndicated(parent, bb)) {

			// show, if indicated follow up
			if ((D3webUtils.isFollowUpToQCon(to, parent) && isIndicated(to, bb))
					|| (!D3webUtils.isFollowUpToQCon(to, parent))) {
				st.removeAttribute("inactive");
				st.removeAttribute("readonly");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "");
			}
			else {
				st.setAttribute("inactive", "true");
				st.setAttribute("readonly", "true");
			}
		}

		else {
			st.setAttribute("inactive", "true");
			st.setAttribute("readonly", "true");
		}

		if (value.equals(Unknown.getInstance())) {
			// set the selected OC as selected in the next round
			// st.setAttribute("selection", "true");
			st.setAttribute("selection", "checked=\"checked\"");
		}
		else {
			st.removeAttribute("selection");
			st.setAttribute("selection", "");
		}

		sb.append(st.toString());

		super.makeTables(to, to, cc, sb);

		return sb.toString();
	}

}
