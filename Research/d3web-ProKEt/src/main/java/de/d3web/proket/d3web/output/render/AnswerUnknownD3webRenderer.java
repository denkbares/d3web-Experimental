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
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

public class AnswerUnknownD3webRenderer extends D3webRenderer {

	@Override
	/**
	 * Specifically adapted for Unknown Option rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		StringBuilder sb = new StringBuilder();
		StringTemplate st = null;

		// Get the fitting template.
		st = TemplateUtils.getStringTemplate(
					super.getTemplateName("UnknownTabular"), "html");

		// set basic properties
		st.setAttribute("fullId", to.getName().replace(" ", "_"));
		st.setAttribute("parentFullId", parent.getName().replace(" ", "_"));

		Blackboard bb = super.d3webSession.getBlackboard();
		Value value = bb.getValue((ValueObject) to);

		// abstraction question --> readonly
		if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
			st.setAttribute("readonly", "true");
			st.setAttribute("inactive", "true");
		}

		// question has parents, e.g. direct qcontainer or question parent
		// then readonly state depends on parent state
		else if (to.getParents() != null &&
				to.getParents().length != 0) {

			if (to.getParents()[0] instanceof QContainer) {
				if (!isParentIndicated(to, bb)) {
					st.setAttribute("readonly", "true");
					st.setAttribute("inactive", "true");
				}
			}
			else if (to.getParents()[0] instanceof Question) {
				if (isIndicated(to, bb) ||
						(isParentOfFollowUpQuIndicated(to, bb) &&
							isIndicated(to, bb))) {
					st.removeAttribute("readonly");
					st.removeAttribute("inactive");
				}
				else {
					st.setAttribute("readonly", "true");
					st.setAttribute("inactive", "true");

						// also remove possible set values
					st.removeAttribute("selection");
					st.setAttribute("selection", "");
				}
			}
		}
		else {
			st.removeAttribute("readonly");
			st.removeAttribute("inactive");
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

		// if (!isIndicated(parent, bb)) {
		// st.setAttribute("readonly", "true");
		// }

		sb.append(st.toString());

		super.makeTables(to, to, cc, sb);

		return sb.toString();
	}
}
