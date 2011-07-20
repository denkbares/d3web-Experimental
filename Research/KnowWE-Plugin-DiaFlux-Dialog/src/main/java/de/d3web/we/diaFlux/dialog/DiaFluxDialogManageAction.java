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

import java.io.IOException;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;

/**
 * 
 * @author Florian Ziegler
 * @created 20.06.2011
 */
public class DiaFluxDialogManageAction extends AbstractAction {

	public static String DIAFLUXDIALOG_ENDOFDIALOG = "ENDOFDIALOG";

	@Override
	public void execute(UserActionContext context) throws IOException {
		String type = context.getParameter("type");

		// add active flowchart via manager
		if (type.equalsIgnoreCase("addActiveFlowchart")) {
			String name = context.getParameter("name");
			DiaFluxDialogManager.getInstance().addActiveFlowchart(name);
		}

		// get flowchart
		else if (type.equalsIgnoreCase("getNextActiveFlowchart")) {
			int size = DiaFluxDialogManager.getInstance().getActiveFlowcharts().size();

			if (size > 1) {
			String current = DiaFluxDialogManager.getInstance().getActiveFlowchart();
			String next = DiaFluxDialogManager.getInstance().getNextActiveFlowchart();
			String path = DiaFluxDialogUtils.createPathStringForRequest();
			context.getWriter().write(
					next + DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR + current + path);
			} else {
				context.getWriter().write(DIAFLUXDIALOG_ENDOFDIALOG);
			}
		}

	}

}
