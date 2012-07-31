/*
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

package de.d3web.we.ci4ke.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.testing.ArgsCheckResult;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.Test;

/**
 * Anomaly detection: test for automatically starting flowcharts with more than
 * one start node
 * 
 * @author Gritje Meinke
 * @created 17.04.2011
 */
public class AnomalyAutostartFlow implements Test<KnowledgeBase> {

	@Override
	public Message execute(KnowledgeBase kb, String[] args) {

		Message res = new Message(Type.SUCCESS, null);
		StringBuffer buf = new StringBuffer();

		if (kb != null) {

			// get all flowcharts
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);

			for (Flow flow : flowcharts) {

				if (flow.isAutostart()) {

					List<StartNode> startNodes = flow.getStartNodes();
					if (startNodes.size() > 1) {
						buf.append("FLOWCHART: " + flow.getName() + " Startnodes: "
								+ String.valueOf(startNodes.size()) + "\n");
					}
				}

			} // end for each flowchart

		} // end if KB

		if (buf.length() > 0) {
			res = new Message(
					Type.FAILURE,
							"automatically starting flowcharts with more than one startnode found: \n"
									+ buf.toString());
		}

		return res;

	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {
		return new ArgsCheckResult(args);
	}

	@Override
	public Class<KnowledgeBase> getTestObjectClass() {
		return KnowledgeBase.class;
	}
	
	@Override
	public String getDescription() {
		return "No description available";
	}
}
