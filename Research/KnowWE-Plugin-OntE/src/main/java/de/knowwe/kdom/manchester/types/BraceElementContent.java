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
package de.knowwe.kdom.manchester.types;

import java.util.List;

import org.semanticweb.owlapi.model.OWLObjectOneOf;

import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.helper.BracedConditionContent;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.Strings;

/**
 * A content element enclosed in curly brackets is a {@link OWLObjectOneOf}
 * element in the ontology and should be handled accordingly.
 * 
 * Copied from {@link BracedConditionContent}
 * 
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class BraceElementContent extends NonTerminalCondition {

	public static char OPEN = Character.MAX_VALUE;
	public static char CLOSED = Character.MAX_VALUE;

	public BraceElementContent(char open, char closed) {
		OPEN = open;
		CLOSED = closed;
		this.setSectionFinder(new BraceElementContentFinder());
	}

	class BraceElementContentFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			String trimmed = text.trim();
			int leadingSpaces = text.indexOf(trimmed);
			if (trimmed.startsWith(Character.toString(OPEN))) {
				int closingBracket = Strings.findIndexOfClosingBracket(trimmed, 0,
							OPEN, CLOSED);

				return SectionFinderResult.createSingleItemList(new SectionFinderResult(
							leadingSpaces + 1, closingBracket));

			}
			return null;
		}

	}
}