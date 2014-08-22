package de.knowwe.ontology.turtle.edit;

import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.ontology.turtle.Object;
import de.knowwe.ontology.turtle.ObjectList;
import de.knowwe.ontology.turtle.PredicateSentence;
import de.knowwe.termbrowser.DefaultMarkupDragDropInserter;


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
public class PredicateDragDropInserter extends DefaultMarkupDragDropInserter {



	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Section<?> findSectionToBeReplaced(Section<?> s) {
		return Sections.ancestor(s,
				PredicateSentence.class);
	}

	@Override
	protected String createReplaceTextForSelectedSection(Section<?> section, String dropText) {
		String[] split = dropText.split("#");

		String shortURI = split[0] + ":" + Strings.encodeURL(split[1]);
		List<Section<Object>> objects = Sections.successors(section, Object.class);
		String replaceText = null;
		if (objects == null || objects.size() == 0) {
			/*
			 * there was no turtle object yet, so just append
			 */
			replaceText = section.getText() + " " + shortURI;
		}
		else {
			/*
			 * insert as last object
			 */
			replaceText = createReplaceTextPredicateSentence(section, shortURI);
		}
		return replaceText;
	}

	private String createReplaceTextPredicateSentence(Section<?> section, String appendName) {
		List<Section<?>> children = section.getChildren();
		String result = "";
		for (Section<? extends Type> child : children) {
			if (child.get() instanceof ObjectList) {
				String appendText = ", " + appendName;
				result += child.getText() + appendText;
			}
			else {
				result += child.getText();
			}
		}
		return result;
	}
}
