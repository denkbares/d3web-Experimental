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
package de.d3web.we.correction.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.d3web.we.correction.ApproximateCorrectionProvider;
import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.KDOMReplaceTermNameAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * Replaces the Terms found in Markup by the {@link ApproximateCorrectionProvider}.
 * This is an adaption from {@link KDOMReplaceTermNameAction}
 * or {@link de.knowwe.d3web.action.KDOMReplaceTermNameAction}
 * 
 * @author Johannes Dienst
 * @created 25.11.2011
 */
public class KDOMReplaceTermUserSupportAction extends AbstractAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(UserActionContext context) throws IOException {

		if (context.getWriter() == null) {
			return;
		}

		String web = context.getWeb();
		String nodeID = context.getParameter(KnowWEAttributes.TARGET);
		String name = context.getTitle();
		String newText = context.getParameter(KnowWEAttributes.TEXT);
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);

		// Check for user access
		if (!KnowWEEnvironment.getInstance().getWikiConnector().userCanEditPage(name,
				context.getRequest())) {
			context.sendError(403, "You do not have the permission to edit this page.");
			return;
		}

		// Prepare new text, urldecode and strip whitespaces that JSPWiki might
		// have added
		newText = KnowWEUtils.urldecode(newText);
		newText = newText.replaceAll("\\s*$", "");

		Map<String, String> nodesMap = new HashMap<String, String>();

		Section<?> section = Sections.getSection(nodeID);

		if (!(section.get() instanceof TermReference)) {
			context.sendError(500, "Invalid section type");
			return;
		}

		TermReference t = (TermReference) section.get();
		String originalText = section.getOriginalText();
		String oldTermName = t.getTermName(section);
		String newNodeText = originalText.replace(oldTermName, newText);

		nodesMap.put(nodeID, newNodeText);
		nodesMap.put(nodeID, newText);
		Sections.replaceSections(context, nodesMap);

		context.setContentType("text/html; charset=UTF-8");
		context.getWriter().write("done");
	}
}
