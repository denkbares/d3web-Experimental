/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.hermes.action;

import de.d3web.we.action.DeprecatedAbstractKnowWEAction;
import de.d3web.we.core.KnowWEParameterMap;

public class RenderConceptRelationPropositionsAction extends DeprecatedAbstractKnowWEAction {

	@Override
	public String perform(KnowWEParameterMap parameterMap) {

		String[] rels = {
				"bla", "blubb", "concept mismatch", "dont ask agein" };

		StringBuffer buffy = new StringBuffer();

		buffy.append("<div class=\"semContents\" >");
		buffy.append("<div class=\"questionsheet-layer\">");

		for (String string : rels) {

			String rqst = "KnowWE.jsp" + "?action=setRelation&articleName="
					+ java.net.URLEncoder.encode(parameterMap.getTopic())
					+ "&ObjectID=" + "conceptName" + "&ValueID="
					+ string;

			buffy.append("<INPUT TYPE='radio' NAME='f" + "timestampid" + "id"
					+ string + "' " + "value='"
					+ string + "'" + "id='semanooc"
					+ string + "' " + "rel=\"{url: '" + rqst
					+ "'}\" ");

			buffy.append("class='semano_oc'");
			buffy.append(">" + string + "<br />");

		}

		buffy.append("</div>");
		buffy.append("</div>");
		return buffy.toString();
	}

}
