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

import de.d3web.core.session.Session;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;
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
		String topic = sec.getTitle();
		Session session = D3webUtils.getSession(master, user, article.getWeb());
		StringBuilder html = new StringBuilder();
		html.append("<h2>DiaFluxDialog</h2>");
		html.append("<input type=\"hidden\" id=\"hiddenMaster\" value=\"" + master + "\">");

		// if (!DiaFluxUtils.isFlowCase(session)) {
		// string.append("No Flowchart found.");
		// return;
		// }
		//
		// List<Section<FlowchartType>> flows = new
		// ArrayList<Section<FlowchartType>>();
		//
		// FlowSet flowSet = DiaFluxUtils.getFlowSet(session);
		//
		// for (Flow flow : flowSet) {
		//
		// String origin = flow.getInfoStore().getValue(
		// Property.getProperty(FlowchartSubTreeHandler.ORIGIN_KEY,
		// String.class));
		//
		// if (origin == null) continue;
		//
		// Section<FlowchartType> node = (Section<FlowchartType>)
		// KnowWEEnvironment.getInstance().getArticleManager(
		// article.getWeb()).findNode(origin);
		// flows.add(node);
		// String s = FlowchartUtils.createFlowchartRenderer(node, user);
		// html.append(s);
		// }


		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
