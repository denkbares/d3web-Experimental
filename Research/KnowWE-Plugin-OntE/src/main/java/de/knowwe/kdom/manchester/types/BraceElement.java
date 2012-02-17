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

import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

/**
 *
 * Copied from {@link BracedCondition}
 *
 * @author Stefan Mark
 * @created 21.09.2011
 */
public class BraceElement extends NonTerminalCondition {

	public static char OPEN = Character.MAX_VALUE;
	public static char CLOSED = Character.MAX_VALUE;

	public BraceElement(char open, char closed) {
		OPEN = open;
		CLOSED = closed;

		this.setSectionFinder(BracedExpressionFinder.createEmbracedExpressionFinder());
		this.setRenderer(new KnowWERenderer<BraceElement>() {

			@Override
			public void render(Section<BraceElement> section, UserContext user, StringBuilder string) {

				StringBuilder masked = new StringBuilder();
				DelegateRenderer.getInstance().render(section, user, masked);
				string.append(KnowWEUtils.maskJSPWikiMarkup(masked.toString()));
			}
		});
	}
}

/**
 * @see Copied from {@link BracedCondition}
 */
class BracedExpressionFinder implements SectionFinder {

	public static SectionFinder createEmbracedExpressionFinder() {
		ConstraintSectionFinder sectionFinder = new ConstraintSectionFinder(
					new BracedExpressionFinder());
		return sectionFinder;
	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

		String trimmed = text.trim();
		boolean startsWithOpen = trimmed.startsWith(Character.toString(BraceElement.OPEN));

		int closingBracket = SplitUtility.findIndexOfClosingBracket(trimmed, 0,
				BraceElement.OPEN, BraceElement.CLOSED);

		// text does not start with open brace, return null
		if (!startsWithOpen) {
			return null;
		}

		// closing brace could not be found -> throw error message
		if (closingBracket == -1) {
			Messages.storeMessage(father.getArticle(), father,
						this.getClass(), Messages.syntaxError("missing \""
								+ BraceElement.CLOSED + "\""));
			return null;
		}
		else {
			// clear messages and look for matching braces
			Messages.clearMessages(father.getArticle(), father, this.getClass());
		}

		int leadingSpaces = text.indexOf(trimmed);
		int followingSpaces = text.length() - trimmed.length() - leadingSpaces;

		// an bracedExpression needs to start and end with OPEN and CLOSED
		if (startsWithOpen && trimmed.endsWith(Character.toString(BraceElement.CLOSED))) {
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
		if (trimmed.startsWith(Character.toString(BraceElement.OPEN))) {
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
