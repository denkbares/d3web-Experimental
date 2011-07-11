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

import java.util.LinkedList;

import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * @author Florian Ziegler
 * @created 08.06.2011
 */
public class DiaFluxDialogRenderer extends KnowWEDomRenderer<DiaFluxDialogType> {

	@Override
	public void render(KnowWEArticle article, Section<DiaFluxDialogType> sec, UserContext user, StringBuilder string) {
		String master = DiaFluxDialogType.getMaster(sec);
		if (master == null) {
			master = article.getTitle();
		}
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"defaultMarkup\">");
		html.append("<h2>DiaFluxDialog</h2>");
		html.append("<div onclick=\"DiaFluxDialog.reset('" + sec.getID() + "');\">reset</div>");

		DiaFluxDialogManager manager = DiaFluxDialogManager.getInstance();
		LinkedList<DiaFluxDialogQuestionFindingPair> path = manager.getExactPath();

		manager.resetActiveFlowcharts();

		String hiddenPathDiv = "";
		// already answered questions -> recreate state
		if (path.size() > 0) {
			StringBuilder hiddenPath = new StringBuilder();
			for (DiaFluxDialogQuestionFindingPair pair : path) {
				hiddenPath.append(pair.getQuestion());
				hiddenPath.append("+++++");
					for (String s : pair.getFinding()) {
						hiddenPath.append(s);
						hiddenPath.append("+-+-+");
					}
				hiddenPath = hiddenPath.delete(hiddenPath.length() - 5, hiddenPath.length());
				hiddenPath.append(DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR);
			}
			

			hiddenPathDiv = "<input type=\"hidden\" id=\"hiddenPath\" value=\"" + hiddenPath
					+ "\">";

		}
		
		
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
		html.append(hiddenPathDiv);
		html.append(fc);
		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
