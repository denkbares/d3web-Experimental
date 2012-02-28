/*
 * Copyright (C) 2012 Unersity Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.debugger.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.knowwe.d3web.debugger.DebugUtilities;
import de.knowwe.d3web.debugger.renderer.DebuggerQuestionRenderer;

/**
 * A data type for rule's condition in the debugger.
 * 
 * @author dupke
 */
public class DebuggerRuleCondition implements Condition {

	/** Some information */
	private final Collection<? extends TerminologyObject> conditionObjects;
	private final String conditionText;
	private List<DebuggerRuleCondition> subConditions;
	private String connector;
	private boolean complex;
	/** Truth of the condition. undefined: -1, unknown: 0, false: 1, true: 2 */

	public DebuggerRuleCondition(Condition condition) {
		this(condition.getTerminalObjects(), condition.toString());
	}

	public DebuggerRuleCondition(Collection<? extends TerminologyObject> conditionObjects, String conditionText) {
		this.conditionObjects = conditionObjects;
		this.conditionText = conditionText;
		split();
	}

	public boolean contains(TerminologyObject to) {
		return conditionObjects.contains(to);
	}

	@Override
	public boolean eval(Session session) {
		return evaluateForRendering(session) == 1;
	}

	public List<DebuggerRuleCondition> getSubconditions() {
		return subConditions;
	}

	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		return conditionObjects;
	}

	public boolean isComplex() {
		return complex;
	}

	/**
	 * Get HTML-output with the session's context.
	 */
	public String render(Session session, String web, String topic, boolean inside) {
		StringBuffer buffer = new StringBuffer();
		int truth = evaluateForRendering(session);

		if (truth == 1) buffer.append("<div class='" + DebugUtilities.COND_TRUE + "'>");
		else if (truth == -1) buffer.append("<div class='" + DebugUtilities.COND_FALSE + "'>");
		else if (truth == 0) buffer.append("<div class='" + DebugUtilities.COND_UNKNOWN + "'>");
		else buffer.append("<div class='" + DebugUtilities.COND_UNDEFINED + "'>");
		

		if (complex) {
			if (connector.equals(DebugUtilities.DEBUG_AND)
					|| connector.equals(DebugUtilities.DEBUG_OR)) {
				for (int i = 0; i < subConditions.size(); i++) {
					if (i > 0) buffer.append(connector);

					buffer.append(" ( "
							+ subConditions.get(i).render(session, web, topic, inside)
							+ " ) ");
				}
			}
			else if (connector.equals(DebugUtilities.DEBUG_NOT)) {
				buffer.append(" " + connector + "( "
						+ subConditions.get(0).render(session, web, topic, inside)
						+ " ) ");
			}
		}
		// Basic condition
		else {
			// Its basic, only one TerminologyObject left in condition
			TerminologyObject to = (TerminologyObject) getTerminalObjects().toArray()[0];
			String basicCond = transformBasicConditionText(conditionText);
			if (to instanceof Question) {
				if (connector.equals(DebugUtilities.DEBUG_KNOWN)) buffer.append(connector + "[");

				buffer.append(DebuggerQuestionRenderer.renderQuestion(
						(Question) to, session,
						topic,
						web, inside));

				if (connector.equals(DebugUtilities.DEBUG_KNOWN)) buffer.append("]");
				else
				buffer.append(basicCond.substring(basicCond.indexOf(connector) - 1));
			}
			else if (to instanceof Solution) {
				buffer.append("<span class='debuggerActionSolution'>" + to + "</span>");
				buffer.append(basicCond.substring(basicCond.indexOf(connector) - 1));
			}
			else buffer.append(basicCond);
		}

		buffer.append("</div>");

		return buffer.toString();
	}

	@Override
	public String toString() {
		return conditionText;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DebuggerRuleCondition other = (DebuggerRuleCondition) obj;
		if (conditionText == null) {
			if (other.conditionText != null) return false;
		}
		else if (!conditionText.equals(other.conditionText)) return false;
		return true;
	}

	/**
	 * Unknown: 0, true: 1, false: -1, undefined: 2012
	 */
	public int evaluateForRendering(Session session) {
		int eval, counter = 0;
		TerminologyObject to;
		String value, objValue;
		
		if (complex) {
			if (connector.equals(DebugUtilities.DEBUG_AND)) {
				for (DebuggerRuleCondition dc : subConditions) {
					eval = dc.evaluateForRendering(session);
					if (eval == 0) return 0;
					if (eval == 2012) return 2012;
					counter += eval;
				}
				// No -1 added => counter = number of subconditions
				if (counter == subConditions.size()) return 1;
			}
			else if (connector.equals(DebugUtilities.DEBUG_OR)) {
				for (DebuggerRuleCondition dc : subConditions) {
					eval = dc.evaluateForRendering(session);
					if (eval == 0) return 0;
					if (eval == 2012) return 2012;
					counter += eval;
				}
				// Any 1 added => counter*(-1) < number of subconditions
				if (counter * (-1) < subConditions.size()) return 1;
			}
			else if (connector.equals(DebugUtilities.DEBUG_NOT)) {
				return subConditions.get(0).evaluateForRendering(session) * (-1);
			}
		}
		// Basic condition
		else {
			// Its basic, only one TerminologyObject left in condition
			to = (TerminologyObject) getTerminalObjects().toArray()[0];
			if (to instanceof Solution) value = session.getBlackboard().getRating((Solution) to).toString();
			else if (to instanceof Question) value = session.getBlackboard().getValue((Question) to).toString();
			else return -1;
			if (value.equals("-?-")) return 0;
			if (value.equals("Undefined")) return 2012;
			if (connector.equals(DebugUtilities.DEBUG_KNOWN)) {
				if (!value.equals("")) return 1;
				else return -1;
			}
			objValue = transformBasicConditionText(conditionText).split(" " + connector + " ")[1];

			if (connector.equals(DebugUtilities.DEBUG_NUMGREATEREQUAL)
					&& Double.parseDouble(value) >= Double.parseDouble(objValue)) return 1;
			else if (connector.equals(DebugUtilities.DEBUG_NUMGREATER)
					&& Double.parseDouble(value) > Double.parseDouble(objValue)) return 1;
			else if (connector.equals(DebugUtilities.DEBUG_NUMLESSEQUAL)
					&& Double.parseDouble(value) <= Double.parseDouble(objValue)) return 1;
			else if (connector.equals(DebugUtilities.DEBUG_NUMLESS)
					&& Double.parseDouble(value) < Double.parseDouble(objValue)) return 1;
			else if (to instanceof QuestionMC) {
				value = value.substring(1, value.length() - 1);
				for (String v : value.split(", "))
					if (objValue.equals(v)) return 1;
			}
			else if (connector.equals(DebugUtilities.DEBUG_STATE) && objValue.equals(value)) return 1;
		}
	
		return -1;
	}

	/**
	 * Needs to be called when the condition needs to be split (AND, OR).
	 * Returns the TerminologyObjects of the condition's part.
	 */
	private Collection<? extends TerminologyObject> getObjectsOfConditionPart(String conditionPart) {
		List<TerminologyObject> conditionPartObjects = new LinkedList<TerminologyObject>();
	
		for (TerminologyObject to : conditionObjects) {
			if (conditionPart.contains(to.getName() + " ==")
					|| conditionPart.contains("diagnosis: " + to.getName())
					|| conditionPart.contains("question: " + to.getName())) {
				conditionPartObjects.add(to);
			}
		}
	
		return conditionPartObjects;
	}

	private String transformBasicConditionText(String conditionText) {
		String basicCond = conditionText;
		if (connector.equals(DebugUtilities.DEBUG_NUMGREATEREQUAL)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.NUMGREATEREQUAL + " question: ",
					"");
			basicCond = basicCond.replaceFirst("value:", DebugUtilities.DEBUG_NUMGREATEREQUAL);
		}
		else if (connector.equals(DebugUtilities.DEBUG_NUMGREATER)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.NUMGREATER + " question: ", "");
			basicCond = basicCond.replaceFirst("value:", DebugUtilities.DEBUG_NUMGREATER);
		}
		else if (connector.equals(DebugUtilities.DEBUG_NUMLESSEQUAL)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.NUMLESSEQUAL + " question: ", "");
			basicCond = basicCond.replaceFirst("value:", DebugUtilities.DEBUG_NUMLESSEQUAL);
		}
		else if (connector.equals(DebugUtilities.DEBUG_NUMLESS)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.NUMLESS + " question: ", "");
			basicCond = basicCond.replaceFirst("value:", DebugUtilities.DEBUG_NUMLESS);
		}
		else if (connector.equals(DebugUtilities.DEBUG_STATE)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.STATE + " diagnosis: ", "");
			basicCond = basicCond.replaceFirst("value:", DebugUtilities.DEBUG_STATE);
		}
		else if (connector.equals(DebugUtilities.DEBUG_KNOWN)) {
			basicCond = basicCond.replaceFirst(DebugUtilities.KNOWN + " question: ", "");
		}
		return basicCond;
	}

	private void split() {
		String conditionText = this.conditionText;
		List<String> subConds;

		// Complex condition (AND, OR, NOT)
		if (conditionText.startsWith(DebugUtilities.AND)) {
			complex = true;
			connector = DebugUtilities.DEBUG_AND;
			conditionText = conditionText.replaceFirst(DebugUtilities.AND, "");
			conditionText = conditionText.substring(2, conditionText.length() - 1);
			subConds = splitConditionText(conditionText);
			subConditions = new ArrayList<DebuggerRuleCondition>();

			for (String s : subConds)
				subConditions.add(new DebuggerRuleCondition(getObjectsOfConditionPart(s), s));
		}
		else if (conditionText.startsWith(DebugUtilities.OR)) {
			complex = true;
			connector = DebugUtilities.DEBUG_OR;
			conditionText = conditionText.replaceFirst(DebugUtilities.OR, "");
			conditionText = conditionText.substring(2, conditionText.length() - 1);
			subConds = splitConditionText(conditionText);
			subConditions = new ArrayList<DebuggerRuleCondition>();

			for (String s : subConds)
				subConditions.add(new DebuggerRuleCondition(getObjectsOfConditionPart(s), s));
		}
		else if (conditionText.startsWith(DebugUtilities.NOT)) {
			complex = true;
			connector = DebugUtilities.DEBUG_NOT;
			conditionText = conditionText.replaceFirst(DebugUtilities.NOT, "");
			conditionText = conditionText.substring(2, conditionText.length() - 1);
			subConditions = new ArrayList<DebuggerRuleCondition>();
			
			subConditions.add(new DebuggerRuleCondition(conditionObjects, conditionText));
		}
		// Basic condition (==, >=, >, <=, <, State)
		else {
			complex = false;
			if (conditionText.startsWith(DebugUtilities.NUMGREATEREQUAL)) {
				connector = DebugUtilities.DEBUG_NUMGREATEREQUAL;
			}
			else if (conditionText.startsWith(DebugUtilities.NUMGREATER)) {
				connector = DebugUtilities.DEBUG_NUMGREATER;
			}
			else if (conditionText.startsWith(DebugUtilities.NUMLESSEQUAL)) {
				connector = DebugUtilities.DEBUG_NUMLESSEQUAL;
			}
			else if (conditionText.startsWith(DebugUtilities.NUMLESS)) {
				connector = DebugUtilities.DEBUG_NUMLESS;
			}
			else if (conditionText.startsWith(DebugUtilities.STATE)) {
				connector = DebugUtilities.DEBUG_STATE;
			}
			else if (conditionText.startsWith(DebugUtilities.KNOWN)) {
				connector = DebugUtilities.DEBUG_KNOWN;
			}
			else {
				connector = "==";
			}
		}

	}

	/**
	 * Find the right index of ';' to split the condition's text into two parts.
	 * (can't use Stringmethod ".split(regex)" because of complex conditions
	 * having more than one semicolon;
	 */
	private List<String> splitConditionText(String conditionText) {
		List<String> parts = new LinkedList<String>();
		int skip = 0;
		int lastPos = -2;

		for (int i = 0; i < conditionText.length(); i++) {
			if (skip == 0 && conditionText.charAt(i) == ';') {
				parts.add(conditionText.substring(lastPos + 2, i));
				lastPos = i;
			}
			else if (conditionText.charAt(i) == '{') {
				skip++;
			}
			else if (conditionText.charAt(i) == '}') {
				skip--;
			}
		}
		parts.add(conditionText.substring(lastPos + 2));

		return parts;
	}

}
