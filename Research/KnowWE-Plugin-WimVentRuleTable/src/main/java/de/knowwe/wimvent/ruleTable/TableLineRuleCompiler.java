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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.RuleFactory;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.Number;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.CreateRelationFailed;
import de.d3web.we.kdom.report.message.ObjectCreatedMessage;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.wimvent.ruleTable.WimVentTable.WimVentRuleTableRuleLine;

public class TableLineRuleCompiler extends D3webSubtreeHandler<WimVentRuleTableRuleLine> {

	public static final String ruleStoreKey = "RULE_STORE_KEY";

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<WimVentRuleTableRuleLine> s) {

		if (s.hasErrorInSubtree(article)) {
			return Arrays.asList((KDOMReportMessage) new CreateRelationFailed("Rule"));
		}

		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();

		Section<DefaultMarkupType> defaultMarkup = Sections.findAncestorOfType(s,
				DefaultMarkupType.class);
		String numString = defaultMarkup.get().getAnnotation(defaultMarkup,
				WimVentTableMarkup.COLUMN_NUMBER_KEY);
		int numberOfActionColumns = 1;
		try {
			numberOfActionColumns = Integer.parseInt(numString);
		}
		catch (Exception e) {

		}

		List<Section<TableCellContent>> cells = new ArrayList<Section<TableCellContent>>();
		Sections.findSuccessorsOfType(s, TableCellContent.class, cells);
		List<Condition> conditions = new ArrayList<Condition>();
		for (int i = 0; i < cells.size() - numberOfActionColumns; i++) {
			Section<TableCellContent> cell = cells.get(i);
			Section<D3webCondition> condSec = Sections.findSuccessor(cell,
					D3webCondition.class);
			if (condSec != null) {
				Condition cond = condSec.get().getCondition(article, condSec);
				if (cond != null) {
					conditions.add(cond);
				}
				else {
					return Arrays.asList((KDOMReportMessage) new CreateRelationFailed(
							D3webModule.getKwikiBundle_d3web().
									getString("KnowWE.rulesNew.notcreated")
							));
				}
			}
			else {
				return Arrays.asList((KDOMReportMessage) new CreateRelationFailed(
						D3webModule.getKwikiBundle_d3web().
								getString("KnowWE.rulesNew.notcreated")
						));
			}
		}
		CondAnd and = new CondAnd(conditions);

		for (int i = cells.size() - numberOfActionColumns; i < cells.size(); i++) {
			PSAction a = null;
			Section<TableCellContent> cell = cells.get(i);
			Section<QuestionReference> qRefInColumn = WimVentTable.findQRecInColumn(cell,
					i);
			Question q = qRefInColumn.get().getTermObject(article, qRefInColumn);

			// check for number
			Section<Number> numberSec = Sections.findSuccessor(cell,
					de.d3web.we.kdom.basic.Number.class);
			if (q != null && numberSec != null) {
				if (q instanceof QuestionNum) {
					QuestionNum qNum = (QuestionNum) q;
					ActionSetValue asv = new ActionSetValue();
					asv.setQuestion(qNum);
					asv.setValue(Number.getNumber(numberSec));
					a = asv;
				}else {
					messages.add(new CreateRelationFailed(
							"wrong question-answer combination")
							);
				}
			}

			// check for answerRef
			Section<AnswerReference> answerSec = Sections.findSuccessor(cell,
					AnswerReference.class);
			if (q != null && answerSec != null) {
				if (q instanceof QuestionChoice) {
					Choice choice = answerSec.get().getTermObject(article, answerSec);
					ActionSetValue asv = new ActionSetValue();
					asv.setQuestion(q);
					asv.setValue(choice);
					a = asv;
				}
				else {
					return Arrays.asList((KDOMReportMessage) new CreateRelationFailed(
							"wrong question-answer combination")
							);
				}
			}
			// create actual rule
			if (and != null && a != null) {
				Rule r = RuleFactory.createRule(a, and,
						null, null, PSMethodAbstraction.class);
				if (r != null) {
					KnowWEUtils.storeObject(article, s, ruleStoreKey, r);
					messages.add(new ObjectCreatedMessage(
							"Rule"));
				}

			}
			else {
				messages.add(new CreateRelationFailed("Rule"));
			}

		}

		return messages;

	}

}
