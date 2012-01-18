/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.debugger;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * 
 * @author dupke
 */
public class DebugUtils {

	static final String AND = "← CondAnd";
	static final String OR = "← CondOr";
	static final String NOT = "← CondNot";
	static final String NUMGREATER = "← CondNumGreater";
	static final String NUMGREATEREQUAL = "← CondNumGreaterEqual";
	static final String NUMLESS = "← CondNumLess";
	static final String NUMLESSEQUAL = "← CondNumLessEqual";

	/**
	 * Render questions.
	 */
	public static void getQuestionsRenderingRecursively(TerminologyObject root, StringBuffer buffer, int depth, Session session) {
		String margin = "margin-left:" + depth * 20 + "px;";
		for (TerminologyObject q : root.getChildren()) {

			if (q instanceof QContainer) {
				buffer.append("<p style='" + margin + "font-weight:bold'>" + q.getName() + "</p>");
			}
			else {
				if (q instanceof QuestionOC) {
					renderQuestionOC((QuestionChoice) q, session, buffer, depth);
				}
				else if (q instanceof QuestionMC) {
					renderQuestionMC((QuestionMC) q, session, buffer, depth);
				}
				else if (q instanceof QuestionNum) {
					renderQuestionNum((QuestionNum) q, session, buffer, depth);
				}
				else if (q instanceof QuestionText) {
					renderQuestionText((QuestionText) q, session, buffer, depth);
				}
				else if (q instanceof QuestionDate) {
					// renderQuestionDate();
				}
			}

			if (q.getChildren().length > 0) {
				getQuestionsRenderingRecursively(q, buffer, depth + 1,
						session);
			}
				
		}
	}

	/**
	 * render oneChoice-question.
	 */
	private static void renderQuestionOC(QuestionChoice q, Session session, StringBuffer buffer, int depth) {
		String marked;
		buffer.append("<p style='margin-left:" + depth * 20 + "px;'>" + q.getName() + " <> ");
		Value value = session.getBlackboard().getValue(q);

		if (value != null) {
			for (Choice choice : q.getAllAlternatives()) {
				buffer.append(" | ");

				marked = (UndefinedValue.isNotUndefinedValue(value)
						&& isAnsweredinCase(value, new ChoiceValue(choice)))
								? "color:green;font-weight:bold;"
								: "";

				buffer.append("<span style='" + marked + "'>" + choice.getName() + "</span>");
				buffer.append(" |");
			}
		}
		buffer.append("</p>");
	}

	/**
	 * Render multipleChoice-question.
	 */
	private static void renderQuestionMC(QuestionMC q, Session session, StringBuffer buffer, int depth) {
		buffer.append("<p style='margin-left:" + depth * 20 + "px'>" + q.getName() + " <> ");
		String marked;
		Value value = session.getBlackboard().getValue(q);

		if (value != null) {
			for (Choice choice : q.getAllAlternatives()) {
				buffer.append(" | ");

				marked = (UndefinedValue.isNotUndefinedValue(value)
						&& isAnsweredinCase(value, new ChoiceValue(choice)))
								? "color:green;font-weight:bold" : "";

				buffer.append("<span style='" + marked + "'>" + choice.getName() + "</span>");
				buffer.append(" |");
			}
		}
		buffer.append("</p>");
	}

	/**
	 * Render numeric-question.
	 */
	private static void renderQuestionNum(QuestionNum q, Session session, StringBuffer buffer, int depth) {
		buffer.append("<p style='margin-left:" + depth * 20 + "px'>" + q.getName() + " <> ");
		Value answer = session.getBlackboard().getValue(q);
		if (UndefinedValue.isUndefinedValue(answer)) {
			buffer.append(" undefined");
		}
		else if (answer != null && answer instanceof Unknown) {
			buffer.append(" unknown");
		}
		else {
			buffer.append(" " + answer);
		}
		buffer.append("</p>");
	}

	/**
	 * Render text-question.
	 */
	private static void renderQuestionText(QuestionText q, Session session, StringBuffer buffer, int depth) {
		buffer.append("<p style='margin-left:" + depth * 20 + "px'>" + q.getName() + " <> ");
		Value answer = session.getBlackboard().getValue(q);
		if (answer != null && answer instanceof Unknown) {
			buffer.append(" unknown");
		}
		else {
			buffer.append(" " + answer);
		}
		buffer.append("</p>");
	}

	/**
	 * Render solutions.
	 */
	public static void getSolutionsRenderingRecursively(TerminologyObject rootSolution, StringBuffer buffer, Set<TerminologyObject> processedTOs, int depth, List<Solution> list) {
		String margin = "margin-left:" + depth * 20 + "px;";
		String font_weight;
		for (TerminologyObject s : rootSolution.getChildren()) {
			font_weight = (list.contains(s) || depth == 0) ? "font-weight:bold;" : "";

			if (depth == 0) buffer.append("<p style='" + margin + font_weight + "'>" + s.getName()
					+ "</p>");
			if (depth > 0) buffer.append("<p style='" + margin + font_weight + "'>"
					+ s.getName() + "</p>");
			if (s.getChildren().length > 0) {
				getSolutionsRenderingRecursively(s, buffer, processedTOs, depth + 1, list);
			}
		}
	}

	/**
	 * Render rules.
	 */
	public static void getRulesRendering(KnowledgeBase kb, Session session, StringBuffer buffer) {
		List<Rule> list = new LinkedList<Rule>();
		for (KnowledgeSlice ks : kb.getAllKnowledgeSlices()) {
			if (ks instanceof RuleSet) {
				for (Rule r : ((RuleSet) ks).getRules()) {
					if (!list.contains(r)) {
						list.add(r);
						buffer.append("<p>" + r + "</p>");
					}
				}
			}
		}
	}

	/**
	 * Checks, whether an answer value was already processed in the current
	 * session
	 * 
	 * @param sessionValue the sessionValue
	 * @param value the value to be checked
	 * @return true if the given session value contains the checked value (MC
	 *         Questions) or if the session value equals the value
	 */
	private static boolean isAnsweredinCase(Value sessionValue, Value value) {
		// test for MC values separately
		if (sessionValue instanceof MultipleChoiceValue) {
			return ((MultipleChoiceValue) sessionValue).contains(value);
		}
		else {
			return sessionValue.equals(value);
		}
	}

	/**
	 * Get all rules.
	 */
	public static List<Rule> getAllRules(KnowledgeBase kb) {
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