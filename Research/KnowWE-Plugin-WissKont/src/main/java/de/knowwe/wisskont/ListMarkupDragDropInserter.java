/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.termbrowser.DragDropEditInserter;

/**
 * 
 * @author jochenreutelshofer
 * @created 04.06.2013
 */
public class ListMarkupDragDropInserter implements DragDropEditInserter<RelationMarkup> {

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException {
		if (Sections.hasType(s, RelationMarkup.class)) {
			Section<RelationMarkup> section = Sections.cast(s, RelationMarkup.class);

			String replaceText = createReplaceText(section, droppedTerm);

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

		return "error on drop insertion - wrong type";
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
				String appendText = ", " + termname;
				if (contentSection.getText().trim().length() == 0) {
					appendText = termname; // if there is none yet, no comma
											// needed
				}
				result += contentSection.getText() + appendText;
			}
			else {
				result += child.getText();
			}
		}
		return result;
	}

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<RelationMarkup> getTypeClass() {
		return RelationMarkup.class;
	}

}
