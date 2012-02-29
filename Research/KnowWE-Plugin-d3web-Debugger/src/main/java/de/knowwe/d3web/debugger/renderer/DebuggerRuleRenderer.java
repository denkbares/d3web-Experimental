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
package de.knowwe.d3web.debugger.renderer;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondAnswered;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.CondTextQuestion;
import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.we.kdom.rules.RuleContentType;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * Renders rules to provide a debugging-layout.
 * 
 * @author dupke
 */
public class DebuggerRuleRenderer implements Renderer{

	/** connectors */
	private final String AND = "AND";
	private final String OR = "OR";
	private final String NOT = "NOT";
	private final String UNKNOWN = "??";
	/** basic connectors */
	private final String GREATER = ">";
	private final String GREATER_EQUAL = ">=";
	private final String LESS = "<";
	private final String LESS_EQUAL = "<=";
	private final String EQUAL = "==";

	@Override
	public void render(Section<?> sec, UserContext user,
			StringBuilder string) {
		KnowWEArticle article = KnowWEUtils.getCompilingArticles(sec).iterator().next();
		Session session = D3webUtils.getSession(article.getTitle(), user,
				article.getWeb());
		Section<RuleAction> ruleAction = Sections.findSuccessor(sec,
				RuleAction.class);
		Rule r = null;
		if (ruleAction != null) {
			r = (Rule) KnowWEUtils.getStoredObject(article, ruleAction,
					RuleContentType.ruleStoreKey);
		}
		StringBuffer buffer = new StringBuffer();
		String title = user.getTitle();

		if (r.hasFired(session)) buffer.append("<div class='ruleContentFired' ruleid='"
				+ r.hashCode() + "'>");
		else buffer.append("<div class='ruleContent' ruleid='" + r.hashCode() + "'>");

		// condition
		buffer.append("IF " + renderCondition(r.getCondition(), session, title, false) + "<br />");
		// action
		System.out.println("---------------");
		System.out.println(r);
		System.out.println("---------------");
		buffer.append("THEN " + renderAction(r.getAction()));
		buffer.append("</div>");

		string.append(KnowWEUtils.maskHTML(buffer.toString()));
	}

	/**
	 * Get the rendering for a condition.
	 * 
	 * @param inside If the rule is display inside the debugger, it has to use
	 *        different js-functions.
	 */
	public String renderCondition(Condition cond, Session session, String title, boolean inside) {
		StringBuffer buffer = new StringBuffer();
		String connector = getConnector(cond);

		try {
			if (cond.eval(session)) buffer.append("<div class='condTrue'>");
			else buffer.append("<div class='condFalse'>");
		}
		catch (NoAnswerException e) {
			buffer.append("<div class='condUndefined'>");
		}
		catch (UnknownAnswerException e) {
			buffer.append("<div class='condUnknown'>");
		}

		if (connector.equals(NOT)) buffer.append(NOT);

		if (cond instanceof NonTerminalCondition) {
			for (Condition c : ((NonTerminalCondition) cond).getTerms()) {
				buffer.append("( " + renderCondition(c, session, title, inside) + " )");
				buffer.append(" " + connector + " ");
			}
			// delete last connector
			buffer = buffer.delete(buffer.length() - connector.length() - 2, buffer.length());
		}
		else {
			// renderTerminalCondition
			buffer.append(renderTerminalCondition(cond, session, title, inside));
		}

		buffer.append("</div>");

		return buffer.toString();
	}

	/**
	 * Get the rendering for an action.
	 */
	public String renderAction(PSAction action) {
		StringBuffer buffer = new StringBuffer();

		if (action instanceof ActionHeuristicPS) {
			ActionHeuristicPS ac = (ActionHeuristicPS) action;
			buffer.append("<span class='debuggerSolution'>" + ac.getSolution().getName()
					+ "</span> = " + ac.getScore());
		}
		else if (action instanceof ActionContraIndication) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionSuppressAnswer) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionInstantIndication) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionNextQASet) {
			ActionNextQASet anq = (ActionNextQASet) action;
			for (int i = 0; i < anq.getQASets().size(); i++) {
				if (i < anq.getQASets().size() - 1) buffer.append("<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName() + "</span>, ");
				else buffer.append("<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName()
						+ "</span>");
			}
		}
		else if (action instanceof ActionSetValue) {
			ActionSetValue asv = (ActionSetValue) action;
			buffer.append("<span class='debuggerAction'>" + asv.getQuestion()
					+ "</span> = <span class='debuggerValue'>"
					+ asv.getValue() + "</span>");
		}
		else {
			buffer.append(action.toString());
		}

		return buffer.toString();
	}

	private String renderTerminalCondition(Condition cond, Session session, String title, boolean inside) {
		StringBuffer buffer = new StringBuffer();

		if (cond instanceof CondDState) {
			buffer.append("<span class='debuggerSolution'>" + ((CondDState) cond).getSolution()
					+ "</span> == " + ((CondDState) cond).getStatus());
		}
		else if (cond instanceof CondAnswered) {
			buffer.append("ANSWERED[");
			buffer.append(DebuggerQuestionRenderer.renderQuestion(
					((CondAnswered) cond).getQuestion(),
							session, title, inside));
			buffer.append("]");
		}
		else if (cond instanceof CondKnown) {
			buffer.append("KNOWN[");
			buffer.append(DebuggerQuestionRenderer.renderQuestion(((CondKnown) cond).getQuestion(),
					session, title, inside));
			buffer.append("]");
		}
		else if (cond instanceof CondUnknown) {
			buffer.append("UNKNOWN[");
			buffer.append(DebuggerQuestionRenderer.renderQuestion(
					((CondUnknown) cond).getQuestion(),
					session, title, inside));
			buffer.append("]");
		}
		else if (cond instanceof CondEqual) {
			buffer.append(DebuggerQuestionRenderer.renderQuestion(((CondEqual) cond).getQuestion(),
					session, title, inside));
			buffer.append(" == ");
			buffer.append(((CondEqual) cond).getValue());
		}
		else if (cond instanceof CondTextQuestion) {
			buffer.append(DebuggerQuestionRenderer.renderQuestion(
					((CondTextQuestion) cond).getQuestion(),
					session, title, inside));
			buffer.append(" == ");
			buffer.append(((CondTextQuestion) cond).getValue());
		}
		else if (cond instanceof CondNum) {
			buffer.append(DebuggerQuestionRenderer.renderQuestion(((CondNum) cond).getQuestion(),
					session, title, inside));
			buffer.append(" " + getBasicConnector((CondNum) cond) + " ");
			buffer.append(((CondNum) cond).getConditionValue());
		}
		else {
			buffer.append(cond);
		}

		return buffer.toString();
	}

	/**
	 * Get the condition's connector.
	 */
	private String getConnector(Condition cond) {
		if (cond instanceof CondAnd) return AND;
		if (cond instanceof CondOr) return OR;
		if (cond instanceof CondNot) return NOT;

		return UNKNOWN;
	}

	private String getBasicConnector(CondNum cond) {
		if (cond instanceof CondNumGreaterEqual) return GREATER_EQUAL;
		if (cond instanceof CondNumGreater) return GREATER;
		if (cond instanceof CondNumLessEqual) return LESS_EQUAL;
		if (cond instanceof CondNumLess) return LESS;
		if (cond instanceof CondNumEqual) return EQUAL;

		return UNKNOWN;
	}
}
