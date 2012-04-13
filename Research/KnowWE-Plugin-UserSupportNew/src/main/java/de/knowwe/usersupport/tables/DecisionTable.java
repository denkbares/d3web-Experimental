/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.usersupport.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.kdom.rules.action.SetQuestionValue;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class DecisionTable extends ITable
{

	public DecisionTable()
	{
		this.sectionFinder = new AllTextSectionFinder();
		this.addSubtreeHandler(Priority.PRECOMPILE_LOW, new DecisionTableSubtreeHandler());

		this.addChildType(new TableDescriptionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());
	}

	public class DecisionTableSubtreeHandler extends GeneralSubtreeHandler<DecisionTable>
	{

		@Override
		public Collection<Message> create(Article article, Section<DecisionTable> decisionSec)
		{
			Section<InnerTable> innerTable =
					Sections.findChildOfType(decisionSec, InnerTable.class);
			if (Sections.findSuccessorsOfType(innerTable, TableCell.class).isEmpty()) return null;

			Article compilingArticle = KnowWEUtils.getCompilingArticles(decisionSec).iterator().next();

			// Get all conditions and SetQuestionValues from firstColumn
			List<Section<TableCell>> firstColumn = new LinkedList<Section<TableCell>>(
					TableUtils.getColumnCells(
							0, Sections.findChildOfType(decisionSec, InnerTable.class)));

			LinkedList<Condition> conditionList = new LinkedList<Condition>();
			LinkedList<Section<SetQuestionValue>> qValList = new LinkedList<Section<SetQuestionValue>>();
			Section<TableCell> cell = null;

			int i = 0;
			for (; i < firstColumn.size(); i++)
			{
				cell = firstColumn.get(i);
				if (cell.getText().equals("Actions"))
				{
					i++;
					break;
				}

				Section<CompositeCondition> cond = Sections.findChildOfType(cell,
						CompositeCondition.class);
				compilingArticle = KnowWEUtils.getCompilingArticles(cell).iterator().next();
				Condition d3Cond = KDOMConditionFactory.createCondition(compilingArticle, cond);
				conditionList.add(d3Cond);
			}

			for (; i < firstColumn.size(); i++)
			{
				cell = firstColumn.get(i);
				Section<SetQuestionValue> qVal = Sections.findChildOfType(cell,
						SetQuestionValue.class);
				qValList.add(qVal);
			}

			// Do for every column
			int cellCount = TableUtils.getMaximumTableCellCount(innerTable);
			LinkedList<Section<TableCell>> column = null;
			for (int j = 1; j < cellCount; j++)
			{
				List<Section<TableCell>> columnCells = TableUtils.getColumnCells(
						j, Sections.findChildOfType(decisionSec, InnerTable.class));
				if (columnCells == null) break;

				column = new LinkedList<Section<TableCell>>(columnCells);

				// Collect all conditions and create final ConditionAnd
				List<Condition> conditions = new ArrayList<Condition>();
				Condition c = null;
				int k = 0;
				for (; k < conditionList.size(); k++)
				{
					c = conditionList.get(k);
					if (c == null) break; // Transition: Actions follow

					if (column.get(k).getText().equals("+"))
					{
						conditions.add(c);
					}
					else if (column.get(k).getText().equals("-"))
					{
						conditions.add(new CondNot(c));
					}

				}
				CondAnd conditionAnd = new CondAnd(conditions);

				// Create actions
				// Create Rule for every action
				RuleSet ruleSet = new RuleSet();
				Section<SetQuestionValue> qVal = null;
				for (int h = 0; h < qValList.size(); h++, k++)
				{
					qVal = qValList.get(h);
					if (qVal == null) continue;
					// Create no action
					if (column.get(k).getText().equals("")) continue;

					// TODO what todo if negated?
					// else if (column.get(i).getText().equals("-"))
					// {
					// conditions.add(new CondNot(c));
					// }

					PSAction action = qVal.get().getAction(compilingArticle, qVal);

					Rule rule = new Rule(PSMethodAbstraction.class);
					rule.setAction(action);

					// Create the final Rule
					rule.setCondition(conditionAnd);
					rule.setException(null);
					ruleSet.addRule(rule);

				}
			}

			return null;
		}

	}

}
