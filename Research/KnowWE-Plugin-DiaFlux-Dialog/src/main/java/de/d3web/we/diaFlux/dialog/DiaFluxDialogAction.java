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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.utils.D3webUtils;

/**
 * 
 * @author Florian Ziegler
 * @created 01.06.2011
 */
public class DiaFluxDialogAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) throws IOException {
		String web = context.getWeb();
		String master = context.getParameter("master");
		String question = context.getParameter("question");
		String selectedValues = context.getParameter("selectedValues");
		KnowledgeBase kb = D3webModule.getAD3webKnowledgeServiceInTopic(
				context.getWeb(), master);

		Session session = D3webUtils.getSession(master, context.getUserName(), context.getWeb());
		Blackboard blackboard = session.getBlackboard();



		context.getWriter().write("lolrofllol");
	}
}