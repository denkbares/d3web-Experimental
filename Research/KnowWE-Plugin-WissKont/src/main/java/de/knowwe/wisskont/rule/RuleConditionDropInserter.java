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
package de.knowwe.wisskont.rule;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.termbrowser.DragDropEditInserter;
import de.knowwe.wisskont.rule.RuleMarkup.ConditionArea;

/**
 * 
 * @author jochenreutelshofer
 * @created 08.08.2013
 */
public class RuleConditionDropInserter implements DragDropEditInserter<RuleKeyType> {

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException {
		if (Sections.hasType(s, RuleKeyType.class)) {
			Section<RuleKeyType> section = Sections.cast(s, RuleKeyType.class);
			Section<RuleMarkup> rule = Sections.findAncestorOfType(section, RuleMarkup.class);
			Section<ConditionArea> condition = Sections.findSuccessor(rule, ConditionArea.class);
			Map<String, String> nodesMap = new HashMap<String, String>();
			if (condition != null && condition.getText().trim().length() > 0) {
				String replaceText = createReplaceText(condition, droppedTerm);
				nodesMap.put(condition.getID(), replaceText);
			}
			else {
				String replaceText = section.getText() + " " + droppedTerm + " DANN";
				nodesMap.put(section.getID(), replaceText);
			}
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

	private String createReplaceText(Section<?> cond, String termname) {
		return cond.getText() + " UND " + termname;
	}

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<RuleKeyType> getTypeClass() {
		return RuleKeyType.class;
	}

}
