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
package de.knowwe.annotation.type.header;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.annotation.type.AnnotatedConceptsType;
import de.knowwe.annotation.type.list.ConceptList;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.parsing.Sections.ReplaceResult;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.jspwiki.types.HeaderType;
import de.knowwe.termbrowser.DragDropEditInserter;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.06.2013
 */
public class HeadlineDragDropInserter implements DragDropEditInserter {

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException {
		if (Sections.hasType(s, HeaderType.class)) {

			List<Section<?>> siblings = s.getParent().getChildren();

			Section<ConceptList> list = searchExistingConceptList(s, siblings);

			Section<?> replacedSection = null;
			String replaceText = null;
			if (list != null) {
				// append to given list
				replacedSection = list;
				replaceText = createReplaceTextAppendGivenList(replacedSection, droppedTerm);
			}
			else {
				// create new list
				replacedSection = s;
				replaceText = createReplaceTextAppendNewList(replacedSection, droppedTerm);

			}

			// finally replace
			{
				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(
						replacedSection.getID(), replaceText
						);
				String result = "done";

				ReplaceResult replaceResult = Sections.replace(context, nodesMap);
				replaceResult.sendErrors(context);
				Map<String, String> newSectionIDs = replaceResult.getSectionMapping();
				if (newSectionIDs != null && newSectionIDs.size() > 0) {
					// Section<?> sectionNewVersion = Sections.get();
					result = newSectionIDs.values().iterator().next();
				}

				// hotfix: workaround to trigger update of the sectionID map
				DelegateRenderer.getInstance().render(replacedSection, context, new
						RenderResult(context));

				return result;
			}
		}

		return "error on drop insertion - wrong type";
	}

	/**
	 * 
	 * @created 23.06.2013
	 * @param s
	 * @param siblings
	 * @return
	 */
	private Section<ConceptList> searchExistingConceptList(Section<?> s, List<Section<?>> siblings) {
		// Search for existing list of concepts to append term
		Section<ConceptList> list = null;
		boolean found = false;
		for (Section<? extends Type> sibling : siblings) {
			if (sibling.equals(s)) {
				found = true;
				continue;
			}
			if (!found) {
				continue;
			}
			// after the next following header break out -> out of scope
			if (sibling.get() instanceof HeaderType) {
				break;
			}
			if (sibling.get() instanceof ConceptList) {
				list = Sections.cast(sibling, ConceptList.class);
			}
		}
		return list;
	}

	/**
	 * 
	 * @created 23.06.2013
	 * @param replacedSection
	 * @param droppedTerm
	 * @return
	 */
	private String createReplaceTextAppendGivenList(Section<?> replacedSection, String droppedTerm) {
		return replacedSection.getText() + ", " + droppedTerm;
	}

	/**
	 * 
	 * @created 23.06.2013
	 * @param replacedSection
	 * @param droppedTerm
	 * @return
	 */
	private String createReplaceTextAppendNewList(Section<?> replacedSection, String droppedTerm) {

		return replacedSection.getText() + AnnotatedConceptsType.CONCEPT_KEY + " " + droppedTerm
				+ "\n";
	}

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}



}
