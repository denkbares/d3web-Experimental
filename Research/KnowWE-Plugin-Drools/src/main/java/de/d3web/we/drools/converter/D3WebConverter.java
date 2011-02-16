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
package de.d3web.we.drools.converter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.inference.PSMethodHeuristic;

public class D3WebConverter {

	/**
	 * Specifies the standard indent
	 */
	private final String INDENT = "  ";

	/**
	 * A prefix for an input e.g. "not", "or" etc. Don't change it here, it is
	 * changed on the fly while running this converter!
	 */
	private String prefix = "";

	/**
	 * Counts the amount of rules in NonTerminalConditions
	 */
	private int ruleCounter = 0;

	/**
	 * The d3web-KnowledgeBase.
	 */
	private final KnowledgeBase d3Kb;

	/**
	 * The converted Drools Rules
	 */
	private final List<DroolsRule> droolsRules = new LinkedList<DroolsRule>();

	/**
	 * If true, the rule won't be added to droolsRules
	 */
	private boolean dontAdd;

	/**
	 * If true, we are processing a not condition!
	 */
	private boolean notCondition;

	/**
	 * The current PSAction, necessary for CondOrs
	 */
	private PSAction action;

	/**
	 * Default Constructor which requires just a d3-web KnowledgeBase.
	 * 
	 * @param d3Kb the d3-web KnowledgeBase
	 * @throws IOException
	 */
	public D3WebConverter(KnowledgeBase d3Kb) throws IOException {
		this.d3Kb = d3Kb;
	}

