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
			StringBuilder content = new StringBuilder();
			KnowWEArticle compilingArticle = PackageRenderUtils.checkArticlesCompiling(decisionSec.getArticle(), decisionSec, content);
			KnowledgeBase kb = D3webUtils.getKnowledgeBase(decisionSec.getWeb(), compilingArticle.getTitle());

			// Collect all text values from first column
			LinkedList<String> firstColumnsQuestionStringList = this.getConditionStringsFirstColumn(decisionSec);
			LinkedList<String> firstColumnsActionStringList = this.getActionStringsFirstColumn(decisionSec);

			// Collect cells for columns
			// TODO Check if header misses 1st Tablecell
			int cellCount = TableUtils.getMaximumTableCellCount(innerTable);

			// Do for every column
			LinkedList<Section<TableCell>> column = null;
			for (int i = 1; i < cellCount; i++)
			{
				column = new LinkedList<Section<TableCell>>(
						TableUtils.getColumnCells(
								i, Sections.findChildOfType(decisionSec, InnerTable.class)));

				// Create all conditions from choices and questions
				List<ChoiceValue> conditionChoices = this.getRuleConditionChoices(column);
				List<Question> questionList = new LinkedList<Question>();
				for (int j = 0; j < firstColumnsQuestionStringList.size(); j++)
				{
					String questionText = firstColumnsQuestionStringList.get(j);
					ChoiceValue value = conditionChoices.get(j);
					questionList.add(this.createQuestion(kb, questionText, value));
				}

				List<Condition> conditions = this.getRuleConditions(kb, questionList, conditionChoices);

				// Get the Actions from the rest of TableCells
				// TODO Right ChoiceValue set?
				List<ChoiceValue> actionChoices = this.getRuleActionChoices(column);

				List<ActionSetValue> actions = new ArrayList<ActionSetValue>();
				ActionSetValue action = null;

				for (int k = 0; k < firstColumnsActionStringList.size(); k++)
				{
					String actionText = firstColumnsActionStringList.get(k);

					// action not to fire
					if (actionChoices.get(k) == null)
						continue;

					action = new ActionSetValue();
					action.setQuestion(this.createQuestion(kb, actionText, actionChoices.get(k)));
					action.setValue(actionChoices.get(k));
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


		/**
		 * 
		 * @created 12.01.2012
		 * @param column
		 * @return
		 */
		private List<ChoiceValue> getRuleActionChoices(LinkedList<Section<TableCell>> column)
		{
			Section<TableCell> cell = null;
			List<ChoiceValue> choices = new ArrayList<ChoiceValue>();

			while (!column.isEmpty())
			{
				cell = column.removeFirst();
				String cellText = cell.getText().trim();

				if (cellText.equals(""))
				{
					choices.add(null);
					continue;
				}

				ChoiceValue choice = this.createChoiceValue(cellText);
				choices.add(choice);
			}

			return choices;
		}


		/**
		 * 
		 * Creates all ChoiceValues for conditions in a column.
		 * Element is null if no ChoiceValue for this position
		 * is available.
		 * 
		 * @created 11.01.2012
		 * @param column
		 * @return
		 */
		private List<ChoiceValue> getRuleConditionChoices(LinkedList<Section<TableCell>> column)
		{
			Section<TableCell> cell = null;
			List<ChoiceValue> choices = new ArrayList<ChoiceValue>();

			while (!column.isEmpty())
			{
				cell = column.removeFirst();
				String cellText = cell.getText().trim();

				// Check if actions are reached
				if (cellText.equals(""))
				{
					Section<?> firstCellFather = cell.getFather().getChildren().get(0);
					if (firstCellFather.getText().trim().equals("")) break;
					continue;
				}

				// TODO what to do if it is a action: See createChoiceValue
				ChoiceValue choice = this.createChoiceValue(cellText);
				choices.add(choice);
			}

			return choices;
		}

		/**
		 * 
		 * TODO Condition can be any condition: dependent on choice-value
		 * 
		 * @created 11.01.2012
		 * @param kb
		 * @param questions
		 * @param choices
		 * @return
		 */
		private List<Condition> getRuleConditions(KnowledgeBase kb, List<Question> questions, List<ChoiceValue> choices)
		{
			List<Condition> conditions = new ArrayList<Condition>();
			for (int j = 0; j < questions.size(); j++)
			{
				if (questions.get(j) == null) continue;
				Question q = kb.getManager().searchQuestion(questions.get(j).getName());
				if ( (q == null) || (choices.get(j) == null) ) continue;

				CondEqual cond = new CondEqual(q, choices.get(j));
				conditions.add(cond);
			}

			return conditions;
		}


		/**
		 * Collects all actiontexts that the DecisionTable
		 * contains after the empty line.
		 * 
		 * @created 11.01.2012
		 * @param decisionSec
		 * @return
		 */
		private LinkedList<String> getActionStringsFirstColumn(Section<DecisionTable> decisionSec)
		{
			List<Section<TableCell>> firstColumn = TableUtils.getColumnCells(
					0, Sections.findChildOfType(decisionSec, InnerTable.class));

			LinkedList<String> questionList = new LinkedList<String>();
			boolean add = false;
			for (Section<TableCell> cell : firstColumn)
			{
				String questionText = cell.getText().trim();
				if (questionText.equals("Actions"))
				{
					add = true;
					continue;
				}
				if (add) questionList.add(questionText);
			}
			return questionList;
		}


		/**
		 * Collects all cell text until it reaches the empty line
		 * of the DecisionTable.
		 * 
		 * @created 11.01.2012
		 * @param decisionSec
		 * @return
		 */
		private LinkedList<String> getConditionStringsFirstColumn(Section<DecisionTable> decisionSec)
		{
			List<Section<TableCell>> firstColumn = TableUtils.getColumnCells(
					0, Sections.findChildOfType(decisionSec, InnerTable.class));

			LinkedList<String> questionList = new LinkedList<String>();
			for (Section<TableCell> cell : firstColumn)
			{
				String questionText = cell.getText().trim();
				if (questionText.equals("Actions")) break;
				questionList.add(questionText);
			}
			return questionList;
		}


		/**
		 * 
		 * @created 23.12.2011
		 * @param cellText
		 */
		private ChoiceValue createChoiceValue(String cellText)
		{
			ChoiceValue choice = null;
			if (cellText.equals("Yes"))
				choice = new ChoiceValue("YES");
			else if (cellText.equals("No"))
				choice = new ChoiceValue("NO");
			else if (cellText.equals("x"))
				choice = new ChoiceValue("X");
			return choice;
		}


		/**
		 * 
		 * Creates right QuestionType according to ChoiceValueType
		 * 
		 * @created 23.12.2011
		 * @param kb
		 * @param questionText
		 * @param choiceValue
		 * @return
		 */
		private Question createQuestion(KnowledgeBase kb, String questionText, ChoiceValue choiceValue)
		{
			if (choiceValue == null) return null;

			Question q = kb.getManager().searchQuestion(questionText);
			if (q == null)
				q = new QuestionYN(kb, questionText);
			return q;
		}

	}

}
