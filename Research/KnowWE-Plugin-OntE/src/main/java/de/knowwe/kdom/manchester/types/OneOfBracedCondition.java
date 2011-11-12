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

import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SyntaxError;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

/**
 * A content element enclosed in curly brackets is a {@link OWLObjectOneOf}
 * element in the ontology and should be handled accordingly.
 *
 * Copied from {@link BracedCondition}
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class OneOfBracedCondition extends NonTerminalCondition {

	public static final char CURLY_BRACKET_OPEN = '\u007B';
	public static final char CURLY_BRACKET_CLOSED = '\u007D';

	@Override
	protected void init() {
		this.sectionFinder = OneOfExpressionFinder.createEmbracedExpressionFinder();
	}
}

/**
 * @see Copied from {@link BracedCondition}
 */
class OneOfExpressionFinder implements SectionFinder {

	public static SectionFinder createEmbracedExpressionFinder() {
		ConstraintSectionFinder sectionFinder = new ConstraintSectionFinder(
					new OneOfExpressionFinder());
		return sectionFinder;
	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
		String trimmed = text.trim();
		int leadingSpaces = text.indexOf(trimmed);
		int followingSpaces = text.length() - trimmed.length() - leadingSpaces;
		boolean startsWithOpen = trimmed.startsWith(Character.toString(OneOfBracedCondition.CURLY_BRACKET_OPEN));
		int closingBracket = SplitUtility.findIndexOfClosingBracket(trimmed, 0,
				OneOfBracedCondition.CURLY_BRACKET_OPEN, OneOfBracedCondition.CURLY_BRACKET_CLOSED);

		// if it doesnt start with an opening bracket
		if (!startsWithOpen) {
			// its not an embraced expression for sure => return null
			return null;
		}

		// throw error if no corresponding closing bracket can be found
		if (closingBracket == -1) {
			KDOMReportMessage.storeSingleError(father.getArticle(), father,
						this.getClass(), new SyntaxError("missing \""
								+ OneOfBracedCondition.CURLY_BRACKET_CLOSED + "\""));
			return null;
		}
		else {
			KDOMReportMessage.clearMessages(father.getArticle(), father,
						this.getClass());
		}

		// an embracedExpression needs to to start and end with '(' and ')'
		if (startsWithOpen
						&& trimmed.endsWith(Character.toString(OneOfBracedCondition.CURLY_BRACKET_CLOSED))) {
			// and the ending ')' needs to close the opening
			if (closingBracket == trimmed.length() - 1) {
				return SectionFinderResult.createSingleItemList(new SectionFinderResult(
								leadingSpaces, text.length() - followingSpaces));
			}

		}

		// OR an embracedExpression can be concluded with a lineEnd-comment
		int lastEndLineCommentSymbol = SplitUtility.lastIndexOfUnquoted(text, "//");
		// so has to start with '(' and have a lineend-comment-sign after
		// the closing bracket but nothing in between!
		if (trimmed.startsWith(Character.toString(OneOfBracedCondition.CURLY_BRACKET_OPEN))) {
			if (lastEndLineCommentSymbol > -1
						&& !CompositeCondition.hasLineBreakAfterComment(trimmed)) {
				// TODO fix: < 3 is inaccurate
				// better check that there is no other expression in between
				if (lastEndLineCommentSymbol - closingBracket < 3) {
					return SectionFinderResult.createSingleItemList(new SectionFinderResult(
									leadingSpaces, text.length()));
				}
			}

		}
		return null;
	}
}