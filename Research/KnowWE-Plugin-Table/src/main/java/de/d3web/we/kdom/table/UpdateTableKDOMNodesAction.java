/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * <p>
 * UpdateTableKDOMNodes class.
 * </p>
 * 
 * This class handles the changes in the in-view editable tables.
 * 
 * @author smark
 * @see KnowWEAction
 */
public class UpdateTableKDOMNodesAction extends AbstractAction {

	private final static String UPDATE_CELL_SEPERATOR = ";-;";

	private final static String UPDATE_NODE_SEPERATOR = "::";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) throws IOException {
		String web = context.getWeb();
		String nodes = context.getParameter(KnowWEAttributes.TARGET);
		String name = context.getTopic();

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);

		Map<String, String> nodesMap = new HashMap<String, String>();

		if (nodes != "") {
			if (nodes.contains(UPDATE_NODE_SEPERATOR)) {
				String[] tokens = nodes.split(UPDATE_NODE_SEPERATOR);
				for (String string : tokens) {
					String[] node = string.split(UPDATE_CELL_SEPERATOR);
					if (node.length == 2) {
						nodesMap.put(node[0], node[1]);
					}
				}
			}
			else {
				String[] node = nodes.split(UPDATE_CELL_SEPERATOR);
				if (node.length == 2) {
					nodesMap.put(node[0], node[1]);
				}
			}
			mgr.replaceKDOMNodesSaveAndBuild(context, name, nodesMap);
		}

		return "done";
	}
}