	public void convert(String filename) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources/misc/"
						+ filename), "UTF-8"));
		out.write("[{KnowWEPlugin drools}]\n\n");
		out.write("%%DroolsFacts\n");
		out.write(getDroolsFacts());
		out.write("\n%\n\n");
		convertRules();
		out.write("%%DroolsRules\n");
		for (DroolsRule rule : droolsRules) {
			if (rule.getConditions().size() > 0) out.write(rule.toString());
		}
		out.write("%\n");
		out.close();
		System.out.println("Wrote file to: " + System.getProperty("user.dir")
				+ "/src/main/resources/misc/" + filename);

	}

	/**
	 * Returns a String representing the Drools Facts.
	 * 
	 * @return drools facts
	 * @throws InvalidClassException
	 */
	private String getDroolsFacts() throws InvalidClassException {

		int factsCounter = 0;

		// StringBuilder for Drools Output
		StringBuilder droolsFacts = new StringBuilder();

		// Convert each d3web Question in a drools Fact
		for (Question question : d3Kb.getManager().getQuestions()) {
			convertD3Question(droolsFacts, question);
			factsCounter++;
		}

		// Convert each d3web Solution in a drools Fact
		for (Solution diagnosis : d3Kb.getManager().getSolutions()) {
			convertDiagnosis(droolsFacts, diagnosis);
			factsCounter++;
		}

		System.out.println("Converted " + factsCounter + " facts.\n");
		return droolsFacts.toString();
	}

	/**
	 * Returns a String representing the Drools Rules.
	 * 
	 * @return drools rules.
	 * @throws InvalidClassException
	 */
	public void convertRules() throws InvalidClassException {

		// Stores the ids of the already processed rules
		List<Rule> processedRules = new LinkedList<Rule>();

		// Convert each d3web-Rule in a drools Rule
		for (KnowledgeSlice slice : d3Kb.getAllKnowledgeSlicesFor(PSMethodHeuristic.FORWARD)) {
			if (slice instanceof RuleSet) {
				for (Rule rule : ((RuleSet) slice).getRules()) {
					if (rule != null && !processedRules.contains(rule)) {
						convertD3Rule(rule);
						processedRules.add(rule);
					}
				}
			}
		}

		// Convert each d3web-AbstractionRule in a droolsRule
		for (KnowledgeSlice slice : d3Kb.getAllKnowledgeSlicesFor(PSMethodAbstraction.FORWARD)) {
			if (slice instanceof RuleSet) {
				for (Rule rule : ((RuleSet) slice).getRules()) {
					if (rule != null && !processedRules.contains(rule)) {
						convertD3Rule(rule);
						processedRules.add(rule);
					}
				}
			}
		}

		System.out.println("Converted " + processedRules.size() + " rules.\n");

	}

	/* ************************************************************************************************************
	 * 
	 * Helper Methods for getDroolsFacts
	 * 
	 * **************************************************************************
	 * *********************************
	 */

	/**
	 * Converts a d3-web question to a textual representation of a drools fact.
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param question the d3-web question
	 * @throws InvalidClassException
	 */
	private void convertD3Question(StringBuilder droolsFacts, Question question) throws InvalidClassException {
		if (question instanceof QuestionNum) convertQuestionNum(droolsFacts, (QuestionNum) question);
		else if (question instanceof QuestionOC) convertQuestionOC(droolsFacts,
				(QuestionOC) question);
		else if (question instanceof QuestionMC) convertQuestionMC(droolsFacts,
				(QuestionMC) question);
		else throw new InvalidClassException("This type of Question is not supported: "
				+ question.getClass());

	}

	/**
	 * Converts a d3-web QuestionNum to a textual representation of a drools
	 * fact.
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param question the d3-web QuestionNum
	 */
	private void convertQuestionNum(StringBuilder droolsFacts, QuestionNum question) {
		droolsFacts.append("Input<Num>(\"");
		droolsFacts.append(question.getName());
		droolsFacts.append("\");\n");
	}

	/**
	 * Converts a d3-web QuestionOC to a textual representation of a drools
	 * fact.
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param question the d3-web QuestionOC
	 */
	private void convertQuestionOC(StringBuilder droolsFacts, QuestionOC question) {
		droolsFacts.append("Input<OC>(\"");
		droolsFacts.append(question.getName());
		droolsFacts.append("\", {");
		convertChoiceAlternatives(droolsFacts, question.getAllAlternatives());
		droolsFacts.append("});\n");
	}

	/**
	 * Converts a d3-web QuestionMC to a textual representation of a drools
	 * fact.
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param question the d3-web QuestionMC
	 */
	private void convertQuestionMC(StringBuilder droolsFacts, QuestionMC question) {
		droolsFacts.append("Input<MC>(\"");
		droolsFacts.append(question.getName());
		droolsFacts.append("\", {");
		convertChoiceAlternatives(droolsFacts, question.getAllAlternatives());
		droolsFacts.append("});\n");
	}

	/**
	 * Appends all AnswerAlternatives to the current fact
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param aternatives all answer alternatives of the d3-web question
	 */
	private void convertChoiceAlternatives(StringBuilder droolsFacts,
			List<Choice> aternatives) {
		for (Choice c : aternatives) {
			droolsFacts.append("\"");
			droolsFacts.append(c.getName());
			droolsFacts.append("\", ");
		}
		droolsFacts.delete(droolsFacts.length() - 2, droolsFacts.length());
	}

	/**
	 * Converts a d3-web diagnosis to a textual representation of a drools fact.
	 * 
	 * @param droolsFacts the StringBuilder representing the drools facts
	 * @param diagnosis the d3-web diagnosis
	 */
	private void convertDiagnosis(StringBuilder droolsFacts, Solution diagnosis) {
		if (!diagnosis.getName().equals("P000")) {
			droolsFacts.append("Input<Solution>(\"");
			droolsFacts.append(diagnosis.getName());
			droolsFacts.append("\");\n");
		}
	}

	/* ************************************************************************************************************
	 * 
	 * Helper Methods for getDroolsRules
	 * 
	 * **************************************************************************
	 * *********************************
	 */

	/**
	 * Converts a single d3-web Rule in a Drools Rule.
	 * 
	 * @param droolsRulesBuilde StringBuilder representing the drools rules
	 * @param d3Rule the d3-web rule
	 * @throws InvalidClassException
	 */
	private void convertD3Rule(Rule d3Rule) throws InvalidClassException {
		dontAdd = false;
		notCondition = false;
		action = d3Rule.getAction();
		DroolsRule droolsRule = new DroolsRule(d3Rule.toString());
		convertCondition(d3Rule.getCondition(), droolsRule);
		convertAction(d3Rule.getAction(), droolsRule);
		if (!dontAdd) droolsRules.add(droolsRule);
	}

	/**
	 * Appends the conditions to the current Drools Rule.
	 * 
	 * @param droolsRule
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param d3Rule the current d3-web rule
	 * @throws InvalidClassException
	 */
	private void convertCondition(Condition condition, DroolsRule droolsRule) throws InvalidClassException {
		if (condition instanceof CondEqual) covertCondEqual(droolsRule, (CondEqual) condition);
		else if (condition instanceof CondKnown) convertCondKnown(droolsRule, (CondKnown) condition);
		else if (condition instanceof CondNumEqual) convertCondNum(droolsRule, (CondNum) condition,
				"==");
		else if (condition instanceof CondNumGreater) convertCondNum(droolsRule,
				(CondNum) condition, ">");
		else if (condition instanceof CondNumGreaterEqual) convertCondNum(droolsRule,
				(CondNum) condition, ">=");
		else if (condition instanceof CondNumIn) convertCondNumIn(droolsRule, (CondNumIn) condition);
		else if (condition instanceof CondNumLess) convertCondNum(droolsRule, (CondNum) condition,
				"<");
		else if (condition instanceof CondNumLessEqual) convertCondNum(droolsRule,
				(CondNum) condition, "<=");
		else if (condition instanceof CondAnd) convertConditionAnd(droolsRule, (CondAnd) condition);
		else if (condition instanceof CondNot) convertConditionNot(droolsRule, (CondNot) condition);
		else if (condition instanceof CondOr) convertConditionOr(droolsRule, (CondOr) condition);
		else throw new InvalidClassException("Type of Condition is not yet supported: "
				+ condition.getClass());
	}

	/**
	 * Helper method which converts CondAnds of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed d3-web rule
	 * @throws InvalidClassException
	 */
	private void convertConditionAnd(DroolsRule droolsRule, CondAnd condition) throws InvalidClassException {
		for (Condition term : condition.getTerms()) {
			ruleCounter++;
			convertCondition(term, droolsRule);
		}
		ruleCounter = 0;
	}

	/**
	 * Helper method which converts CondNots of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed d3-web rule
	 * @throws InvalidClassException
	 */
	private void convertConditionNot(DroolsRule droolsRule, CondNot condition) throws InvalidClassException {
		notCondition = true;
		prefix = "not ";
		for (Condition term : condition.getTerms()) {
			convertCondition(term, droolsRule);
			prefix = "";
		}
	}

	/**
	 * Helper method which converts CondOrs of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed d3-web rule
	 * @throws InvalidClassException
	 */
	private void convertConditionOr(DroolsRule droolsRule, CondOr condition) throws InvalidClassException {
		for (Condition term : condition.getTerms()) {
			ruleCounter++;
			if (!notCondition) {
				String name = droolsRule.getName() + "_" + ruleCounter + "_"
						+ condition.getTerms().size();
				DroolsRule newRule = new DroolsRule(name);
				convertCondition(term, newRule);
				convertAction(action, newRule);
				droolsRules.add(newRule);
				dontAdd = true;
			}
			else {
				convertCondition(term, droolsRule);
			}
		}
		ruleCounter = 0;
	}

	/**
	 * Helper method which converts CondEquals of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed condition
	 */
	private void covertCondEqual(DroolsRule droolsRule,
			CondEqual condition) {
		StringBuilder conditionBuilder = new StringBuilder();
		convertAnswer(conditionBuilder, condition.getValue());
		convertCondEqualCondition(conditionBuilder, condition);
		droolsRule.getConditions().add(conditionBuilder.toString());
	}

	/**
	 * Helper method which converts CondKnowns of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed condition
	 */
	private void convertCondKnown(DroolsRule droolsRule, CondKnown condition) {
		StringBuilder conditionBuilder = new StringBuilder();
		convertQuestion(conditionBuilder, condition.getQuestion());
		conditionBuilder.append(" && numValue != 0");
		conditionBuilder.append(")\n");
		droolsRule.getConditions().add(conditionBuilder.toString());
	}

	/**
	 * Helper method which converts CondNum of d3-web rules
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed condition
	 */
	private void convertCondNum(DroolsRule droolsRule,
			CondNum condition, String comparator) {
		StringBuilder conditionBuilder = new StringBuilder();
		convertQuestion(conditionBuilder, condition.getQuestion());
		conditionBuilder.append(" && numValue ");
		conditionBuilder.append(comparator);
		conditionBuilder.append(" ");
		conditionBuilder.append(condition.getConditionValue());
		conditionBuilder.append(")\n");
		droolsRule.getConditions().add(conditionBuilder.toString());
	}

	/**
	 * Helper method which converts ConNumIN of d3-web rules
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed condition
	 */
	private void convertCondNumIn(DroolsRule droolsRule, CondNumIn condition) {
		StringBuilder conditionBuilder = new StringBuilder();
		convertQuestion(conditionBuilder, condition.getQuestion());
		conditionBuilder.append(" && numValue >=");
		conditionBuilder.append(" ");
		conditionBuilder.append(condition.getMinValue());
		conditionBuilder.append(" && numValue <=");
		conditionBuilder.append(" ");
		conditionBuilder.append(condition.getMaxValue());
		conditionBuilder.append(")\n");
		droolsRule.getConditions().add(conditionBuilder.toString());

	}

	/**
	 * Helper method which converts values (answers) of d3-web conditions
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param value the value (answer) of the currently processed condition
	 * @param counter an int which counts the number of converted values
	 */
	private void convertAnswer(StringBuilder conditionBuilder, Value value) {
		conditionBuilder.append(INDENT);
		conditionBuilder.append(INDENT);
		;
		conditionBuilder.append("$value");
		conditionBuilder.append(ruleCounter == 0 ? "" : ruleCounter);
		conditionBuilder.append(" : Value(value == \"");
		conditionBuilder.append(value.getValue());
		conditionBuilder.append("\")");
		conditionBuilder.append("\n");
	}

	/**
	 * Helper method which converts questions of d3-web conditions.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param condition the currently processed d3-web condition
	 */
	private void convertCondEqualCondition(StringBuilder conditionBuilder, CondEqual condition) {
		convertQuestion(conditionBuilder, condition.getQuestion());
		conditionBuilder.append(" && values.size > 0 && values " + prefix + "contains $value");
		conditionBuilder.append(ruleCounter == 0 ? "" : ruleCounter);
		conditionBuilder.append(")\n");
	}

	/**
	 * Helper method which converts a d3-web question in an input declaration.
	 * <b>ATTENTION:</b> You have to close the delaration with a brace ')'
	 * manually.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param question the question of the d3-web condition
	 */
	private void convertQuestion(StringBuilder conditionBuilder, Question question) {
		conditionBuilder.append(INDENT);
		conditionBuilder.append(INDENT);
		conditionBuilder.append("Input(name == \"");
		conditionBuilder.append(question.getName());
		conditionBuilder.append("\"");
	}

	/**
	 * Converts Actions from d3-web rules to drools actions.
	 * 
	 * @param d3Rule the StringBuilder of the drools rules
	 * @param droolsRule the currently processed d3-web rule
	 */
	private void convertAction(PSAction action, DroolsRule droolsRule) {
		if (action instanceof ActionHeuristicPS) convertHeuristicAction(droolsRule,
				(ActionHeuristicPS) action);
		else if (action instanceof ActionSetValue) convertSetValueAction(droolsRule,
				(ActionSetValue) action);
		else Logger.getLogger(this.getClass()).warn(
				"Unable to handle ActionType " + droolsRule.getAction().getClass());
	}

	/**
	 * Helper method which converts HeuristicActions of d3-web rules.
	 * 
	 * @param droolsRules the StringBuilder of the drools rules
	 * @param action the currently processed d3-web action
	 */
	private void convertHeuristicAction(DroolsRule droolsRule,
			ActionHeuristicPS action) {
		StringBuilder actionBuilder = new StringBuilder();
		actionBuilder.append(INDENT);
		actionBuilder.append(INDENT);
		actionBuilder.append("$solution : SolutionInput(name == \"");
		actionBuilder.append(action.getSolution().getName());
		actionBuilder.append("\")\n");
		actionBuilder.append(INDENT);
		actionBuilder.append("then\n");
		actionBuilder.append(INDENT);
		actionBuilder.append(INDENT);
		actionBuilder.append("$solution.setValue(");
		actionBuilder.append(action.getScore());
		actionBuilder.append(");\n");
		droolsRule.setAction(actionBuilder.toString());
	}

	/**
	 * Helper method which converts SetValueActions of d3-web rules.
	 * 
	 * @param droolsRule the StringBuilder of the drools rule
	 * @param action the currently processed d3-web action
	 */
	private void convertSetValueAction(DroolsRule droolsRule,
			ActionSetValue action) {
		StringBuilder actionBuilder = new StringBuilder();
		actionBuilder.append(INDENT);
		actionBuilder.append(INDENT);
		actionBuilder.append("$input : ChoiceInput(name == \"");
		actionBuilder.append(action.getQuestion());
		actionBuilder.append("\")\n");
		actionBuilder.append(INDENT);
		actionBuilder.append("then\n");
		actionBuilder.append(INDENT);
		actionBuilder.append(INDENT);
		actionBuilder.append("$input.setValue(");
		convertActionValue(actionBuilder, action.getValue());
		actionBuilder.append(");\n");
		droolsRule.setAction(actionBuilder.toString());
	}

	/**
	 * Helper method which converts the values of d3-web rules' actions.
	 * 
	 * @param droolsRule the StringBuilder of the drools rule
	 * @param value the currently processed action value
	 */
	private void convertActionValue(StringBuilder actionBuilder, Object value) {
		if (value instanceof Value) {
			actionBuilder.append("\"");
			actionBuilder.append(((Value) value).getValue());
			actionBuilder.append("\"");
		}
		else Logger.getLogger(this.getClass()).warn(
				"Unable to handle type of action value: " + value.getClass());
	}

}
