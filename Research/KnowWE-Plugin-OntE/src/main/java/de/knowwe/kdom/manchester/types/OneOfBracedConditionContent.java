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

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.helper.BracedConditionContent;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.utils.SplitUtility;

/**
 * A content element enclosed in curly brackets is a {@link OWLObjectOneOf}
 * element in the ontology and should be handled accordingly.
 *
 * Copied from {@link BracedConditionContent}
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class OneOfBracedConditionContent extends NonTerminalCondition {

	@Override
	protected void init() {
		this.sectionFinder = new OneOfConditionContentFinder();
	}

	class OneOfConditionContentFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			String trimmed = text.trim();
			int leadingSpaces = text.indexOf(trimmed);
			if (trimmed.startsWith(Character.toString(OneOfBracedCondition.CURLY_BRACKET_OPEN))) {
				int closingBracket = SplitUtility.findIndexOfClosingBracket(trimmed, 0,
							OneOfBracedCondition.CURLY_BRACKET_OPEN,
						OneOfBracedCondition.CURLY_BRACKET_CLOSED);

				return SectionFinderResult.createSingleItemList(new SectionFinderResult(
							leadingSpaces + 1, closingBracket));

			}
			return null;
		}

	}
}