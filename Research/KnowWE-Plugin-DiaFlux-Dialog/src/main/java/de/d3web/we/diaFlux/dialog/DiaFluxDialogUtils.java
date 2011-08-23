/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import java.util.Collection;
import java.util.Stack;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.flow.FlowchartSubTreeHandler;
import de.d3web.we.flow.FlowchartUtils;
import de.d3web.we.flow.type.FlowchartType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.ConnectorAttachment;

/**
 * 
 * @author Florian Ziegler
 * @created 09.06.2011
 */
public class DiaFluxDialogUtils {

	public static final String DIAFLUXDIALOG_FLOWCHART = "DiaFluxDialogFlowchart";
	public static String DIAFLUXDIALOG_SEPARATOR = "#####";

	public static FlowSet getFlowSet(String master, UserContext user) {
		Session session = D3webUtils.getSession(master, user,
				user.getWeb());

		if (!DiaFluxUtils.isFlowCase(session)) {
			// No Flowchart found
			return null;
		}
		return DiaFluxUtils.getFlowSet(session);
	}

	// TODO fix in flowchartutils vor comitten
	@SuppressWarnings("unchecked")
	public static String extractFlowchartRendererFromFlow(Flow flow, UserContext user) {
		String origin = (String) FlowchartUtils.getFlowProperty(flow,
				FlowchartSubTreeHandler.ORIGIN_KEY);

		if (origin == null) {
			return null;
		}

		Section<FlowchartType> node = (Section<FlowchartType>)
				Sections.getSection(origin);

		return KnowWEUtils.maskHTML(FlowchartUtils.createFlowchartRenderer(node, user,
				DIAFLUXDIALOG_FLOWCHART, "diafluxdialog"));
	}

	public static String extraxtMinimalFlowchart(String flowchart) {
		flowchart = KnowWEUtils.unmaskHTML(flowchart);
		// führt sonst zu komischen pfeilen
		flowchart = "<div>" + flowchart.substring(flowchart.indexOf("<xml"));

		String part1 = flowchart.substring(0, flowchart.indexOf("</flowchart>"));
		String part2 =
				flowchart.substring(flowchart.indexOf("</flowchart>"));

		// script braucht man beim rendern
		part2 = part2.substring(part2.indexOf("<script")) + "";

		return part1 + part2;
	}

	/**
	 * creates the path to the current active flowchart as a single string for
	 * the request
	 * 
	 * @created 20.06.2011
	 */
	public static String createPathStringForRequest() {
		Stack<String> stack = DiaFluxDialogManager.getInstance().getActiveFlowcharts();

		String path = DIAFLUXDIALOG_SEPARATOR;

		for (String s : stack) {
			path += s + DIAFLUXDIALOG_SEPARATOR;
		}

		return path;
	}

	public static String createHiddenSessionContent(DiaFluxDialogSession session, boolean forwardKnowledge) {
		StringBuilder bob = new StringBuilder();

		if (forwardKnowledge) {
			bob.append("[FORWARDKNOWLEDGE]");
			for (DiaFluxDialogQuestionFindingPair pair : session.getForwardKnowledge()) {
				bob.append(pair.getQuestion());
				bob.append("+++++");
				for (String s : pair.getFinding()) {
					bob.append(s);
					bob.append("+-+-+");
				}
				bob = bob.delete(bob.length() - 5, bob.length());
				bob.append(DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR);
			}
			bob.append("[PATH]");
		}

		for (DiaFluxDialogQuestionFindingPair pair : session.getPath()) {
			bob.append(pair.getQuestion());
			bob.append("+++++");
			for (String s : pair.getFinding()) {
				bob.append(s);
				bob.append("+-+-+");
			}
			bob = bob.delete(bob.length() - 5, bob.length());
			bob.append(DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR);
		}

		return bob.toString();
	}

	public static ConnectorAttachment findConnectorAttachmentWithName(Collection<ConnectorAttachment> attachments, String fileName) {
		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getFileName().equals(fileName)) {
				return attachment;
			}
		}
		return null;
	}

}
