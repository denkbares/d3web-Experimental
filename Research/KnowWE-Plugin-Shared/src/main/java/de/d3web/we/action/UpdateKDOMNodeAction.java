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

package de.d3web.we.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * <p>
 * UpdateTableKDOMNodes class.
 * </p>
 *
 *
 * @author smark
 * @see KnowWEAction
 */
public class UpdateKDOMNodeAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		String web = context.getWeb();
		String text = URLDecoder.decode(context.getParameter(KnowWEAttributes.TARGET));
		String name = context.getTopic();
		String id = context.getParameter(KnowWEAttributes.SECTION_ID);

		String newSourceText = "";
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);

		if (text != "") {
			if (id != null) {
				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(id, text);
				newSourceText = mgr.replaceKDOMNodesWithoutSave(context, name, nodesMap);
				KnowWEEnvironment.getInstance().getWikiConnector().writeArticleToWikiEnginePersistence(
						name, newSourceText, context);
			}
		}
		return "done";
	}
}
