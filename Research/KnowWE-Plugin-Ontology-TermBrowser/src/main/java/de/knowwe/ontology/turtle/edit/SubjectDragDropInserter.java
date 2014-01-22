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
package de.knowwe.ontology.turtle.edit;

import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.ontology.turtle.PredicateObjectSentenceList;
import de.knowwe.ontology.turtle.PredicateSentence;
import de.knowwe.ontology.turtle.TurtleSentence;
import de.knowwe.termbrowser.DefaultMarkupDragDropInserter;

public class SubjectDragDropInserter extends DefaultMarkupDragDropInserter {


	private String createReplaceTextPredicateSentence(Section<?> section, String appendName) {
		List<Section<? extends Type>> children = section.getChildren();
		String result = "";
		for (Section<? extends Type> child : children) {
			if (child.get() instanceof PredicateObjectSentenceList) {
				String appendText = "; \r\n" + appendName;
				result += child.getText() + appendText;
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
	protected Section<?> findSectionToBeReplaced(Section<?> s) {
		Section<TurtleSentence> sentence = Sections.findAncestorOfType(s,
				TurtleSentence.class);
		return sentence;
	}

	@Override
	protected String createReplaceTextForSelectedSection(Section<?> section, String dropText) {

		String[] split = dropText.split("#");

		String shortURI = split[0] + ":" + Strings.encodeURL(split[1]);

		List<Section<PredicateSentence>> predSentences = Sections.findSuccessorsOfType(section,
				PredicateSentence.class);
		String replaceText = null;
		if (predSentences == null || predSentences.size() == 0) {
			replaceText = section.getText() + " " + shortURI;
		}
		else {
			replaceText = createReplaceTextPredicateSentence(section, shortURI);
		}

		return replaceText;
	}

}
