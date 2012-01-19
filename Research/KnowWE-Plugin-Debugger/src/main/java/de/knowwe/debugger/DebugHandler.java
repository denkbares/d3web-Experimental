/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.debugger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * @author dupke
 */
public class DebugHandler extends AbstractTagHandler {

	static final String AND = "← CondAnd";
	static final String OR = "← CondOr";
	static final String NOT = "← CondNot";
	static final String NUMGREATER = "← CondNumGreater";
	static final String NUMGREATEREQUAL = "← CondNumGreaterEqual";
	static final String NUMLESS = "← CondNumLess";
	static final String NUMLESSEQUAL = "← CondNumLessEqual";
	static final String STATE = "← CondDState";

	public DebugHandler() {
		super("debugger");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuffer buffer = new StringBuffer();
		String topic = userContext.getTitle();
		String web = userContext.getParameter(KnowWEAttributes.WEB);
		KnowledgeBase kb = D3webModule.getKnowledgeBase(web, topic);
		if (kb == null) return KnowWEUtils.maskHTML("Error: No knowledgebase found.");
		String kbid = kb.getId();
		SessionBroker broker = D3webModule.getBroker(userContext.getUserName(), web);
		Session session = broker.getSession(kbid);
		if (session == null) {
			kbid = KnowWEEnvironment.generateDefaultID(KnowWEEnvironment.WIKI_FINDINGS);
			session = broker.getSession(kbid);
		}
		// Answered questions
		List<Question> aqs = session.getBlackboard().getAnsweredQuestions();
		HashMap<String, String> facts = new HashMap<String, String>();
		for (Question q : aqs) {
				facts.put(q.getName(), session.getBlackboard().getValue(q).toString());
		}
		// Solutions
		Solution rootSolution = kb.getRootSolution();
		List<Solution> solutions = new LinkedList<Solution>();
		getAllSolutions(rootSolution, solutions);
		for (Solution s : solutions) {
			facts.put(s.getName(), session.getBlackboard().getRating(s).toString());
		}
		// Rules
		List<Rule> rules = getAllRules(kb);
		if (rules.size() == 0) return KnowWEUtils.maskHTML("Error: No rules declared.");
		/* DEBUGGER-DIV */
		buffer.append("<div id='debugger'>");
		// Rule -> Solution
		for (Solution s : solutions) {
			getTraceRendering(rules, s, new LinkedList<TerminologyObject>(), buffer, 0, session,
					facts);
		}

		buffer.append("</div>");

		return KnowWEUtils.maskHTML(buffer.toString());
	}

	/**
	 * Renders the trace from the solution to basic-rules.
	 * 
	 * @param rules all rules the knowledgebase contains
	 * @param to the object that starts the trace
	 * @param buffer the buffer which contains the rendering
	 * @param depth the count of steps needed to reach the object
	 * @param session the session
	 * @param facts the values of the answered questions
	 */
	private void getTraceRendering(List<Rule> rules, TerminologyObject to, List<TerminologyObject> checkedTOs, StringBuffer buffer, int depth, Session session, HashMap<String, String> facts) {
		List<TerminologyObject> conditionTOs = new LinkedList<TerminologyObject>();
		// If to already was checked -> return
		if (checkedTOs.contains(to)) return;
		else checkedTOs.add(to);
		// render to
		if (to instanceof Solution) {
			buffer.append("<div class='solution'>");
			buffer.append("<div class='indicator'>&nbsp;</div>");
			buffer.append("<span class='solution_"
					+ session.getBlackboard().getRating((Solution) to)
					+ "' >" + ": " + to
					+ " (" + session.getBlackboard().getRating((Solution) to) + ")</span>");
			buffer.append("<div id='debug_" + to + "' style='display:none' class='non-solutions'>");
		}
		else {
			buffer.append("<div class='single_object'><p style='font-weight:bold'>");
			for (int i = 0; i < depth; i++) {
				buffer.append("-");
			}
			buffer.append(" " + to + "</p>");
		}

		// If there are no rules for to
		if (getRulesWithTO(rules, to).size() == 0) {
			buffer.append("> No rules for  \"" + to + "\" declared.");

			if (!(to instanceof Solution)) {
				buffer.append("</div>");
			}
		}
		else {
			// render the rules that involve to in their action
			for (Rule r : getRulesWithTO(rules, to)) {
				buffer.append("<ul><li>");
				renderRule(r, buffer, facts, session);
				buffer.append("</li></ul>");
				for (TerminologyObject t : r.getCondition().getTerminalObjects()) {
					if (!conditionTOs.contains(t) & !checkedTOs.contains(t)) {
						conditionTOs.add(t);
					}
				}
			}

			if (!(to instanceof Solution)) {
				buffer.append("</div>");
			}

			// Render trace for every object in the condition of a rule before
			for (TerminologyObject t : conditionTOs) {
				getTraceRendering(rules, t, checkedTOs, buffer, depth + 1,
							session, facts);
			}
		}
		if (to instanceof Solution) {
			buffer.append("</div></div>");
		}
	}

