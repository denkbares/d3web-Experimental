/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.wimvent.ruleTable;

import java.util.regex.Pattern;

import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.we.kdom.ExclusiveType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableUtils;
import de.d3web.we.object.QuestionReference;

public abstract class TableCondNum extends D3webCondition<TableCondNum> implements ExclusiveType {

	public TableCondNum(String op) {
		this.setSectionFinder(new RegexSectionFinder(op + "(.+)",
				Pattern.CASE_INSENSITIVE,
				1));
		de.d3web.we.kdom.basic.Number t = new de.d3web.we.kdom.basic.Number(
				new AllTextFinderTrimmed());
		t.setCustomRenderer(WimVentTable.NUMBER_RENDERER);
		this.addChildType(t);

	}

	@Override
	protected Condition createCondition(KnowWEArticle article, Section<TableCondNum> section) {
		Section<de.d3web.we.kdom.basic.Number> numberSec = Sections.findChildOfType(
				section, de.d3web.we.kdom.basic.Number.class);
		if (numberSec != null) {
			Double number = de.d3web.we.kdom.basic.Number.getNumber(numberSec);
			if (number != null) {
				Section<TableCellContent> cell = Sections.findAncestorOfType(section,
							TableCellContent.class);
				int column = TableUtils.getColumn(cell);
				Section<QuestionReference> qRecInColumn = WimVentTable.findQRecInColumn(
						section,
							column);
				if (qRecInColumn != null) {
					Question q = qRecInColumn.get().getTermObject(article, qRecInColumn);
					if (q instanceof QuestionNum) {
						return getCond((QuestionNum) q, number);
					}
				}
			}
		}

		return null;
	}

	protected abstract Condition getCond(QuestionNum q, Double number);

}

class EqTableCondNum extends TableCondNum {
	public EqTableCondNum() {
		super("=");
	}

	@Override
	protected Condition getCond(QuestionNum q, Double number) {
		return new CondNumEqual(q, number);
	}

}

class GrEqTableCondNum extends TableCondNum {
	public GrEqTableCondNum() {
		super(">=");
	}

	@Override
	protected Condition getCond(QuestionNum q, Double number) {
		return new CondNumGreaterEqual(q, number);
	}
}

class LeEqTableCondNum extends TableCondNum {
	public LeEqTableCondNum() {
		super("<=");
	}

	@Override
	protected Condition getCond(QuestionNum q, Double number) {
		return new CondNumLessEqual(q, number);
	}
}

class LeTableCondNum extends TableCondNum {
	public LeTableCondNum() {
		super("<");
	}

	@Override
	protected Condition getCond(QuestionNum q, Double number) {
		return new CondNumLess(q, number);
	}
}

class GrTableCondNum extends TableCondNum {
	public GrTableCondNum() {
		super(">");
	}

	@Override
	protected Condition getCond(QuestionNum q, Double number) {
		return new CondNumGreater(q, number);
	}
}

// class IntervallTableCondNum extends TableCondNum {
// public IntervallTableCondNum() {
// this.setSectionFinder(new RegexSectionFinder("\\[(.+)\\]",
// Pattern.CASE_INSENSITIVE, 1));
// }
// }
