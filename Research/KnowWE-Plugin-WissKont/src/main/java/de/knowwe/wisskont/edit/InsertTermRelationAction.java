/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.wisskont.edit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.wisskont.RelationMarkupContentType;

/**
 * 
 * @author jochenreutelshofer
 * @created 30.11.2012
 */
public class InsertTermRelationAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	public String perform(UserActionContext context) throws IOException {
		String title = context.getTitle();
		boolean mayEdit = Environment.getInstance().getWikiConnector().userCanEditArticle(title,
				context.getRequest());
		if (mayEdit) {

			String termname = context.getParameter("termname");
			String targetIDString = context.getParameter("targetID");
			Section<?> section = Sections.getSection(targetIDString);

			if (section == null) {
				String message = "Section not found: " + targetIDString;
				Logger.getLogger(this.getClass().getName()).error(message);
				return message;

			}

			String replaceText = createReplaceText(section, termname);

			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(
					section.getID(), replaceText
					);
			String result = "done";

			Map<String, String> newSectionIDs = Sections.replaceSections(context, nodesMap);
			if (newSectionIDs != null && newSectionIDs.size() > 0) {
				// Section<?> sectionNewVersion = Sections.getSection();
				result = newSectionIDs.values().iterator().next();
			}

			// hotfix: workaround to trigger update of the sectionID map
			DelegateRenderer.getInstance().render(section, context, new
					RenderResult(context));
			return result;
		}
		else {
			return "You are not allowed to edit this page.";
		}

	}

	/**
	 * 
	 * @created 01.12.2012
	 * @param section
	 * @param termname
	 * @return
	 */
	private String createReplaceText(Section<?> section, String termname) {
		Section<RelationMarkupContentType> contentSection = Sections.findSuccessor(section,
				RelationMarkupContentType.class);
		List<Section<? extends Type>> children = section.getChildren();
		String result = "";
		for (Section<? extends Type> child : children) {
			if (child.equals(contentSection)) {
				result += contentSection.getText() + ", " + termname;
			}
			else {
				result += child.getText();
			}
		}
		return result;
	}
}