	/**
	 * Render the condition and action of the given rule.
	 */
	private void renderRule(Rule r, StringBuffer buffer, HashMap<String, String> facts, Session session) {
		buffer.append("<p>");
		boolean canFire = renderCondition(r.getCondition().toString(), buffer, facts, session);

		buffer.append(" &rArr; ");
		// Render action
		if (canFire) {
			buffer.append("<span class='true'>"
					+ r.getAction() + "</span>");
		}
		else {
			buffer.append("<span class='false'>" + r.getAction()
					+ "</span>");
		}
		buffer.append("</p>");
	}

	/**
	 * Render the given condition
	 */
	private boolean renderCondition(String condition, StringBuffer buffer, HashMap<String, String> facts, Session session) {
		List<String> parts = new LinkedList<String>();
		// No need to check this extra with rule.canFire() if this method is
		// checking the condition-truth anyway
		boolean canFire = false;
		String isTrue = "<span class='true'>";
		String isFalse = "<span class='false'>";

		// Get the connector (not, and, or)
		if (condition.startsWith(NOT)) {
			condition = condition.replace(NOT, "");
			condition = condition.substring(2, condition.length() - 1);

			if (!evaluateCondition(condition, facts)) {
				buffer.append(isTrue);
				canFire = true;
			}
			else {
				buffer.append(isFalse);
				canFire = false;
			}

			buffer.append(" !( ");
			renderCondition(condition, buffer, facts, session);
			buffer.append(" ) </span>");
		}
		else if (condition.startsWith(AND)) {
			condition = condition.replaceFirst(AND, "");
			condition = condition.substring(2, condition.length() - 1);
			parts = getConditionParts(condition);
			canFire = evaluateCondition(parts.get(0), facts);
			for (int i = 1; i < parts.size(); i++) {
				canFire = canFire && evaluateCondition(parts.get(i), facts);
			}

			if (canFire) {
				buffer.append(isTrue);
			}
			else {
				buffer.append(isFalse);
			}

			for (int i = 0; i < parts.size(); i++) {
				if (i > 0) {
					buffer.append("AND");
				}
				buffer.append(" ( ");
				renderCondition(parts.get(i), buffer, facts, session);
				buffer.append(" ) ");
			}
			buffer.append("</span>");
		}
		else if (condition.startsWith(OR)) {
			condition = condition.replaceFirst(OR, "");
			condition = condition.substring(2, condition.length() - 1);
			parts = getConditionParts(condition);
			canFire = evaluateCondition(parts.get(0), facts);
			for (int i = 1; i < parts.size(); i++) {
				canFire = canFire || evaluateCondition(parts.get(i), facts);
			}

			if (canFire) {
				buffer.append(isTrue);
			}
			else {
				buffer.append(isFalse);
			}

			for (int i = 0; i < parts.size(); i++) {
				if (i > 0) {
					buffer.append("OR");
				}
				buffer.append(" ( ");
				renderCondition(parts.get(i), buffer, facts, session);
				buffer.append(" ) ");
			}
			buffer.append("</span>");
		}
		// Basic-condition
		else {
			// Edit num-condition(>=, >, <=, <) or state-condition
			if (condition.startsWith(NUMGREATEREQUAL)) {
				condition = condition.replaceFirst(NUMGREATEREQUAL + " question: ", "");
				condition = condition.replaceFirst("value:", ">");
			}
			else if (condition.startsWith(NUMGREATER)) {
				condition = condition.replaceFirst(NUMGREATER + " question: ", "");
				condition = condition.replaceFirst("value:", ">=");
			}
			else if (condition.startsWith(NUMLESSEQUAL)) {
				condition = condition.replaceFirst(NUMLESSEQUAL + " question: ", "");
				condition = condition.replaceFirst("value:", "<");
			}
			else if (condition.startsWith(NUMLESS)) {
				condition = condition.replaceFirst(NUMLESS + " question: ", "");
				condition = condition.replaceFirst("value:", "<=");
			}
			else if (condition.startsWith(STATE)) {
				condition = condition.replaceFirst(STATE + " diagnosis: ", "");
				condition = condition.replaceFirst("value:", "==");
			}

			if (evaluateCondition(condition, facts)) {
				canFire = true;
				buffer.append(isTrue);
			}
			else {
				canFire = false;
				buffer.append(isFalse);
			}
			// Render TO of condition
			// HashMap<String, TerminologyObject> qs = new HashMap<String,
			// TerminologyObject>();
			// getAllQuestions(session.getKnowledgeBase().getRootQASet(), qs);
			// TerminologyObject to = qs.get(condition.split(" == ")[0]);
			// if (to instanceof QuestionChoice) {
			// buffer.append(to.getName() + " ");
			// buffer.append("<select name='" + condition.split(" == ")[0] +
			// "' size='3'>");
			// for (Choice choice : ((QuestionChoice) to).getAllAlternatives())
			// {
			// buffer.append("<option>" + choice.getName() + "</option>");
			// }
			// buffer.append("</select>");
			// buffer.append(" == " + condition.split(" == ")[1]);
			// }
			// else {
			// buffer.append(condition + "</span>");
			// }
			buffer.append(condition + "</span>");
		}

		return canFire;
	}

