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
package de.knowwe.onte.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.d3web.strings.Strings;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.KDOMReplaceTermNameAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.types.MisspelledSyntaxConstruct;

/**
 * Simply an adaption of the {@link KDOMReplaceTermNameAction}. The adaption was
 * necessary since we wanted to replace no {@link TermReference} objects.
 * 
 * @author Stefan Mark
 * @created 11.11.2011
 */
public class KeywordReplaceAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		if (context.getWriter() == null) {
			return;
		}

		String nodeID = context.getParameter(Attributes.TARGET);
		String name = context.getParameter(Attributes.TOPIC);
		String newText = context.getParameter(Attributes.TEXT);

		// Check for user access
		if (!Environment.getInstance().getWikiConnector().userCanEditArticle(name,
				context.getRequest())) {
			context.sendError(403, "You do not have the permission to edit this page.");
			return;
		}

		// Prepare new text, urldecode and strip whitespaces that JSPWiki might
		// have added
		newText = Strings.decodeURL(newText);
		newText = newText.replaceAll("\\s*$", "");

		Map<String, String> nodesMap = new HashMap<String, String>();

		Section<?> section = Sections.get(nodeID);

		if (!(section.get() instanceof MisspelledSyntaxConstruct)) {
			context.sendError(500, "Invalid section type");
			return;
		}

		String originalText = section.getText();
		String newNodeText = originalText.replace(originalText, newText);

		nodesMap.put(nodeID, newNodeText);
		Sections.replace(context, nodesMap).sendErrors(context);

		context.setContentType("text/html; charset=UTF-8");
		context.getWriter().write("done");
	}

}
