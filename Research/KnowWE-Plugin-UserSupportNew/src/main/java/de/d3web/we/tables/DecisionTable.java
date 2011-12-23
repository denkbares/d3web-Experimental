/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.we.kdom.xcl.list.ListSolutionType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.packaging.PackageRenderUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;


/**
 * 
 * TODO Add ReportMessages
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class DecisionTable extends ITable {

	public DecisionTable() {
		this.sectionFinder = new AllTextSectionFinder();
		this.addSubtreeHandler(Priority.LOW, new DecisionTableSubtreehandler());
		this.addChildType(new ListSolutionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());
	}

	/**
	 * Handles the creation of Rules from DecisionTableMarkup
	 * 
	 * @author Johannes Dienst
	 * @created 10.11.2011
	 */
	public class DecisionTableSubtreehandler extends GeneralSubtreeHandler<DecisionTable> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<DecisionTable> decisionSec)
		{
			Section<InnerTable> innerTable =
					Sections.findChildOfType(decisionSec, InnerTable.class);
			if (Sections.findSuccessorsOfType(innerTable, TableCell.class).isEmpty())
				return null;

			// TODO Right KnowledgeBase?
			//			Section<DecisionTableMarkup> mark = Sections.findAncestorOfExactType(
			//					decisionSec, DecisionTableMarkup.class);
			//			String packageName = DefaultMarkupType.getAnnotation(mark, "package");
			//			KnowledgeBase kb = D3webUtils.getKB(decisionSec.getWeb(), packageName + " - master");
			// get the article compiling this cell
			StringBuilder content = new StringBuilder();
			KnowWEArticle compilingArticle = PackageRenderUtils.checkArticlesCompiling(decisionSec.getArticle(), decisionSec, content);
			KnowledgeBase kb = D3webUtils.getKB(decisionSec.getWeb(), compilingArticle.getTitle());

			// Create all Questions
			// TODO First Cell is no Question: Removed it! But what if empty?
			List<Section<TableCell>> firstColumn = TableUtils.getColumnCells(
					0, Sections.findChildOfType(decisionSec, InnerTable.class));

			//			LinkedList<Question> questionList = new LinkedList<Question>();
			//			for (Section<TableCell> cell : firstColumn)
			//			{
			//				String questionText = cell.getText().trim();
			//				// Markup now has a empty line to seperate the Conditions from the diagnoses
			//				if (questionText.equals("")) continue;
			//				Question q = kb.getManager().searchQuestion(questionText);
			//				if (q == null)
			//				{
			//					QuestionYN question = new QuestionYN(kb, questionText);
			//					questionList.add(question);
			//				}
			//				else
			//				{
			//					questionList.add(q);
			//				}
			//			}
			LinkedList<String> questionList = new LinkedList<String>();
			for (Section<TableCell> cell : firstColumn)
			{
				String questionText = cell.getText().trim();
				// Markup now has a empty line to seperate the Conditions from the diagnoses
				if (questionText.equals("")) continue;
				questionList.add(questionText);
			}

			// Collect cells for columns
			// TODO Check if header misses 1st Tablecell
			int cellCount = TableUtils.getMaximumTableCellCount(innerTable);

			// Do for every column
			LinkedList<Section<TableCell>> column = null;
			for (int i = 1; i < cellCount; i++) {
				column = new LinkedList<Section<TableCell>>(
						TableUtils.getColumnCells(
								i, Sections.findChildOfType(decisionSec, InnerTable.class)));
				// Remove RuleName
				//				column.removeFirst();

				// create rule choices
				Section<TableCell> cell = null;
				List<ChoiceValue> choices = new ArrayList<ChoiceValue>();
				while (true)
				{
					cell = column.get(0);
					String cellText = cell.getText().trim();

					// only until we get to the actions
					if (cellText.equals("x") || cellText.equals("")) break;

					column.removeFirst();

					ChoiceValue choice = null;
					if (cellText.equals("Yes"))
						choice = new ChoiceValue("YES");
					else if (cellText.equals("No"))
						choice = new ChoiceValue("NO");
					choices.add(choice);
				}

				// create condition from choices
				List<Condition> conditions = new ArrayList<Condition>();
				for (int j = 0; j < choices.size(); j++)
				{
					// TODO Condition can be any condition: dependent on choice-value
					Question q = kb.getManager().searchQuestion(questionList.get(j));
					if (q == null)
					{
						q = new QuestionYN(kb, questionList.get(j));
					}

					CondEqual cond = new CondEqual(q, choices.get(j));
					conditions.add(cond);
				}

				// Get the Actions from the rest of TableCells
				// TODO Right ChoiceValue set?
				List<ActionSetValue> actions = new ArrayList<ActionSetValue>();
				ActionSetValue action = null;
				// int to get the right questions for actions
				int b = choices.size();
				//				if ( cell != null )
				//				{
				//					if ( cell.getText().trim().equals("x") )
				//					{
				//						action = new ActionSetValue();
				//						action.setQuestion(questionList.get(b));
				//						action.setValue(new ChoiceValue(cell.getText().trim()));
				//						actions.add(action);
				//					}
				//					b++;
				//				}

				while (!column.isEmpty())
				{
					cell = column.removeFirst();
					String cellText = cell.getText().trim();

					// action not to fire
					if (cellText.equals(""))
					{
						b++;
						continue;
					}
					action = new ActionSetValue();

					// TODO Condition can be any condition: dependent on choice-value
					Question q = kb.getManager().searchQuestion(questionList.get(b));
					if (q == null)
					{
						q = new QuestionYN(kb, questionList.get(b));
					}
					b++;
					action.setQuestion(q);
					action.setValue(new ChoiceValue(cell.getText().trim()));
					actions.add(action);
				}

				// Create final CondAnd
				CondAnd conditionAnd = new CondAnd(conditions);

				// Create Rule for every action
				RuleSet ruleSet = new RuleSet();
				for (ActionSetValue actionValue : actions)
				{
					Rule rule = new Rule(PSMethodAbstraction.class);
					rule.setAction(actionValue);
					rule.setCondition(conditionAnd);
					rule.setException(null);
					ruleSet.addRule(rule);
				}

			}
			return null;
		}

	}

}