	private HashMap<String, TerminologyObject> getAllQuestions(TerminologyObject to, HashMap<String, TerminologyObject> qs) {
		for (TerminologyObject q : to.getChildren()) {
			qs.put(q.getName(), q);
			if (q.getChildren().length > 0) {
				getAllQuestions(q, qs);
			}
		}
		return qs;
	}

	/**
	 * evaluate a condition.
	 */
	private boolean evaluateCondition(String condition, HashMap<String, String> facts) {
		List<String> parts = new LinkedList<String>();
		boolean canFire = false;

		// Walk recursively through the conditions text
		// Get the connector (not, and, or)
		if (condition.startsWith(NOT)) {
			condition = condition.replace(NOT, "");
			condition = condition.substring(2, condition.length() - 1);
			return !(evaluateCondition(condition, facts));
		}
		else if (condition.startsWith(AND)) {
			condition = condition.replaceFirst(AND, "");
			condition = condition.substring(2, condition.length() - 1);
			parts = getConditionParts(condition);
			canFire = evaluateCondition(parts.get(0), facts);
			for (int i = 1; i < parts.size(); i++) {
				canFire = canFire && evaluateCondition(parts.get(i), facts);
			}
			return canFire;
		}
		else if (condition.startsWith(OR)) {
			condition = condition.replaceFirst(OR, "");
			condition = condition.substring(2, condition.length() - 1);
			parts = getConditionParts(condition);
			canFire = evaluateCondition(parts.get(0), facts);
			for (int i = 1; i < parts.size(); i++) {
				canFire = canFire || evaluateCondition(parts.get(i), facts);
			}
			return canFire;
		}
		// Prepare basic-conditions
		else {
			// num-condition(>=, >, <=, <) or state-condition
			if (condition.startsWith(NUMGREATEREQUAL)) {
				condition = condition.replaceFirst(NUMGREATEREQUAL + " question: ", "");
				condition = condition.replaceFirst("value:", ">");
			}
			else if (condition.startsWith(NUMGREATER)) {
				condition = condition.replaceFirst(NUMGREATER + " question: ", "");
				condition = condition.replaceFirst("value:", ">=");
			}
			else if (condition.startsWith(NUMLESSEQUAL)) {
				condition = condition.replaceFirst(NUMLESSEQUAL + " question: ", "");
				condition = condition.replaceFirst("value:", "<");
			}
			else if (condition.startsWith(NUMLESS)) {
				condition = condition.replaceFirst(NUMLESS + " question: ", "");
				condition = condition.replaceFirst("value:", "<=");
			}
			else if (condition.startsWith(STATE)) {
				condition = condition.replaceFirst(STATE + " diagnosis: ", "");
				condition = condition.replaceFirst("value:", "==");
			}
		}

		// Evaluate basic-condition
		try {
			if (condition.contains("==")) {
				String fact = facts.get(condition.split(" == ")[0]);
				// Check for MC-Questions
				if (fact.startsWith("[") && fact.endsWith("]")) {
					fact = fact.substring(1, fact.length() - 1);
					for (String s : fact.split(", ")) {
						if (s.equals(condition.split(" == ")[1])) return true;
					}
					return false;
				}
				// OC-Question or Solution
				return fact.equals(condition.split(" == ")[1]);
			}
			else if (condition.contains(">=")) {
				return Double.parseDouble(facts.get(condition.split(" >= ")[0])) >= Double.parseDouble(condition.split(" >= ")[1]);
			}
			else if (condition.contains(">")) {
				return Double.parseDouble(facts.get(condition.split(" > ")[0])) > Double.parseDouble(condition.split(" > ")[1]);
			}
			else if (condition.contains("<=")) {
				return Double.parseDouble(facts.get(condition.split(" <= ")[0])) <= Double.parseDouble(condition.split(" <= ")[1]);
			}
			else if (condition.contains("<")) {
				return Double.parseDouble(facts.get(condition.split(" < ")[0])) < Double.parseDouble(condition.split(" < ")[1]);
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	/**
	 * divides the condition in two part.
	 */
	private List<String> getConditionParts(String condition) {
		List<String> parts = new LinkedList<String>();
		int skip = 0;
		int lastPos = -2;

		for (int i = 0; i < condition.length(); i++) {
			if (skip == 0 && condition.charAt(i) == ';') {
				parts.add(condition.substring(lastPos + 2, i));
				lastPos = i;
			}
			else if (condition.charAt(i) == '{') {
				skip++;
			}
			else if (condition.charAt(i) == '}') {
				skip--;
			}
		}
		parts.add(condition.substring(lastPos + 2));

		return parts;
	}

	/**
	 * Get all rules containing the object.
	 */
	private List<Rule> getRulesWithTO(List<Rule> rules, TerminologyObject to) {
		List<Rule> rulesWithTO = new LinkedList<Rule>();
		for (Rule r : rules) {
			if (!rulesWithTO.contains(r) && ruleActionContainsTO(r, to)) rulesWithTO.add(r);
		}

		return rulesWithTO;
	}

	/**
	 * Checks whether the rule's action
	 */
	private boolean ruleActionContainsTO(Rule r, TerminologyObject to) {
		return r.getAction().getBackwardObjects().contains(to);
	}

	/**
	 * Get all solutions.
	 */
	public void getAllSolutions(TerminologyObject rootSolution, List<Solution> solutions) {
		for (TerminologyObject s : rootSolution.getChildren()) {
			solutions.add((Solution) s);
			if (s.getChildren().length > 0) {
				getAllSolutions(s, solutions);
			}
		}
	}

	/**
	 * Get all rules.
	 */
	public List<Rule> getAllRules(KnowledgeBase kb) {
		List<Rule> rules = new LinkedList<Rule>();
		for (KnowledgeSlice ks : kb.getAllKnowledgeSlices()) {
			if (ks instanceof RuleSet) {
				for (Rule r : ((RuleSet) ks).getRules()) {
					if (!rules.contains(r)) rules.add(r);
				}
			}
		}

		return rules;
	}
}
