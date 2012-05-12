/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class NoAutoStartTest extends AbstractAnomalyTest {

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	// String articleName = getParameter(0);
	// String config = "knowledge base article: " + articleName;
	//
	// KnowledgeBase kb =
	// D3webUtils.getKnowledgeBase(
	// Environment.DEFAULT_WEB, articleName);
	//
	// CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
	//
	// if (null != kb) {
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	// boolean noAuto = true;
	// for (Flow flow : flowcharts) {
	// noAuto &= !flow.isAutostart();
	// }
	//
	// if (noAuto) {
	// res = new CITestResult(Type.FAILED, "No Autostart-Flowchart", config);
	// }
	// }
	//
	// return res;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		String errormsg = "";
		boolean noAuto = true;
		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				noAuto &= !flow.isAutostart();
			}
		}
		if (noAuto) errormsg = "No Autostart-Flowchart";
		return errormsg;
	}
}
