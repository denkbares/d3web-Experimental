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
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.flow.FlowchartSubTreeHandler;
import de.d3web.we.flow.FlowchartUtils;
import de.d3web.we.flow.type.FlowchartType;
import de.d3web.we.kdom.Section;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;


/**
 * 
 * @author Florian Ziegler
 * @created 09.06.2011
 */
public class DiaFluxDialogUtils {

	public static final String DIAFLUXDIALOG_FLOWCHART = "DiaFluxDialogFlowchart";

	public static FlowSet getFlowSet(String master, UserContext user) {
		Session session = D3webUtils.getSession(master, user,
				user.getWeb());

		if (!DiaFluxUtils.isFlowCase(session)) {
			// No Flowchart found
			return null;
		}
		return DiaFluxUtils.getFlowSet(session);
	}

	@SuppressWarnings("unchecked")
	public static String extractFlowchartRendererFromFlow(Flow flow, UserContext user) {
		String origin = (String) FlowchartUtils.getFlowProperty(flow,
				FlowchartSubTreeHandler.ORIGIN_KEY);

		if (origin == null) {
			return null;
		}

		Section<FlowchartType> node = (Section<FlowchartType>)
				KnowWEEnvironment.getInstance().getArticleManager(
						user.getWeb()).findNode(origin);

		return FlowchartUtils.createFlowchartRenderer(node, user, DIAFLUXDIALOG_FLOWCHART);
	}

}
