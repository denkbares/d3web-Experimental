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
package de.knowwe.d3web.debugger.renderer;

import java.util.List;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondQuestion;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.Conjunct;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.condition.Disjunct;
import de.d3web.we.kdom.condition.NegatedExpression;
import de.d3web.we.kdom.condition.helper.BracedCondition;
import de.d3web.we.kdom.condition.helper.BracedConditionContent;
import de.d3web.we.kdom.rule.ConditionArea;
import de.d3web.we.kdom.rules.RuleContentType;
import de.d3web.we.kdom.rules.action.RuleAction;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.object.SolutionReference;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * Renders rules to provide a debugging-layout.
 * 
 * @author dupke
 */
public class DebuggerRuleRenderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user,
			RenderResult string) {
		Article article = KnowWEUtils.getCompilingArticles(sec).iterator().next();
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(article.getWeb(), article.getTitle());
		Session session = SessionProvider.getSession(user, kb);
		Section<RuleAction> ruleAction = Sections.findSuccessor(sec,
				RuleAction.class);
		Rule r = null;
		if (ruleAction != null) {
			r = (Rule) KnowWEUtils.getStoredObject(article, ruleAction,
					RuleContentType.ruleStoreKey);
		}
		String title = user.getTitle();

		List<Section<? extends Type>> ruleSections = sec.getChildren();

		if (r != null && session != null) {
			if (r.hasFired(session)) {
				string.appendHtml("<div class='ruleContentFired' ruleid='"
						+ r.hashCode() + "'>");
			}
			else {
				string.appendHtml("<div class='ruleContent' ruleid='"
						+ r.hashCode()
						+ "'>");
			}
			for (Section<? extends Type> section : ruleSections) {
				if (section.get() instanceof ConditionArea) {
					// condition
					renderConditionSection(section, r.getCondition(), session, title, false,
							string, user);
				}
				else {
					DelegateRenderer.getInstance().renderSubSection(section, user, string);
				}
			}
			string.appendHtml("</div>");
		}
		else {
			DelegateRenderer.getInstance().render(sec, user, string);
		}
	}

	public static void renderConditionSection(Section<?> condSection, Condition cond, Session session, String title, boolean inside, RenderResult string, UserContext user) {
		List<Section<? extends Type>> children = condSection.getChildren();
		for (Section<? extends Type> section : children) {
			if (section.get() instanceof CompositeCondition) {
				renderCondition(Sections.cast(section, CompositeCondition.class), cond,
						session, title, inside, string, user);
			}
			else {
				DelegateRenderer.getInstance().renderSubSection(section, user, string);
			}
		}
	}

	/**
	 * Get the rendering for a condition.
	 * 
	 * @param inside If the rule is display inside the debugger, it has to use
	 *        different js-functions.
	 */
	public static void renderCondition(Section<CompositeCondition> condSection, Condition cond, Session session, String title, boolean inside, RenderResult string, UserContext user) {

		// open div for cond
		try {
			if (cond.eval(session)) string.appendHtml("<div class='condTrue'>");
			else string.appendHtml("<div class='condFalse'>");
		}
		catch (NoAnswerException e) {
			string.appendHtml("<div class='condUndefined'>");
		}
		catch (UnknownAnswerException e) {
			string.appendHtml("<div class='condUnknown'>");
		}

		// handle div
		List<Section<? extends Type>> children = condSection.getChildren();
		if (Sections.findChildOfType(condSection, BracedCondition.class) != null) {

			for (Section<? extends Type> child : children) {
				if (child.get() instanceof BracedCondition) {
					renderBracedCondition(child, cond, session, title, inside,
							string, user);
				}
				else {
					DelegateRenderer.getInstance().renderSubSection(child, user, string);
				}
			}
		}
		else if (cond instanceof NonTerminalCondition) {

			List<Condition> terms = ((NonTerminalCondition) cond).getTerms();
			int condIndex = 0;
			for (Section<? extends Type> section : children) {

				if (section.get() instanceof Disjunct ||
						section.get() instanceof Conjunct
						|| section.get() instanceof NegatedExpression) {
					renderConditionSection(section, terms.get(condIndex), session, title, inside,
							string, user);
					condIndex++;
				}
				else {
					DelegateRenderer.getInstance().renderSubSection(section, user, string);
				}
			}
		}
		else {
			// renderTerminalCondition
			if (cond instanceof TerminalCondition) {
				if (condSection.get() instanceof CompositeCondition) {
					renderTerminalCondition(
							condSection.getChildren().get(0), cond, session,
							title,
							inside, string, user);
				}
				else {
					// not happening ?!
					DelegateRenderer.getInstance().renderSubSection(condSection, user, string);
				}
			}
			else {
				// happening for ExpressionConditions which are neither
				// TerminalConditions nor NonTerminalConditions

				// DelegateRenderer.getInstance().renderSubSection(condSection,
				// user, string);
				renderTerminalCondition(
						condSection.getChildren().get(0), cond, session,
						title,
						inside, string, user);
			}
		}

		// close div for cond
		string.appendHtml("</div>");

	}

	private static void renderBracedCondition(Section<? extends Type> section, Condition condition, Session session, String title, boolean inside, RenderResult string, UserContext user) {
		List<Section<? extends Type>> children = section.getChildren();
		for (Section<? extends Type> child : children) {
			if (child.get() instanceof BracedConditionContent) {
				List<Section<? extends Type>> contentChildren = child.getChildren();
				for (Section<? extends Type> contentChild : contentChildren) {
					if (contentChild.get() instanceof CompositeCondition) {
						renderCondition(Sections.cast(contentChild, CompositeCondition.class),
								condition, session, title, inside, string, user);
					}
					else {
						DelegateRenderer.getInstance().renderSubSection(contentChild, user, string);
					}

				}
			}
			else {
				DelegateRenderer.getInstance().renderSubSection(child, user, string);
			}
		}

	}

	private static void renderTerminalCondition(Section<?> condSection, Condition cond, Session session, String title, boolean inside, RenderResult builder, UserContext user) {

		List<Section<? extends Type>> children = condSection.getChildren();
		for (Section<? extends Type> child : children) {
			if (child.get() instanceof D3webCondition) {
				List<Section<? extends Type>> grandChildren = child.getChildren();
				for (Section<? extends Type> grandChild : grandChildren) {
					if (grandChild.get() instanceof QuestionReference) {
						if (cond instanceof CondQuestion) {
							DebuggerQuestionRenderer.renderQuestion(
									((CondQuestion) cond).getQuestion(),
									session, title, inside, builder);
						}
					}
					else if (grandChild.get() instanceof SolutionReference) {
						if (cond instanceof CondDState) {
							builder.appendHtml("<span class='debuggerSolution'>");
							builder.append(((CondDState) cond).getSolution());
							builder.appendHtml("</span>");
						}
					}
					else {
						Renderer renderer = grandChild.get().getRenderer();

						if (renderer != null) {
							renderMaskJSPWikiMarkup(renderer, grandChild, user, builder);
						}
						else {
							delegateMaskJSPWikiMarkup(child, user, builder);
						}
					}
				}
			}
			else {
				delegateMaskJSPWikiMarkup(child, user, builder);
			}
		}

	}

	/**
	 * 
	 * @created 24.10.2012
	 * @param child
	 * @param user
	 * @param builder
	 */
	private static void delegateMaskJSPWikiMarkup(Section<? extends Type> child, UserContext user, RenderResult builder) {
		RenderResult buffy = new RenderResult(builder);
		DelegateRenderer.getInstance().renderSubSection(child, user, buffy);
		builder.appendJSPWikiMarkup(buffy);
	}

	/**
	 * 
	 * @created 24.10.2012
	 * @param child
	 * @param user
	 * @param builder
	 */
	private static void renderMaskJSPWikiMarkup(Renderer r, Section<? extends Type> child, UserContext user, RenderResult builder) {
		RenderResult buffy = new RenderResult(builder);
		r.render(child, user, buffy);
		builder.appendJSPWikiMarkup(buffy);
	}
}
