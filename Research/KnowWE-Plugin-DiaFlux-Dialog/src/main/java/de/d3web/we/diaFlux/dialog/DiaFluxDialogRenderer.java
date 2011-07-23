/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.diaFlux.dialog;

import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * @author Florian Ziegler
 * @created 08.06.2011
 */
public class DiaFluxDialogRenderer extends DefaultMarkupRenderer<DiaFluxDialogType> {

	@Override
	public void renderContents(KnowWEArticle article, Section<DiaFluxDialogType> sec, UserContext user, StringBuilder string) {
		String master = DiaFluxDialogType.getMaster(sec);
		if (master == null) {
			master = article.getTitle();
		}
		StringBuilder html = new StringBuilder();
		html.append("<div>");
		html.append("<h2>DiaFluxDialog</h2>");
		html.append("<div id=\"Buttons\">");
		html.append("<div id=\"loadSessionParent\"></div>");
		html.append("<div id=\"compareSessionParent\"></div>");
		html.append("<div id=\"sessionNavigator\">");
		html.append("<div id=\"nextStep\" onclick=\"DiaFluxDialog.Session.nextStep();\"></div>");
		html.append("<div id=\"playSession\" onclick=\"DiaFluxDialog.Session.playSession();\"></div>");
		html.append("</div></div>");

		DiaFluxDialogManager.getInstance().resetActiveFlowcharts();
		String hiddenPathDiv = createPathDiv();
		
		FlowSet flowSet = DiaFluxDialogUtils.getFlowSet(master, user);

		StringBuilder flowchart = new StringBuilder();

		// TODO observer
		String fc = "";
		String name = "";
		for (Flow flow : flowSet) {
			if (!flow.isAutostart()) {
				continue;
			}
			name = flow.getName();
			flowchart.append("<div id=\"DiaFluxDialogFlowchart\"></div>");
			flowchart.append(DiaFluxDialogUtils.extraxtMinimalFlowchart(DiaFluxDialogUtils.extractFlowchartRendererFromFlow(
					flow, user)));
			String flowPart1 = flowchart.substring(0, flowchart.indexOf("</script>"));
			String flowPart2 = flowchart.substring(flowchart.indexOf("</script>"));
			fc = flowPart1 + "DiaFluxDialog.start();" + flowPart2;

		}
		html.append("<div id=\"DiaFluxDialogPath\">Path: " + name + "</div>");
		html.append("<input type=\"hidden\" id=\"hiddenMaster\" value=\"" + master + "\">");
		html.append("<div id=\"hiddenSessions\"></div>");
		html.append(hiddenPathDiv);
		html.append(fc);
		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

	/**
	 * creates the path
	 * 
	 * @created 13.07.2011
	 * @return
	 */
	private static String createPathDiv() {
		String hiddenPathDiv = "";
		DiaFluxDialogSession session = DiaFluxDialogManager.getInstance().getSession();

		// already answered questions -> recreate state
		if (session.getPath().size() > 0) {
			String hiddenPath = DiaFluxDialogUtils.createHiddenSessionContent(session, false);

			hiddenPathDiv = "<input type=\"hidden\" id=\"hiddenPath\" value=\"" + hiddenPath
					+ "\">";

		}
		return hiddenPathDiv;
	}


}
