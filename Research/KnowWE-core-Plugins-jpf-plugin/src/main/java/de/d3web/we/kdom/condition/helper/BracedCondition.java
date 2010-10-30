package de.d3web.we.kdom.condition.helper;

import java.util.List;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.NonTerminalCondition;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExclusiveType;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.util.SplitUtility;

/**
 * @author Jochen
 * 
 *         Any expression enclosed with brackets is a BracedCondition each has a
 *         child of type BracedConditionContent
 * 
 */
public class BracedCondition extends NonTerminalCondition {

	@Override
	protected void init() {
		this.sectionFinder = EmbracedExpressionFinder.createEmbracedExpressionFinder();
	}

}

/**
 * 
 * creates EmbracedExpressions if expression starts with a opening bracket and
 * concludes with a closing brackets AND these two correspond to each other
 * 
 * @author Jochen
 * 
 */
class EmbracedExpressionFinder implements ISectionFinder {

	public static ISectionFinder createEmbracedExpressionFinder() {
		ConstraintSectionFinder sectionFinder = new ConstraintSectionFinder(
					new EmbracedExpressionFinder());
		sectionFinder.addConstraint(ExclusiveType.getInstance());
		return sectionFinder;
	}

	@Override
	public List<SectionFinderResult> lookForSections(String text, Section father, KnowWEObjectType type) {
		String trimmed = text.trim();
		int leadingSpaces = text.indexOf(trimmed);
		int followingSpaces = text.length() - trimmed.length() - leadingSpaces;
		boolean startsWithOpen = trimmed.startsWith(Character.toString(CompositeCondition.BRACE_OPEN));
		int closingBracket = SplitUtility.findIndexOfClosingBracket(trimmed, 0,
						CompositeCondition.BRACE_OPEN, CompositeCondition.BRACE_CLOSED);

		// if it doesnt start with an opening bracket
		if (!startsWithOpen) {
			// its not an embraced expression for sure => return null
			return null;
		}

		// throw error if no corresponding closing bracket can be found
		if (closingBracket == -1) {
			KDOMReportMessage.storeSingleError(father.getArticle(), father,
						this.getClass(), new SyntaxError("missing \")\""));
			return null;
		}
		else {
			KDOMReportMessage.clearMessages(father.getArticle(), father,
						this.getClass());
		}

		// an embracedExpression needs to to start and end with '(' and ')'
		if (startsWithOpen
						&& trimmed.endsWith(Character.toString(CompositeCondition.BRACE_CLOSED))) {
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
		if (trimmed.startsWith(Character.toString(CompositeCondition.BRACE_OPEN))) {
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
