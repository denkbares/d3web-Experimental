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
package de.knowwe.object;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.parsing.Sections.ReplaceResult;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.termbrowser.DragDropEditInserter;

public class TermDragDropInserter implements
		DragDropEditInserter {

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind,
			UserActionContext context) throws IOException {
		if (Sections.hasType(s, TermReference.class)) {

			Section<TermReference> termRefSection = Sections.cast(s,
					TermReference.class);

			Section<?> sectionToBeReplaced = termRefSection;
			String replaceText = droppedTerm;

			Map<String, String> nodesMap = new HashMap<String, String>();

			Section<DefaultMarkupType> defaultMarkupSection =
					Sections.findAncestorOfType(
							termRefSection,
							DefaultMarkupType.class);
			if (defaultMarkupSection != null) {
				sectionToBeReplaced = defaultMarkupSection;
				replaceText =
						createDefaultMarkupReplaceText(defaultMarkupSection,
								termRefSection,
								droppedTerm);
			}

			nodesMap.put(sectionToBeReplaced.getID(), replaceText);
			String result = "done";

			ReplaceResult replaceResult = Sections.replaceSections(context, nodesMap);
			replaceResult.sendErrors(context);
			Map<String, String> newSectionIDs = replaceResult.getSectionMapping();
			if (newSectionIDs != null && newSectionIDs.size() > 0) {
				Entry<String, String> entry =
						newSectionIDs.entrySet().iterator().next();
				result = entry.getKey() + "#" + entry.getValue();
				// result = newSectionIDs.values().iterator().next();
			}

			// hotfix: workaround to trigger update of the sectionID map
			DelegateRenderer.getInstance().render(termRefSection, context,
					new RenderResult(context));

			return result;

		}
		return "error on drop insertion - wrong type";
	}

	private String createDefaultMarkupReplaceText(Section<DefaultMarkupType> defaultMarkupSection, Section<TermReference> termRefSection, String droppedTerm) {
		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(termRefSection.getID(), droppedTerm);
		StringBuffer replacedText = Sections.collectTextAndReplaceNode(defaultMarkupSection,
				nodesMap);
		return replacedText.toString();
	}

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s,
			String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}


}
