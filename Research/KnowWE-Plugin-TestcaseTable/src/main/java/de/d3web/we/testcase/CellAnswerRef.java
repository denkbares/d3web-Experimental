package de.d3web.we.testcase;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;

/**
 * 
 * @author Reinhard Hatko
 * @created 18.01.2011
 */
final class CellAnswerRef extends AnswerReference {

	@Override
	public Section<QuestionReference> getQuestionSection(Section<? extends AnswerReference> s) {

		Section<? extends KnowWEObjectType> headerCell = TestcaseTable.findHeaderCell(s);

		Section<QuestionReference> questionRef = headerCell.findSuccessor(QuestionReference.class);

		return questionRef;
	}

}