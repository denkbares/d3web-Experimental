package de.d3web.we.testcase;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
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

		Section<? extends Type> headerCell = TestcaseTable.findHeaderCell(s);

		Section<QuestionReference> questionRef = Sections.findSuccessor(headerCell,
				QuestionReference.class);

		return questionRef;
	}

}